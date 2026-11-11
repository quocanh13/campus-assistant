import { useState, type ReactNode } from 'react'
import type { User } from '../api/types'
import { navForRole, type PageKey } from '../navigation'
import { initials } from '../utils/format'
import { Icon } from './Icon'

interface AppShellProps {
  user: User
  page: PageKey
  onNavigate: (page: PageKey) => void
  onLogout: () => void
  children: ReactNode
}

export function AppShell({ user, page, onNavigate, onLogout, children }: AppShellProps) {
  const [mobileOpen, setMobileOpen] = useState(false)
  const entries = navForRole(user.role)
  const current = entries.find((entry) => entry.key === page)

  function go(next: PageKey) {
    onNavigate(next)
    setMobileOpen(false)
  }

  return (
    <div className="app-shell">
      {mobileOpen && (
        <button
          type="button"
          className="sidebar-scrim"
          aria-label="Đóng menu"
          onClick={() => setMobileOpen(false)}
        />
      )}

      <aside className={`sidebar ${mobileOpen ? 'sidebar-open' : ''}`}>
        <div className="brand">
          <span className="brand-mark">
            <Icon name="spark" size={20} />
          </span>
          <span className="brand-text">
            <strong>Campus</strong>
            <small>Assistant</small>
          </span>
        </div>

        <nav className="nav" aria-label="Điều hướng chính">
          {entries.map((entry) => (
            <button
              key={entry.key}
              type="button"
              className={`nav-item ${entry.key === page ? 'nav-item-active' : ''}`}
              onClick={() => go(entry.key)}
            >
              <span className="nav-icon">
                <Icon name={entry.icon} size={18} />
              </span>
              <span className="nav-copy">
                <strong>{entry.label}</strong>
                <small>{entry.hint}</small>
              </span>
            </button>
          ))}
        </nav>

        <div className="sidebar-foot">
          <div className="user-card">
            <span className="avatar">{initials(user.email.split('@')[0].replace(/[._-]+/g, ' '))}</span>
            <span className="user-copy">
              <strong>{user.email}</strong>
              <small>{user.role === 'ADMIN' ? 'Quản trị viên' : 'Sinh viên'}</small>
            </span>
          </div>
          <button type="button" className="btn btn-ghost btn-block" onClick={onLogout}>
            <Icon name="logout" size={16} />
            Đăng xuất
          </button>
        </div>
      </aside>

      <div className="main">
        <header className="topbar">
          <div className="topbar-left">
            <button
              type="button"
              className="icon-btn only-mobile"
              onClick={() => setMobileOpen(true)}
              aria-label="Mở menu"
            >
              <Icon name="menu" />
            </button>
            <div className="topbar-title">
              <span className="topbar-eyebrow">{user.role === 'ADMIN' ? 'Khu vực quản trị' : 'Không gian học tập'}</span>
              <strong>{current?.label ?? 'Campus Assistant'}</strong>
            </div>
          </div>
          <div className="topbar-right">
            <span className={`role-chip role-${user.role.toLowerCase()}`}>
              {user.role === 'ADMIN' ? 'ADMIN' : 'STUDENT'}
            </span>
          </div>
        </header>

        <main className="content">{children}</main>
      </div>
    </div>
  )
}
