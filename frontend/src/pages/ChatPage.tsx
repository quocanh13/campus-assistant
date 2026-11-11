import { useEffect, useRef, useState, type FormEvent, type KeyboardEvent } from 'react'
import { useChatStore } from '../store/chatStore'
import { useAuthStore } from '../store/authStore'
import { Icon } from '../components/Icon'
import { Modal } from '../components/Modal'
import { ConfirmDialog } from '../components/ConfirmDialog'
import { EmptyState } from '../components/Page'
import { MessageContent } from '../components/MessageContent'
import { formatDateTime } from '../utils/format'

const SUGGESTIONS = [
  'Thông tin của tôi gồm những gì?',
  'Ngành CNTT khoa học máy tính học bằng ngôn ngữ gì?',
  'Có tài liệu nào về quy chế đào tạo không?',
  'Hôm nay là ngày bao nhiêu?',
]

export function ChatPage() {
  const conversations = useChatStore((state) => state.conversations)
  const activeId = useChatStore((state) => state.activeId)
  const messages = useChatStore((state) => state.messages)
  const loadingConversations = useChatStore((state) => state.loadingConversations)
  const loadingMessages = useChatStore((state) => state.loadingMessages)
  const sending = useChatStore((state) => state.sending)
  const error = useChatStore((state) => state.error)
  const loadConversations = useChatStore((state) => state.loadConversations)
  const selectConversation = useChatStore((state) => state.selectConversation)
  const startNewConversation = useChatStore((state) => state.startNewConversation)
  const send = useChatStore((state) => state.send)
  const renameConversation = useChatStore((state) => state.renameConversation)
  const deleteConversation = useChatStore((state) => state.deleteConversation)

  const user = useAuthStore((state) => state.user)

  const [input, setInput] = useState('')
  const [renameTarget, setRenameTarget] = useState<string | null>(null)
  const [renameValue, setRenameValue] = useState('')
  const [deleteTarget, setDeleteTarget] = useState<string | null>(null)
  const [busy, setBusy] = useState(false)
  const startedRef = useRef(false)
  const endRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (startedRef.current) return
    startedRef.current = true
    void loadConversations()
  }, [loadConversations])

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' })
  }, [messages.length, sending])

  const active = conversations.find((item) => item.id === activeId) ?? null

  async function sendCurrent() {
    if (!input.trim() || sending) return
    const text = input
    setInput('')
    await send(text)
  }

  function submit(event: FormEvent) {
    event.preventDefault()
    void sendCurrent()
  }

  function onComposerKeyDown(event: KeyboardEvent<HTMLTextAreaElement>) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      void sendCurrent()
    }
  }

  function beginRename(id: string) {
    const conversation = conversations.find((item) => item.id === id)
    setRenameTarget(id)
    setRenameValue(conversation?.title ?? '')
  }

  async function confirmRename() {
    if (!renameTarget) return
    setBusy(true)
    await renameConversation(renameTarget, renameValue)
    setBusy(false)
    setRenameTarget(null)
  }

  async function confirmDelete() {
    if (!deleteTarget) return
    setBusy(true)
    await deleteConversation(deleteTarget)
    setBusy(false)
    setDeleteTarget(null)
  }

  return (
    <div className="chat-page">
      <aside className="chat-sidebar">
        <div className="chat-sidebar-head">
          <div>
            <span className="page-eyebrow">Trợ lý AI</span>
            <h2>Cuộc trò chuyện</h2>
          </div>
          <button
            type="button"
            className="icon-btn icon-btn-solid"
            onClick={startNewConversation}
            aria-label="Cuộc trò chuyện mới"
            title="Cuộc trò chuyện mới"
          >
            <Icon name="plus" size={18} />
          </button>
        </div>

        <div className="conversation-list">
          {loadingConversations && <p className="list-hint">Đang tải…</p>}
          {!loadingConversations && conversations.length === 0 && (
            <p className="list-hint">Chưa có cuộc trò chuyện nào. Hãy bắt đầu bằng một câu hỏi.</p>
          )}
          {conversations.map((conversation) => (
            <div
              key={conversation.id}
              className={`conversation-item ${conversation.id === activeId ? 'conversation-active' : ''}`}
            >
              <button
                type="button"
                className="conversation-main"
                onClick={() => void selectConversation(conversation.id)}
              >
                <span className="conversation-icon">
                  <Icon name="chat" size={16} />
                </span>
                <span className="conversation-copy">
                  <strong>{conversation.title}</strong>
                  <small>{formatDateTime(conversation.updatedAt)}</small>
                </span>
              </button>
              <span className="conversation-actions">
                <button
                  type="button"
                  className="mini-btn"
                  onClick={() => beginRename(conversation.id)}
                  aria-label="Đổi tên"
                  title="Đổi tên"
                >
                  <Icon name="edit" size={14} />
                </button>
                <button
                  type="button"
                  className="mini-btn mini-btn-danger"
                  onClick={() => setDeleteTarget(conversation.id)}
                  aria-label="Xoá"
                  title="Xoá"
                >
                  <Icon name="trash" size={14} />
                </button>
              </span>
            </div>
          ))}
        </div>

        <div className="chat-sidebar-foot">
          <Icon name="info" size={14} />
          <span>Trợ lý trả lời dựa trên dữ liệu học vụ của trường.</span>
        </div>
      </aside>

      <section className="chat-main">
        <header className="chat-head">
          <div>
            <h1>{active?.title ?? 'Cuộc trò chuyện mới'}</h1>
            <p>
              {active
                ? `Mã sinh viên ${active.studentId} · cập nhật ${formatDateTime(active.updatedAt)}`
                : `Xin chào ${user?.email ?? ''}, bạn cần hỗ trợ điều gì?`}
            </p>
          </div>
          {active && (
            <div className="chat-head-actions">
              <button type="button" className="btn btn-ghost" onClick={() => beginRename(active.id)}>
                <Icon name="edit" size={15} />
                Đổi tên
              </button>
              <button type="button" className="btn btn-ghost" onClick={() => setDeleteTarget(active.id)}>
                <Icon name="trash" size={15} />
                Xoá
              </button>
            </div>
          )}
        </header>

        {error && (
          <div className="alert alert-error chat-alert">
            <Icon name="info" size={16} />
            <span>{error}</span>
          </div>
        )}

        <div className="message-scroll">
          {loadingMessages && <p className="list-hint">Đang tải tin nhắn…</p>}

          {!loadingMessages && messages.length === 0 && (
            <EmptyState
              icon={<Icon name="spark" size={26} />}
              title="Bắt đầu cuộc trò chuyện"
              message="Hỏi về thông tin sinh viên, ngành học, tài liệu hoặc bất kỳ điều gì liên quan đến học vụ."
              action={
                <div className="suggestions">
                  {SUGGESTIONS.map((suggestion) => (
                    <button
                      key={suggestion}
                      type="button"
                      className="suggestion"
                      onClick={() => void send(suggestion)}
                      disabled={sending}
                    >
                      {suggestion}
                    </button>
                  ))}
                </div>
              }
            />
          )}

          {messages.map((message) => (
            <article
              key={message.id}
              className={`message ${message.role === 'STUDENT' ? 'message-student' : 'message-agent'}`}
            >
              <span className="message-avatar">
                {message.role === 'STUDENT' ? (
                  <Icon name="profile" size={16} />
                ) : (
                  <Icon name="spark" size={16} />
                )}
              </span>
              <div className="message-body">
                <div className={`message-bubble ${message.role === 'ASSISTANT' ? 'agent-bubble' : ''}`}>
                  {message.role === 'ASSISTANT' ? (
                    <MessageContent text={message.content} />
                  ) : (
                    message.content
                  )}
                </div>
                <div className="message-meta">
                  <span>{message.role === 'STUDENT' ? 'Bạn' : 'Trợ lý'}</span>
                  <span>{formatDateTime(message.createdAt)}</span>
                </div>

                {message.meta && message.meta.related_document.length > 0 && (
                  <div className="source-list">
                    <span className="source-label">
                      <Icon name="document" size={13} />
                      Nguồn tham khảo
                    </span>
                    {message.meta.related_document.map((source) => (
                      <span key={source.document_id} className="source-chip">
                        {source.document_id.slice(0, 8)}… · đoạn {source.chunk_indexes.join(', ')}
                      </span>
                    ))}
                  </div>
                )}

                {message.meta && message.meta.tool_data.length > 0 && (
                  <details className="tool-details">
                    <summary>
                      <Icon name="info" size={13} />
                      Dữ liệu công cụ ({message.meta.tool_data.length})
                    </summary>
                    <pre>{JSON.stringify(message.meta.tool_data, null, 2)}</pre>
                  </details>
                )}
              </div>
            </article>
          ))}

          {sending && (
            <article className="message message-agent">
              <span className="message-avatar">
                <Icon name="spark" size={16} />
              </span>
              <div className="message-body">
                <div className="message-bubble typing">
                  <span />
                  <span />
                  <span />
                </div>
              </div>
            </article>
          )}

          <div ref={endRef} />
        </div>

        <form className="composer" onSubmit={submit}>
          <textarea
            value={input}
            onChange={(event) => setInput(event.target.value)}
            onKeyDown={onComposerKeyDown}
            placeholder="Nhập câu hỏi của bạn… (Enter để gửi, Shift + Enter để xuống dòng)"
            rows={2}
            disabled={sending}
          />
          <button
            type="submit"
            className="btn btn-primary composer-send"
            disabled={!input.trim() || sending}
            aria-label="Gửi tin nhắn"
          >
            <Icon name="send" size={18} />
          </button>
        </form>
      </section>

      <Modal
        open={renameTarget !== null}
        title="Đổi tên cuộc trò chuyện"
        onClose={() => setRenameTarget(null)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setRenameTarget(null)}>
              Huỷ
            </button>
            <button type="button" className="btn btn-primary" onClick={confirmRename} disabled={busy || !renameValue.trim()}>
              {busy ? 'Đang lưu…' : 'Lưu'}
            </button>
          </>
        }
      >
        <label className="field">
          <span>Tên cuộc trò chuyện</span>
          <input
            value={renameValue}
            onChange={(event) => setRenameValue(event.target.value)}
            maxLength={200}
            autoFocus
            onKeyDown={(event) => {
              if (event.key === 'Enter') void confirmRename()
            }}
          />
        </label>
      </Modal>

      <ConfirmDialog
        open={deleteTarget !== null}
        title="Xoá cuộc trò chuyện"
        message="Toàn bộ tin nhắn của cuộc trò chuyện này sẽ bị xoá và không thể khôi phục."
        busy={busy}
        onClose={() => setDeleteTarget(null)}
        onConfirm={confirmDelete}
      />
    </div>
  )
}
