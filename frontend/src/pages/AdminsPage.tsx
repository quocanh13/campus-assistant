import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { api, errorMessage } from '../api/client'
import type { Admin } from '../api/types'
import { notify } from '../store/uiStore'
import { PageHeader, EmptyState } from '../components/Page'
import { Icon } from '../components/Icon'
import { Modal } from '../components/Modal'
import { ConfirmDialog } from '../components/ConfirmDialog'
import { AccountModal } from '../components/AccountModal'
import { formatDate } from '../utils/format'

const PAGE_SIZE = 10

interface AdminForm {
  id: string
  accountId: string
  name: string
}

const emptyAdmin: AdminForm = { id: '', accountId: '', name: '' }

export function AdminsPage() {
  const [admins, setAdmins] = useState<Admin[]>([])
  const [index, setIndex] = useState(0)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)

  const [createOpen, setCreateOpen] = useState(false)
  const [createForm, setCreateForm] = useState<AdminForm>(emptyAdmin)
  const [lastAccountId, setLastAccountId] = useState('')
  const [accountOpen, setAccountOpen] = useState(false)

  const [editTarget, setEditTarget] = useState<Admin | null>(null)
  const [editName, setEditName] = useState('')

  const [deleteTarget, setDeleteTarget] = useState<Admin | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const list = await api.admins.list(index, PAGE_SIZE)
      setAdmins(list)
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [index])

  useEffect(() => {
    void load()
  }, [load])

  async function submitCreate(event: FormEvent) {
    event.preventDefault()
    setBusy(true)
    try {
      await api.admins.create({
        id: Number(createForm.id),
        account_id: createForm.accountId.trim(),
        name: createForm.name.trim(),
      })
      notify.success('Đã thêm quản trị viên')
      setCreateOpen(false)
      setCreateForm(emptyAdmin)
      await load()
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  function openEdit(admin: Admin) {
    setEditTarget(admin)
    setEditName(admin.name)
  }

  async function submitEdit(event: FormEvent) {
    event.preventDefault()
    if (!editTarget) return
    setBusy(true)
    try {
      await api.admins.patch(editTarget.id, { name: editName.trim() })
      notify.success('Đã cập nhật quản trị viên')
      setEditTarget(null)
      await load()
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  async function confirmDelete() {
    if (!deleteTarget) return
    setBusy(true)
    try {
      await api.admins.remove(deleteTarget.id)
      notify.success('Đã xoá quản trị viên')
      setDeleteTarget(null)
      if (admins.length === 1 && index > 0) {
        setIndex((value) => value - 1)
      } else {
        await load()
      }
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  const hasNext = admins.length === PAGE_SIZE

  return (
    <div className="page">
      <PageHeader
        eyebrow="Vận hành"
        title="Quản trị viên"
        subtitle="Hồ sơ quản trị viên liên kết với tài khoản có vai trò ADMIN."
        actions={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setAccountOpen(true)}>
              <Icon name="key" size={16} />
              Tạo tài khoản
            </button>
            <button type="button" className="btn btn-ghost" onClick={() => void load()} disabled={loading}>
              <Icon name="refresh" size={16} />
              Làm mới
            </button>
            <button
              type="button"
              className="btn btn-primary"
              onClick={() => {
                setCreateForm({ ...emptyAdmin, accountId: lastAccountId })
                setCreateOpen(true)
              }}
            >
              <Icon name="plus" size={16} />
              Thêm quản trị viên
            </button>
          </>
        }
      />

      <div className="alert alert-info">
        <Icon name="info" size={16} />
        <span>
          Các API quản trị viên hiện đang mở công khai để thử nghiệm. Vai trò truy cập vẫn lấy từ
          tài khoản đăng nhập.
        </span>
      </div>

      <div className="card table-card">
        {loading ? (
          <div className="table-loading">
            <span className="spinner" />
            Đang tải quản trị viên…
          </div>
        ) : admins.length === 0 ? (
          <EmptyState
            icon={<Icon name="admin" size={26} />}
            title="Chưa có quản trị viên"
            message="Thêm hồ sơ quản trị viên đầu tiên. Cần tạo tài khoản vai trò ADMIN trước."
            action={
              <button type="button" className="btn btn-primary" onClick={() => setAccountOpen(true)}>
                <Icon name="key" size={16} />
                Tạo tài khoản
              </button>
            }
          />
        ) : (
          <div className="table-scroll">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Mã QTV</th>
                  <th>Họ và tên</th>
                  <th>Ngày tạo</th>
                  <th>Tài khoản</th>
                  <th className="align-right">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {admins.map((admin) => (
                  <tr key={admin.id}>
                    <td>
                      <span className="code-chip">{admin.id}</span>
                    </td>
                    <td>
                      <strong>{admin.name}</strong>
                    </td>
                    <td>{formatDate(admin.createdAt)}</td>
                    <td>
                      <code className="mono muted">{admin.accountId.slice(0, 8)}…</code>
                    </td>
                    <td>
                      <div className="row-actions">
                        <button
                          type="button"
                          className="icon-btn"
                          onClick={() => openEdit(admin)}
                          aria-label="Sửa"
                          title="Sửa quản trị viên"
                        >
                          <Icon name="edit" size={16} />
                        </button>
                        <button
                          type="button"
                          className="icon-btn icon-btn-danger"
                          onClick={() => setDeleteTarget(admin)}
                          aria-label="Xoá"
                          title="Xoá quản trị viên"
                        >
                          <Icon name="trash" size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <footer className="table-foot">
          <span className="muted">Trang {index + 1}</span>
          <div className="pager">
            <button
              type="button"
              className="btn btn-ghost btn-sm"
              disabled={index === 0 || loading}
              onClick={() => setIndex((value) => Math.max(0, value - 1))}
            >
              <Icon name="chevronLeft" size={16} />
              Trước
            </button>
            <button
              type="button"
              className="btn btn-ghost btn-sm"
              disabled={!hasNext || loading}
              onClick={() => setIndex((value) => value + 1)}
            >
              Sau
              <Icon name="chevronRight" size={16} />
            </button>
          </div>
        </footer>
      </div>

      <Modal
        open={createOpen}
        title="Thêm quản trị viên"
        description="Hồ sơ quản trị viên cần liên kết với một tài khoản đã tồn tại."
        onClose={() => setCreateOpen(false)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setCreateOpen(false)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="create-admin-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang lưu…' : 'Thêm quản trị viên'}
            </button>
          </>
        }
      >
        <form id="create-admin-form" className="form-stack" onSubmit={submitCreate}>
          <div className="field-row">
            <label className="field">
              <span>Mã quản trị viên</span>
              <input
                type="number"
                min={1}
                value={createForm.id}
                onChange={(event) => setCreateForm({ ...createForm, id: event.target.value })}
                placeholder="1"
                required
              />
            </label>
            <label className="field">
              <span>Họ và tên</span>
              <input
                value={createForm.name}
                onChange={(event) => setCreateForm({ ...createForm, name: event.target.value })}
                placeholder="Nguyễn Văn B"
                maxLength={100}
                required
              />
            </label>
          </div>
          <label className="field">
            <span>ID tài khoản</span>
            <input
              value={createForm.accountId}
              onChange={(event) => setCreateForm({ ...createForm, accountId: event.target.value })}
              placeholder="Dán UUID tài khoản vai trò ADMIN"
              required
            />
            <small className="field-hint">Tạo tài khoản ở nút "Tạo tài khoản" rồi sao chép ID.</small>
          </label>
        </form>
      </Modal>

      <Modal
        open={editTarget !== null}
        title="Cập nhật quản trị viên"
        onClose={() => setEditTarget(null)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setEditTarget(null)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="edit-admin-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang lưu…' : 'Lưu thay đổi'}
            </button>
          </>
        }
      >
        <form id="edit-admin-form" className="form-stack" onSubmit={submitEdit}>
          <label className="field">
            <span>Mã quản trị viên</span>
            <input value={editTarget?.id ?? ''} readOnly disabled />
          </label>
          <label className="field">
            <span>Họ và tên</span>
            <input
              value={editName}
              onChange={(event) => setEditName(event.target.value)}
              maxLength={100}
              required
            />
          </label>
        </form>
      </Modal>

      <ConfirmDialog
        open={deleteTarget !== null}
        title="Xoá quản trị viên"
        message={`Hồ sơ của "${deleteTarget?.name ?? ''}" sẽ bị xoá. Tài khoản đăng nhập không bị ảnh hưởng.`}
        busy={busy}
        onClose={() => setDeleteTarget(null)}
        onConfirm={confirmDelete}
      />

      <AccountModal
        open={accountOpen}
        defaultRole="ADMIN"
        onClose={() => setAccountOpen(false)}
        onCreated={setLastAccountId}
      />
    </div>
  )
}
