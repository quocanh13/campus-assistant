import type {
  Account,
  Admin,
  AgentAnswer,
  BaseResponse,
  Conversation,
  DocumentItem,
  LoginResult,
  Major,
  Message,
  Student,
  Unit,
} from './types'

const CORE_PREFIX = '/api'
const AGENT_PREFIX = '/agent'

let accessToken: string | null = null

export function setAccessToken(token: string | null) {
  accessToken = token
}

export class ApiError extends Error {
  status: number
  code?: string
  detail?: { field: string; message: string }[]

  constructor(
    message: string,
    status: number,
    code?: string,
    detail?: { field: string; message: string }[],
  ) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
    this.detail = detail
  }
}

export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.detail && error.detail.length > 0) {
      return error.detail.map((item) => item.message).join(', ')
    }
    return error.message
  }
  if (error instanceof Error) {
    return error.message
  }
  return 'Đã có lỗi xảy ra, vui lòng thử lại'
}

async function request<T>(prefix: string, path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers)
  const isForm = init.body instanceof FormData

  if (accessToken) {
    headers.set('Authorization', `Bearer ${accessToken}`)
  }
  if (init.body && !isForm && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  let response: Response
  try {
    response = await fetch(`${prefix}${path}`, { ...init, headers })
  } catch {
    throw new ApiError('Không kết nối được tới server', 0)
  }

  const text = await response.text()
  let payload: BaseResponse<T> | null = null
  if (text) {
    try {
      payload = JSON.parse(text) as BaseResponse<T>
    } catch {
      payload = null
    }
  }

  if (!response.ok) {
    if (response.status === 401 && accessToken) {
      window.dispatchEvent(new Event('campus:unauthorized'))
    }
    throw new ApiError(
      payload?.message ?? `Lỗi ${response.status}`,
      response.status,
      payload?.code,
      payload?.detail,
    )
  }

  return (payload?.data ?? null) as T
}

const core = <T,>(path: string, init?: RequestInit) => request<T>(CORE_PREFIX, path, init)
const agent = <T,>(path: string, init?: RequestInit) => request<T>(AGENT_PREFIX, path, init)

const json = (body: unknown): RequestInit => ({
  method: 'POST',
  body: JSON.stringify(body),
})

const patch = (body: unknown): RequestInit => ({
  method: 'PATCH',
  body: JSON.stringify(body),
})

export const api = {
  login: (email: string, password: string) =>
    core<LoginResult>('/auth/login', json({ email, password })),

  accounts: {
    get: (id: string) => core<Account>(`/accounts/${id}`),
    create: (body: { email: string; password: string; role: 'STUDENT' | 'ADMIN' }) =>
      core<Account>('/accounts', json(body)),
  },

  students: {
    get: (id: number) => core<Student>(`/students/${id}`),
    create: (body: { id: number; account_id: string; name: string; cohort: number; major: string }) =>
      core<Student>('/students', json(body)),
    patch: (id: number, body: { name?: string; cohort?: number; major?: string }) =>
      core<Student>(`/students/${id}`, patch(body)),
    remove: (id: number) => core<void>(`/students/${id}`, { method: 'DELETE' }),
  },

  admins: {
    list: (index = 0, limit = 10) => core<Admin[]>(`/admins?index=${index}&limit=${limit}`),
    create: (body: { id: number; account_id: string; name: string }) =>
      core<Admin>('/admins', json(body)),
    patch: (id: number, body: { name: string }) => core<Admin>(`/admins/${id}`, patch(body)),
    remove: (id: number) => core<void>(`/admins/${id}`, { method: 'DELETE' }),
  },

  documents: {
    list: (index = 0, limit = 10) =>
      core<DocumentItem[]>(`/documents?index=${index}&limit=${limit}`),
    count: () => core<number>('/documents/count'),
    get: (id: string) => core<DocumentItem>(`/documents/${id}`),
    upload: (form: FormData) => core<{ id: string; name?: string }>('/documents', {
      method: 'POST',
      body: form,
    }),
    patch: (id: string, body: Record<string, unknown>) =>
      core<DocumentItem>(`/documents/${id}`, patch(body)),
    remove: (id: string) => core<DocumentItem>(`/documents/${id}`, { method: 'DELETE' }),
    file: async (id: string, download: boolean): Promise<Blob> => {
      const headers = new Headers()
      if (accessToken) {
        headers.set('Authorization', `Bearer ${accessToken}`)
      }
      const response = await fetch(
        `${CORE_PREFIX}/documents/${id}/${download ? 'download' : 'view'}`,
        { headers },
      )
      if (!response.ok) {
        let message = `Lỗi ${response.status}`
        try {
          const payload = (await response.json()) as BaseResponse<unknown>
          message = payload.message ?? message
        } catch {
          // keep the default message
        }
        throw new ApiError(message, response.status)
      }
      return response.blob()
    },
  },

  majors: {
    list: () => core<Major[]>('/majors'),
    get: (code: string) => core<Major>(`/majors/${code}`),
    create: (body: {
      code: string
      name: string
      instruction_language: string
      unit_code: string
      program_type?: string
    }) => core<Major>('/majors', json(body)),
    patch: (
      code: string,
      body: {
        name?: string
        instruction_language?: string
        unit_code?: string
        program_type?: string
      },
    ) => core<Major>(`/majors/${code}`, patch(body)),
    remove: (code: string) => core<void>(`/majors/${code}`, { method: 'DELETE' }),
  },

  units: {
    list: () => core<Unit[]>('/units'),
    create: (body: { code: string; name: string }) => core<Unit>('/units', json(body)),
    remove: (code: string) => core<void>(`/units/${code}`, { method: 'DELETE' }),
  },

  conversations: {
    list: () => core<Conversation[]>('/student-agent-conversations'),
    get: (id: string) => core<Conversation>(`/student-agent-conversations/${id}`),
    create: (title?: string) =>
      core<Conversation>('/student-agent-conversations', json(title ? { title } : {})),
    patch: (id: string, title: string) =>
      core<Conversation>(`/student-agent-conversations/${id}`, patch({ title })),
    remove: (id: string) =>
      core<void>(`/student-agent-conversations/${id}`, { method: 'DELETE' }),
  },

  messages: {
    list: (conversationId: string) =>
      core<Message[]>(`/student-agent-conversations/${conversationId}/messages`),
    create: (conversationId: string, content: string) =>
      core<Message>(`/student-agent-conversations/${conversationId}/messages`, json({ content })),
  },

  chat: (conversationId: string, message: string, studentCode: number) =>
    agent<AgentAnswer>(`/conversations/${conversationId}`, json({
      message,
      student_code: studentCode,
    })),
}
