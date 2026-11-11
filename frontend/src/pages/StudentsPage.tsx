import { useEffect, useState, type FormEvent } from 'react'
import { api, errorMessage } from '../api/client'
import type { Major, Student } from '../api/types'
import { notify } from '../store/uiStore'
import { PageHeader, EmptyState } from '../components/Page'
import { Icon } from '../components/Icon'
import { Modal } from '../components/Modal'
import { ConfirmDialog } from '../components/ConfirmDialog'
import { AccountModal } from '../components/AccountModal'
import { formatDate } from '../utils/format'

const RECENT_KEY = 'campus-recent-students'

function readRecent(): number[] {
  try {
    const raw = localStorage.getItem(RECENT_KEY)
    if (!raw) return []
    const parsed = JSON.parse(raw) as unknown
    return Array.isArray(parsed) ? parsed.filter((item): item is number => typeof item === 'number') : []
  } catch {
    return []
  }
}

function saveRecent(ids: number[]) {
  try {
    localStorage.setItem(RECENT_KEY, JSON.stringify(ids.slice(0, 8)))
  } catch {
    // ignore storage errors
  }
}

interface StudentForm {
  id: string
  accountId: string
  name: string
  cohort: string
  major: string
}

const emptyStudent: StudentForm = {
  id: '',
  accountId: '',
  name: '',
  cohort: '',
  major: '',
}

export function StudentsPage() {
  const [student, setStudent] = useState<Student | null>(null)
  const [query, setQuery] = useState('')
  const [searching, setSearching] = useState(false)
  const [notFound, setNotFound] = useState(false)
  const [recent, setRecent] = useState<number[]>(() => readRecent())
  const [majors, setMajors] = useState<Major[]>([])

  const [createOpen, setCreateOpen] = useState(false)
  const [createForm, setCreateForm] = useState<StudentForm>(emptyStudent)
  const [lastAccountId, setLastAccountId] = useState('')
  const [accountOpen, setAccountOpen] = useState(false)

  const [editOpen, setEditOpen] = useState(false)
  const [editForm, setEditForm] = useState<StudentForm>(emptyStudent)
  const [deleteOpen, setDeleteOpen] = useState(false)
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    api.majors
      .list()
      .then(setMajors)
      .catch(() => setMajors([]))
  }, [])

  function remember(id: number) {
    setRecent((current) => {
      const next = [id, ...current.filter((item) => item !== id)].slice(0, 8)
      saveRecent(next)
      return next
    })
  }

  function forget(id: number) {
    setRecent((current) => {
      const next = current.filter((item) => item !== id)
      saveRecent(next)
      return next
    })
  }

  async function lookup(rawId?: string) {
    const value = (rawId ?? query).trim()
    if (!value) {
      notify.error('Nhập mã sinh viên cần tra cứu')
      return
    }
    const id = Number(value)
    if (!Number.isInteger(id) || id <= 0) {
      notify.error('Mã sinh viên phải là số nguyên dương')
      return
    }
    setSearching(true)
    setNotFound(false)
    try {
      const found = await api.students.get(id)
      setStudent(found)
      setQuery(String(id))
      remember(id)
    } catch (error) {
      setStudent(null)
      setNotFound(true)
      notify.error(errorMessage(error))
    } finally {
      setSearching(false)
    }
  }

  async function submitCreate(event: FormEvent) {
    event.preventDefault()
    setBusy(true)
    try {
      const created = await api.students.create({
        id: Number(createForm.id),
        account_id: createForm.accountId.trim(),
        name: createForm.name.trim(),
        cohort: Number(createForm.cohort),
        major: createForm.major.trim(),
      })
      notify.success('Đã tạo hồ sơ sinh viên')
      setCreateOpen(false)
      setCreateForm(emptyStudent)
      setStudent(created)
      setQuery(String(created.id))
      setNotFound(false)
      remember(created.id)
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  function openEdit() {
    if (!student) return
    setEditForm({
      id: String(student.id),
      accountId: student.accountId,
      name: student.name,
      cohort: String(student.cohort),
      major: student.major,
    })
    setEditOpen(true)
  }

  async function submitEdit(event: FormEvent) {
    event.preventDefault()
    if (!student) return
    setBusy(true)
    try {
      const updated = await api.students.patch(student.id, {
        name: editForm.name.trim(),
        cohort: Number(editForm.cohort),
        major: editForm.major.trim(),
      })
      notify.success('Đã cập nhật hồ sơ sinh viên')
      setEditOpen(false)
      setStudent(updated)
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  async function confirmDelete() {
    if (!student) return
    setBusy(true)
    try {
      await api.students.remove(student.id)
      notify.success('Đã xoá hồ sơ sinh viên')
      forget(student.id)
      setStudent(null)
      setDeleteOpen(false)
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Hồ sơ"
        title="Sinh viên"
        subtitle="Tra cứu hồ sơ theo mã sinh viên, cập nhật thông tin và quản lý tài khoản liên kết."
        actions={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setAccountOpen(true)}>
              <Icon name="key" size={16} />
              Tạo tài khoản
            </button>
            <button
              type="button"
              className="btn btn-primary"
              onClick={() => {
                setCreateForm({ ...emptyStudent, accountId: lastAccountId })
                setCreateOpen(true)
              }}
            >
              <Icon name="plus" size={16} />
              Thêm sinh viên
            </button>
          </>
        }
      />

      <section className="card lookup-card">
        <form
          className="lookup-form"
          onSubmit={(event) => {
            event.preventDefault()
            void lookup()
          }}
        >
          <label className="search-box search-box-lg">
            <Icon name="search" size={18} />
            <input
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Nhập mã sinh viên, ví dụ 20235264"
              inputMode="numeric"
            />
          </label>
          <button type="submit" className="btn btn-primary" disabled={searching}>
            {searching ? 'Đang tra cứu…' : 'Tra cứu'}
          </button>
        </form>

        {recent.length > 0 && (
          <div className="recent-row">
            <span className="muted">Gần đây:</span>
            {recent.map((id) => (
              <button key={id} type="button" className="chip" onClick={() => void lookup(String(id))}>
                {id}
              </button>
            ))}
          </div>
        )}
      </section>

      {student ? (
        <section className="card profile-card">
          <header className="profile-card-head">
            <div className="profile-identity">
              <span className="avatar avatar-lg">
                {student.name
                  .trim()
                  .split(/\s+/)
                  .slice(-2)
                  .map((part) => part[0])
                  .join('')
                  .toUpperCase()}
              </span>
              <div>
                <span className="page-eyebrow">Hồ sơ sinh viên</span>
                <h2>{student.name}</h2>
                <p>
                  Mã sinh viên <strong>{student.id}</strong> · Ngành <strong>{student.major}</strong>
                </p>
              </div>
            </div>
            <div className="row-actions">
              <button type="button" className="btn btn-ghost" onClick={openEdit}>
                <Icon name="edit" size={16} />
                Sửa hồ sơ
              </button>
              <button type="button" className="btn btn-ghost btn-danger-text" onClick={() => setDeleteOpen(true)}>
                <Icon name="trash" size={16} />
                Xoá
              </button>
            </div>
          </header>

          <div className="detail-grid">
            <div className="detail-item">
              <span>Niên khóa</span>
              <strong>K{student.cohort}</strong>
            </div>
            <div className="detail-item">
              <span>Ngành học</span>
              <strong>
                {majors.find((major) => major.code === student.major)?.name ?? student.major}
              </strong>
            </div>
            <div className="detail-item">
              <span>Ngày tạo hồ sơ</span>
              <strong>{formatDate(student.createdAt)}</strong>
            </div>
            <div className="detail-item detail-wide">
              <span>ID tài khoản liên kết</span>
              <div className="copy-field copy-field-inline">
                <code>{student.accountId}</code>
                <button
                  type="button"
                  className="icon-btn"
                  onClick={async () => {
                    try {
                      await navigator.clipboard.writeText(student.accountId)
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
          </div>
        </section>
      ) : (
        <section className="card">
          <EmptyState
            icon={<Icon name="student" size={26} />}
            title={notFound ? 'Không tìm thấy sinh viên' : 'Tra cứu hồ sơ sinh viên'}
            message={
              notFound
                ? 'Mã sinh viên không tồn tại hoặc chưa có hồ sơ. Kiểm tra lại mã và thử lại.'
                : 'Nhập mã sinh viên ở trên để xem hồ sơ. Hệ thống không cung cấp danh sách toàn bộ sinh viên.'
            }
          />
        </section>
      )}

      <Modal
        open={createOpen}
        title="Thêm hồ sơ sinh viên"
        description="Hồ sơ cần liên kết với một tài khoản đã tồn tại."
        onClose={() => setCreateOpen(false)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setCreateOpen(false)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="create-student-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang lưu…' : 'Tạo hồ sơ'}
            </button>
          </>
        }
      >
        <form id="create-student-form" className="form-stack" onSubmit={submitCreate}>
          <div className="field-row">
            <label className="field">
              <span>Mã sinh viên</span>
              <input
                type="number"
                min={1}
                value={createForm.id}
                onChange={(event) => setCreateForm({ ...createForm, id: event.target.value })}
                placeholder="20235264"
                required
              />
            </label>
            <label className="field">
              <span>Niên khóa</span>
              <input
                type="number"
                min={1}
                value={createForm.cohort}
                onChange={(event) => setCreateForm({ ...createForm, cohort: event.target.value })}
                placeholder="68"
                required
              />
            </label>
          </div>
          <label className="field">
            <span>Họ và tên</span>
            <input
              value={createForm.name}
              onChange={(event) => setCreateForm({ ...createForm, name: event.target.value })}
              placeholder="Nguyễn Văn A"
              maxLength={100}
              required
            />
          </label>
          <label className="field">
            <span>ID tài khoản</span>
            <input
              value={createForm.accountId}
              onChange={(event) => setCreateForm({ ...createForm, accountId: event.target.value })}
              placeholder="Dán UUID tài khoản"
              required
            />
            <small className="field-hint">Tạo tài khoản ở nút "Tạo tài khoản" rồi sao chép ID.</small>
          </label>
          <label className="field">
            <span>Mã ngành</span>
            <input
              list="student-major-options"
              value={createForm.major}
              onChange={(event) => setCreateForm({ ...createForm, major: event.target.value })}
              placeholder="IT1"
              maxLength={10}
              required
            />
          </label>
          <datalist id="student-major-options">
            {majors.map((major) => (
              <option key={major.code} value={major.code}>
                {major.name}
              </option>
            ))}
          </datalist>
        </form>
      </Modal>

      <Modal
        open={editOpen}
        title="Cập nhật hồ sơ sinh viên"
        description="Chỉ họ tên, niên khóa và ngành học có thể thay đổi."
        onClose={() => setEditOpen(false)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setEditOpen(false)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="edit-student-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang lưu…' : 'Lưu thay đổi'}
            </button>
          </>
        }
      >
        <form id="edit-student-form" className="form-stack" onSubmit={submitEdit}>
          <div className="field-row">
            <label className="field">
              <span>Mã sinh viên</span>
              <input value={editForm.id} readOnly disabled />
            </label>
            <label className="field">
              <span>Niên khóa</span>
              <input
                type="number"
                min={1}
                value={editForm.cohort}
                onChange={(event) => setEditForm({ ...editForm, cohort: event.target.value })}
                required
              />
            </label>
          </div>
          <label className="field">
            <span>Họ và tên</span>
            <input
              value={editForm.name}
              onChange={(event) => setEditForm({ ...editForm, name: event.target.value })}
              maxLength={100}
              required
            />
          </label>
          <label className="field">
            <span>Mã ngành</span>
            <input
              list="student-major-options"
              value={editForm.major}
              onChange={(event) => setEditForm({ ...editForm, major: event.target.value })}
              maxLength={10}
              required
            />
          </label>
        </form>
      </Modal>

      <ConfirmDialog
        open={deleteOpen}
        title="Xoá hồ sơ sinh viên"
        message={`Hồ sơ của "${student?.name ?? ''}" (${student?.id ?? ''}) sẽ bị xoá. Tài khoản đăng nhập không bị ảnh hưởng.`}
        busy={busy}
        onClose={() => setDeleteOpen(false)}
        onConfirm={confirmDelete}
      />

      <AccountModal
        open={accountOpen}
        defaultRole="STUDENT"
        onClose={() => setAccountOpen(false)}
        onCreated={(accountId) => {
          setLastAccountId(accountId)
          setCreateForm((form) => ({ ...form, accountId }))
        }}
      />
    </div>
  )
}
