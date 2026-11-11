import { create } from 'zustand'
import { api, errorMessage } from '../api/client'
import type { AgentAnswer, Conversation } from '../api/types'
import { useAuthStore } from './authStore'
import { notify } from './uiStore'

export interface ChatMessageView {
  id: string
  role: 'STUDENT' | 'ASSISTANT'
  content: string
  createdAt: string
  meta?: AgentAnswer
}

const REPLY_CACHE_KEY = 'campus-agent-replies'

type ReplyCache = Record<string, ChatMessageView[]>

function readReplyCache(): ReplyCache {
  try {
    const raw = localStorage.getItem(REPLY_CACHE_KEY)
    if (!raw) return {}
    const parsed = JSON.parse(raw) as ReplyCache
    return parsed && typeof parsed === 'object' ? parsed : {}
  } catch {
    return {}
  }
}

function writeReplyCache(cache: ReplyCache) {
  try {
    localStorage.setItem(REPLY_CACHE_KEY, JSON.stringify(cache))
  } catch {
    // storage might be full or disabled; replies simply won't be cached
  }
}

function saveReply(conversationId: string, reply: ChatMessageView) {
  const cache = readReplyCache()
  const current = cache[conversationId] ?? []
  cache[conversationId] = [...current, reply]
  writeReplyCache(cache)
}

function dropReplies(conversationId: string) {
  const cache = readReplyCache()
  if (cache[conversationId]) {
    delete cache[conversationId]
    writeReplyCache(cache)
  }
}

function mergeMessages(history: ChatMessageView[], replies: ChatMessageView[]): ChatMessageView[] {
  const merged = [...history, ...replies]
  merged.sort((a, b) => {
    const left = new Date(a.createdAt).getTime()
    const right = new Date(b.createdAt).getTime()
    if (Number.isNaN(left) || Number.isNaN(right)) return 0
    return left - right
  })
  return merged
}

function localId(prefix: string): string {
  if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
    return `${prefix}-${crypto.randomUUID()}`
  }
  return `${prefix}-${Date.now()}-${Math.random().toString(16).slice(2)}`
}

interface ChatState {
  conversations: Conversation[]
  activeId: string | null
  messages: ChatMessageView[]
  loadingConversations: boolean
  loadingMessages: boolean
  sending: boolean
  error: string | null
  loadConversations: () => Promise<void>
  selectConversation: (id: string) => Promise<void>
  startNewConversation: () => void
  send: (text: string) => Promise<void>
  renameConversation: (id: string, title: string) => Promise<void>
  deleteConversation: (id: string) => Promise<void>
  reset: () => void
}

export const useChatStore = create<ChatState>((set, get) => ({
  conversations: [],
  activeId: null,
  messages: [],
  loadingConversations: false,
  loadingMessages: false,
  sending: false,
  error: null,

  loadConversations: async () => {
    set({ loadingConversations: true, error: null })
    try {
      const conversations = await api.conversations.list()
      set({ conversations })
      if (conversations.length > 0) {
        useAuthStore.getState().setStudentId(conversations[0].studentId)
      }
      const activeId = get().activeId
      if (!activeId && conversations.length > 0) {
        await get().selectConversation(conversations[0].id)
      }
    } catch (error) {
      const message = errorMessage(error)
      set({ error: message })
    } finally {
      set({ loadingConversations: false })
    }
  },

  selectConversation: async (id) => {
    set({ activeId: id, loadingMessages: true, error: null })
    try {
      const history = await api.messages.list(id)
      const replies = readReplyCache()[id] ?? []
      const messages: ChatMessageView[] = mergeMessages(
        history.map((message) => ({
          id: message.id,
          role: message.role,
          content: message.content,
          createdAt: message.createdAt,
        })),
        replies,
      )
      set({ messages })
      const conversation = get().conversations.find((item) => item.id === id)
      if (conversation) {
        useAuthStore.getState().setStudentId(conversation.studentId)
      }
    } catch (error) {
      set({ error: errorMessage(error), messages: [] })
    } finally {
      set({ loadingMessages: false })
    }
  },

  startNewConversation: () => {
    set({ activeId: null, messages: [], error: null })
  },

  send: async (text) => {
    const content = text.trim()
    if (!content || get().sending) return

    set({ sending: true, error: null })

    try {
      let conversation = get().conversations.find((item) => item.id === get().activeId) ?? null

      if (!conversation) {
        conversation = await api.conversations.create(content.slice(0, 80))
        set((state) => ({
          conversations: [conversation as Conversation, ...state.conversations],
          activeId: conversation!.id,
          messages: [],
        }))
      }

      useAuthStore.getState().setStudentId(conversation.studentId)

      const optimistic: ChatMessageView = {
        id: localId('student'),
        role: 'STUDENT',
        content,
        createdAt: new Date().toISOString(),
      }
      set((state) => ({ messages: [...state.messages, optimistic] }))

      try {
        const saved = await api.messages.create(conversation.id, content)
        set((state) => ({
          messages: state.messages.map((message) =>
            message.id === optimistic.id
              ? {
                  id: saved.id,
                  role: saved.role,
                  content: saved.content,
                  createdAt: saved.createdAt,
                }
              : message,
          ),
        }))
      } catch (error) {
        notify.error(`Không lưu được tin nhắn: ${errorMessage(error)}`)
      }

      const answer = await api.chat(conversation.id, content, conversation.studentId)
      const reply: ChatMessageView = {
        id: localId('agent'),
        role: 'ASSISTANT',
        content: answer.answer || 'Xin lỗi, tôi chưa có câu trả lời cho câu hỏi này.',
        createdAt: new Date().toISOString(),
        meta: answer,
      }

      set((state) => ({ messages: [...state.messages, reply] }))
      saveReply(conversation.id, reply)
    } catch (error) {
      const message = errorMessage(error)
      set({ error: message })
      notify.error(message)
    } finally {
      set({ sending: false })
    }
  },

  renameConversation: async (id, title) => {
    const trimmed = title.trim()
    if (!trimmed) return
    try {
      const updated = await api.conversations.patch(id, trimmed)
      set((state) => ({
        conversations: state.conversations.map((item) => (item.id === id ? updated : item)),
      }))
      notify.success('Đã đổi tên cuộc trò chuyện')
    } catch (error) {
      notify.error(errorMessage(error))
    }
  },

  deleteConversation: async (id) => {
    try {
      await api.conversations.remove(id)
      dropReplies(id)
      set((state) => ({
        conversations: state.conversations.filter((item) => item.id !== id),
        activeId: state.activeId === id ? null : state.activeId,
        messages: state.activeId === id ? [] : state.messages,
      }))
      notify.success('Đã xoá cuộc trò chuyện')
    } catch (error) {
      notify.error(errorMessage(error))
    }
  },

  reset: () => set({ conversations: [], activeId: null, messages: [], error: null }),
}))
