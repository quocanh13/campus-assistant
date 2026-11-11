import { useEffect, useState, type FormEvent } from 'react'
import { api, errorMessage } from '../api/client'
import type { Account, Admin, Major, Student } from '../api/types'
import { useAuthStore } from '../store/authStore'
import { notify } from '../store/uiStore'
import { PageHeader, EmptyState } from '../components/Page'
import { Icon } from '../components/Icon'
import { Modal } from '../components/Modal'
import { formatDate, formatDateTime, initials } from '../utils/format'

export function ProfilePage() {
  const user = useAuthStore((state) => state.user)
  const loginAt = useAuthStore((state) => state.loginAt)
  const setStudentId = useAuthStore((state) => state.setStudentId)
  const logout = useAuthStore((state) => state.logout)

  const [account, setAccount] = useState<Account | null>(null)
  const [student, setStudent] = useState<Student | null>(null)
  const [adminRecord, setAdminRecord] = useState<Admin | null>(null)
  const [majors, setMajors] = useState<Major[]>([])
  const [loading, setLoading] = useState(true)
  const [studentMissing, setStudentMissing] = useState(false)
  const [manualCode, setManualCode] = useState('')
  const [lookupBusy, setLookupBusy] = useState(false)

  const [editOpen, setEditOpen] = useState(false)
  const [editName, setEditName] = useState('')
  const [editCohort, setEditCohort] = useState('')
  const [editMajor, setEditMajor] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!user) return
    let alive = true

    async function load() {
      setLoading(true)
      try {
        const accountInfo = await api.accounts.get(user!.id)
        if (alive) setAccount(accountInfo)
      } catch {
        // Account details are optional; keep the JWT identity as fallback.
      }

      try {
        const majorList = await api.majors.list()
        if (alive) setMajors(majorList)
      } catch {
        if (alive) setMajors([])
      }

      if (user!.role === 'STUDENT') {
        let studentId = useAuthStore.getState().studentId
        if (!studentId) {
          try {
            const conversations = await api.conversations.list()
            if (conversations.length > 0) {
              studentId = conversations[0].studentId
              setStudentId(studentId)
            }
          } catch {
            // No student profile yet; the user can look it up manually below.
          }
        }

        if (studentId) {
          try {
            const info = await api.students.get(studentId)
            if (alive) {
              setStudent(info)
              setStudentMissing(false)
            }
          } catch {
            if (alive) {
              setStudent(null)
              setStudentMissing(true)
            }
          }
        } else if (alive) {
          setStudentMissing(true)
        }
      } else {
        try {
          const admins = await api.admins.list(0, 100)
          const record = admins.find((item) => item.accountId === user!.id) ?? null
          if (alive) setAdminRecord(record)
        } catch {
          // ignore
        }
      }

      if (alive) setLoading(false)
    }

    void load()
    return () => {
      alive = false
    }
  }, [user, setStudentId])

  async function lookupStudent(event: FormEvent) {
    event.preventDefault()
    const id = Number(manualCode.trim())
    if (!Number.isInteger(id) || id <= 0) {
      notify.error('Mã sinh viên phải là số nguyên dương')
      return
    }
    setLookupBusy(true)
    try {
      const info = await api.students.get(id)
      setStudent(info)
      setStudentMissing(false)
      setStudentId(info.id)
      notify.success('Đã tải hồ sơ sinh viên')
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setLookupBusy(false)
    }
  }

  function openEdit() {
    if (!student) return
    setEditName(student.name)
    setEditCohort(String(student.cohort))
    setEditMajor(student.major)
    setEditOpen(true)
  }

  async function submitEdit(event: FormEvent) {
    event.preventDefault()
    if (!student) return
    setSaving(true)
    try {
      const updated = await api.students.patch(student.id, {
        name: editName.trim(),
        cohort: Number(editCohort),
        major: editMajor.trim(),
      })
      setStudent(updated)
      setEditOpen(false)
      notify.success('Đã cập nhật hồ sơ')
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setSaving(false)
    }
  }

  if (!user) return null

  const major = student ? majors.find((item) => item.code === student.major) ?? null : null
  const displayName = student?.name ?? adminRecord?.name ?? user.email.split('@')[0]

  return (
    <div className="page">
      <PageHeader
        eyebrow="Cá nhân"
        title="Tài khoản"
        subtitle="Thông tin đăng nhập, hồ sơ liên kết và trạng thái phiên làm việc."
        actions={
          <button type="button" className="btn btn-ghost btn-danger-text" onClick={logout}>
            <Icon name="logout" size={16} />
            Đăng xuất
          </button>
        }
      />

      <section className="card profile-hero">
        <span className="avatar avatar-xl">{initials(displayName)}</span>
        <div className="profile-hero-copy">
          <span className={`role-chip role-${user.role.toLowerCase()}`}>
            {user.role === 'ADMIN' ? 'Quản trị viên' : 'Sinh viên'}
          </span>
          <h2>{displayName}</h2>
          <p>{user.email}</p>
        </div>
        <div className="profile-hero-meta">
          <div>
            <span>Tài khoản tạo</span>
            <strong>{account ? formatDate(account.createdAt) : '—'}</strong>
          </div>
          <div>
            <span>Trạng thái</span>
            <strong className="status-ok">
              <span className="dot" />
              Đang hoạt động
            </strong>
          </div>
        </div>
      </section>

      <div className="profile-grid">
        <section className="card">
          <header className="card-head">
            <div>
              <h3>Thông tin đăng nhập</h3>
              <p>Dữ liệu lấy từ tài khoản trong hệ thống.</p>
            </div>
            <Icon name="key" size={18} />
          </header>
          <div className="detail-grid detail-grid-tight">
            <div className="detail-item">
              <span>Email</span>
              <strong>{account?.email ?? user.email}</strong>
            </div>
            <div className="detail-item">
              <span>Vai trò</span>
              <strong>{account?.role ?? user.role}</strong>
            </div>
            <div className="detail-item detail-wide">
              <span>ID tài khoản</span>
              <div className="copy-field copy-field-inline">
                <code>{user.id}</code>
                <button
                  type="button"
                  className="icon-btn"
                  onClick={async () => {
                    try {
                      await navigator.clipboard.writeText(user.id)
                      notify.success('Đã sao chép ID tài khoản')
                    } catch {
                      notify.error('Không thể sao chép')
                    }
                  }}
                  aria-label="Sao chép ID tài khoản"
                >
                  <Icon name="copy" size={16} />
                </button>
              </div>
            </div>
            <div className="detail-item detail-wide">
              <span>Đăng nhập gần nhất</span>
              <strong>{formatDateTime(loginAt)}</strong>
            </div>
          </div>
        </section>

        {user.role === 'STUDENT' ? (
          <section className="card">
            <header className="card-head">
              <div>
                <h3>Hồ sơ sinh viên</h3>
                <p>Dùng làm ngữ cảnh khi trò chuyện với trợ lý.</p>
              </div>
              {student && (
                <button type="button" className="btn btn-ghost btn-sm" onClick={openEdit}>
                  <Icon name="edit" size={15} />
                  Sửa
                </button>
              )}
            </header>

            {loading ? (
              <div className="table-loading">
                <span className="spinner" />
                Đang tải hồ sơ…
              </div>
            ) : student ? (
              <div className="detail-grid detail-grid-tight">
                <div className="detail-item">
                  <span>Mã sinh viên</span>
                  <strong>{student.id}</strong>
                </div>
                <div className="detail-item">
                  <span>Niên khóa</span>
                  <strong>K{student.cohort}</strong>
                </div>
                <div className="detail-item">
                  <span>Ngành học</span>
                  <strong>{major?.name ?? student.major}</strong>
                </div>
                <div className="detail-item">
                  <span>Mã ngành</span>
                  <strong>{student.major}</strong>
                </div>
                <div className="detail-item">
                  <span>Ngôn ngữ giảng dạy</span>
                  <strong>{major?.instructionLanguage ?? '—'}</strong>
                </div>
                <div className="detail-item">
                  <span>Chương trình</span>
                  <strong>{major?.programType ?? '—'}</strong>
                </div>
              </div>
            ) : studentMissing ? (
              <div className="profile-empty">
                <EmptyState
                  icon={<Icon name="student" size={24} />}
                  title="Chưa xác định hồ sơ"
                  message="Tài khoản chưa có hồ sơ sinh viên hoặc chưa từng trò chuyện. Nhập mã sinh viên để liên kết."
                  action={
                    <form className="lookup-form" onSubmit={lookupStudent}>
                      <label className="search-box">
                        <Icon name="search" size={16} />
                        <input
                          value={manualCode}
                          onChange={(event) => setManualCode(event.target.value)}
                          placeholder="Nhập mã sinh viên"
                          inputMode="numeric"
                        />
                      </label>
                      <button type="submit" className="btn btn-primary" disabled={lookupBusy}>
                        {lookupBusy ? 'Đang tải…' : 'Tải hồ sơ'}
                      </button>
                    </form>
                  }
                />
              </div>
            ) : null}
          </section>
        ) : (
          <section className="card">
            <header className="card-head">
              <div>
                <h3>Hồ sơ quản trị viên</h3>
                <p>Hồ sơ liên kết với tài khoản đang đăng nhập.</p>
              </div>
              <Icon name="admin" size={18} />
            </header>

            {loading ? (
              <div className="table-loading">
                <span className="spinner" />
                Đang tải hồ sơ…
              </div>
            ) : adminRecord ? (
              <div className="detail-grid detail-grid-tight">
                <div className="detail-item">
                  <span>Mã quản trị viên</span>
                  <strong>{adminRecord.id}</strong>
                </div>
                <div className="detail-item">
                  <span>Họ và tên</span>
                  <strong>{adminRecord.name}</strong>
                </div>
                <div className="detail-item">
                  <span>Ngày tạo hồ sơ</span>
                  <strong>{formatDate(adminRecord.createdAt)}</strong>
                </div>
                <div className="detail-item">
                  <span>ID tài khoản</span>
                  <code className="mono">{adminRecord.accountId.slice(0, 8)}…</code>
                </div>
              </div>
            ) : (
              <div className="profile-empty">
                <EmptyState
                  icon={<Icon name="admin" size={24} />}
                  title="Chưa có hồ sơ quản trị viên"
                  message="Tài khoản này chưa được liên kết với hồ sơ trong bảng quản trị viên."
                />
              </div>
            )}
          </section>
        )}
      </div>

      <Modal
        open={editOpen}
        title="Cập nhật hồ sơ sinh viên"
        description="Chỉ họ tên, niên khóa và ngành học có thể thay đổi."
        onClose={() => setEditOpen(false)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setEditOpen(false)} disabled={saving}>
              Huỷ
            </button>
            <button type="submit" form="profile-student-form" className="btn btn-primary" disabled={saving}>
              {saving ? 'Đang lưu…' : 'Lưu thay đổi'}
            </button>
          </>
        }
      >
        <form id="profile-student-form" className="form-stack" onSubmit={submitEdit}>
          <label className="field">
            <span>Họ và tên</span>
            <input
              value={editName}
              onChange={(event) => setEditName(event.target.value)}
              maxLength={100}
              required
            />
          </label>
          <div className="field-row">
            <label className="field">
              <span>Niên khóa</span>
              <input
                type="number"
                min={1}
                value={editCohort}
                onChange={(event) => setEditCohort(event.target.value)}
                required
              />
            </label>
            <label className="field">
              <span>Mã ngành</span>
              <input
                list="profile-major-options"
                value={editMajor}
                onChange={(event) => setEditMajor(event.target.value)}
                maxLength={10}
                required
              />
            </label>
          </div>
          <datalist id="profile-major-options">
            {majors.map((item) => (
              <option key={item.code} value={item.code}>
                {item.name}
              </option>
            ))}
          </datalist>
        </form>
      </Modal>
    </div>
  )
}
