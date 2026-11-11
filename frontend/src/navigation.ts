import type { Role } from './api/types'

export type PageKey =
  | 'chat'
  | 'documents'
  | 'majors'
  | 'units'
  | 'students'
  | 'admins'
  | 'profile'

export interface NavItem {
  key: PageKey
  label: string
  hint: string
  icon:
    | 'chat'
    | 'document'
    | 'major'
    | 'unit'
    | 'student'
    | 'admin'
    | 'profile'
}

export interface NavEntry extends NavItem {
  roles: Role[]
}

export const NAV_ENTRIES: NavEntry[] = [
  {
    key: 'chat',
    label: 'Trợ lý ảo',
    hint: 'Hỏi đáp cùng AI',
    icon: 'chat',
    roles: ['STUDENT'],
  },
  {
    key: 'documents',
    label: 'Tài liệu',
    hint: 'Kho tài liệu học vụ',
    icon: 'document',
    roles: ['STUDENT', 'ADMIN'],
  },
  {
    key: 'majors',
    label: 'Ngành học',
    hint: 'Chương trình đào tạo',
    icon: 'major',
    roles: ['STUDENT', 'ADMIN'],
  },
  {
    key: 'units',
    label: 'Đơn vị',
    hint: 'Khoa / trường trực thuộc',
    icon: 'unit',
    roles: ['ADMIN'],
  },
  {
    key: 'students',
    label: 'Sinh viên',
    hint: 'Hồ sơ sinh viên',
    icon: 'student',
    roles: ['ADMIN'],
  },
  {
    key: 'admins',
    label: 'Quản trị viên',
    hint: 'Tài khoản vận hành',
    icon: 'admin',
    roles: ['ADMIN'],
  },
  {
    key: 'profile',
    label: 'Tài khoản',
    hint: 'Thông tin cá nhân',
    icon: 'profile',
    roles: ['STUDENT', 'ADMIN'],
  },
]

export function navForRole(role: Role): NavEntry[] {
  return NAV_ENTRIES.filter((entry) => entry.roles.includes(role))
}

export function isPageKey(value: string): value is PageKey {
  return NAV_ENTRIES.some((entry) => entry.key === value)
}

export function readPageFromHash(): string {
  const raw = window.location.hash.replace(/^#\/?/, '').trim()
  return raw || 'chat'
}

export function navigate(page: PageKey) {
  window.location.hash = `#/${page}`
}

export function defaultPageForRole(role: Role): PageKey {
  return role === 'ADMIN' ? 'documents' : 'chat'
}
