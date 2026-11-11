import { useEffect, useState } from 'react'
import { AppShell } from './components/AppShell'
import { Toasts } from './components/Toasts'
import {
  defaultPageForRole,
  isPageKey,
  navigate,
  navForRole,
  readPageFromHash,
  type PageKey,
} from './navigation'
import { useAuthStore } from './store/authStore'
import { useChatStore } from './store/chatStore'
import { notify } from './store/uiStore'
import { LoginPage } from './pages/LoginPage'
import { ChatPage } from './pages/ChatPage'
import { DocumentsPage } from './pages/DocumentsPage'
import { MajorsPage } from './pages/MajorsPage'
import { UnitsPage } from './pages/UnitsPage'
import { StudentsPage } from './pages/StudentsPage'
import { AdminsPage } from './pages/AdminsPage'
import { ProfilePage } from './pages/ProfilePage'

function initialPage(): PageKey {
  const raw = readPageFromHash()
  return isPageKey(raw) ? raw : 'chat'
}

export default function App() {
  const token = useAuthStore((state) => state.token)
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)
  const resetChat = useChatStore((state) => state.reset)

  const [page, setPage] = useState<PageKey>(initialPage)

  useEffect(() => {
    const onHashChange = () => {
      const raw = readPageFromHash()
      if (isPageKey(raw)) setPage(raw)
    }
    window.addEventListener('hashchange', onHashChange)
    return () => window.removeEventListener('hashchange', onHashChange)
  }, [])

  useEffect(() => {
    const onUnauthorized = () => {
      if (useAuthStore.getState().token) {
        logout()
        resetChat()
        notify.error('Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại')
      }
    }
    window.addEventListener('campus:unauthorized', onUnauthorized)
    return () => window.removeEventListener('campus:unauthorized', onUnauthorized)
  }, [logout, resetChat])

  useEffect(() => {
    if (!user) return
    const allowed = navForRole(user.role).some((entry) => entry.key === page)
    if (!allowed) {
      const fallback = defaultPageForRole(user.role)
      navigate(fallback)
      setPage(fallback)
    }
  }, [user, page])

  function handleNavigate(next: PageKey) {
    navigate(next)
    setPage(next)
  }

  function handleLogout() {
    logout()
    resetChat()
    notify.info('Đã đăng xuất')
  }

  if (!token || !user) {
    return (
      <>
        <LoginPage />
        <Toasts />
      </>
    )
  }

  function renderPage(key: PageKey) {
    switch (key) {
      case 'chat':
        return <ChatPage />
      case 'documents':
        return <DocumentsPage />
      case 'majors':
        return <MajorsPage />
      case 'units':
        return <UnitsPage />
      case 'students':
        return <StudentsPage />
      case 'admins':
        return <AdminsPage />
      case 'profile':
        return <ProfilePage />
      default:
        return <DocumentsPage />
    }
  }

  return (
    <>
      <AppShell user={user} page={page} onNavigate={handleNavigate} onLogout={handleLogout}>
        {renderPage(page)}
      </AppShell>
      <Toasts />
    </>
  )
}
