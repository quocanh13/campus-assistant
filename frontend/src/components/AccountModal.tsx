import { useEffect, useState, type FormEvent } from 'react'
import { api, errorMessage } from '../api/client'
import { notify } from '../store/uiStore'
import { Icon } from './Icon'
import { Modal } from './Modal'

interface AccountModalProps {
  open: boolean
  defaultRole?: 'STUDENT' | 'ADMIN'
  onClose: () => void
  onCreated?: (accountId: string) => void
}

export function AccountModal({ open, defaultRole = 'STUDENT', onClose, onCreated }: AccountModalProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState<'STUDENT' | 'ADMIN'>(defaultRole)
  const [busy, setBusy] = useState(false)
  const [createdId, setCreatedId] = useState<string | null>(null)

  useEffect(() => {
    if (open) {
      setEmail('')
      setPassword('')
      setRole(defaultRole)
      setCreatedId(null)
      setBusy(false)
    }
  }, [open, defaultRole])

  async function submit(event: FormEvent) {
    event.preventDefault()
    setBusy(true)
    try {
      const account = await api.accounts.create({ email: email.trim(), password, role })
      setCreatedId(account.id)
      onCreated?.(account.id)
      notify.success('Đã tạo tài khoản mới')
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  async function copyId() {
    if (!createdId) return
    try {
      await navigator.clipboard.writeText(createdId)
      notify.success('Đã sao chép ID tài khoản')
    } catch {
      notify.error('Không thể sao chép, vui lòng chọn và sao chép thủ công')
    }
  }

  return (
    <Modal
      open={open}
      title="Tạo tài khoản"
      description="Tài khoản đăng nhập dùng email và mật khẩu. Không có luồng tự đăng ký."
      onClose={onClose}
      footer={
        createdId ? (
          <button type="button" className="btn btn-primary" onClick={onClose}>
            Hoàn tất
          </button>
        ) : (
          <>
            <button type="button" className="btn btn-ghost" onClick={onClose} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="account-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang tạo…' : 'Tạo tài khoản'}
            </button>
          </>
        )
      }
    >
      {createdId ? (
        <div className="created-account">
          <span className="created-check">
            <Icon name="check" size={22} />
          </span>
          <h3>Đã tạo tài khoản</h3>
          <p>Dùng ID dưới đây khi tạo hồ sơ sinh viên hoặc quản trị viên.</p>
          <div className="copy-field">
            <code>{createdId}</code>
            <button type="button" className="icon-btn" onClick={copyId} aria-label="Sao chép ID">
              <Icon name="copy" size={16} />
            </button>
          </div>
        </div>
      ) : (
        <form id="account-form" className="form-stack" onSubmit={submit}>
          <label className="field">
            <span>Email</span>
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              placeholder="sinhvien@campus.edu.vn"
              required
              autoComplete="off"
            />
          </label>
          <label className="field">
            <span>Mật khẩu</span>
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Tối thiểu 8 ký tự"
              minLength={8}
              maxLength={72}
              required
              autoComplete="new-password"
            />
          </label>
          <label className="field">
            <span>Vai trò</span>
            <select value={role} onChange={(event) => setRole(event.target.value as 'STUDENT' | 'ADMIN')}>
              <option value="STUDENT">STUDENT — Sinh viên</option>
              <option value="ADMIN">ADMIN — Quản trị viên</option>
            </select>
          </label>
        </form>
      )}
    </Modal>
  )
}
