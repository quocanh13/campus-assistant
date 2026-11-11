export type Role = 'STUDENT' | 'ADMIN'
export type MessageRole = 'STUDENT' | 'ASSISTANT'

export interface FieldError {
  field: string
  message: string
}

export interface BaseResponse<T> {
  code: string
  message?: string
  detail?: FieldError[]
  data: T
}

export interface User {
  id: string
  email: string
  role: Role
}

export interface LoginResult {
  token: string
  user: User
}

export interface Account {
  id: string
  email: string
  role: Role
  createdAt?: string
}

export interface Student {
  id: number
  accountId: string
  name: string
  cohort: number
  major: string
  createdAt: string
}

export interface Admin {
  id: number
  accountId: string
  name: string
  createdAt: string
}

export interface DocumentItem {
  id: string
  name: string
  description: string | null
  mimeType: string
  size: number
  userId: string
  uploadedAt: string
  validAt: string | null
  expriedAt: string | null
}

export interface Major {
  code: string
  name: string
  instructionLanguage: string | null
  programType: string | null
  unitCode: string
}

export interface Unit {
  code: string
  name: string
}

export interface Conversation {
  id: string
  title: string
  studentId: number
  createdAt: string
  updatedAt: string
}

export interface Message {
  id: string
  conversationId: string
  role: MessageRole
  content: string
  createdAt: string
}

export interface RelatedDocument {
  document_id: string
  chunk_indexes: number[]
}

export interface ToolData {
  tool_name: string
  data: unknown
}

export interface AgentAnswer {
  answer: string
  related_document: RelatedDocument[]
  tool_data: ToolData[]
}
