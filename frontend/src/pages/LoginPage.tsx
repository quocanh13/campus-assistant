import { useState, type FormEvent } from 'react'
import { api, errorMessage } from '../api/client'
import { useAuthStore } from '../store/authStore'
import { notify } from '../store/uiStore'
import { Icon } from '../components/Icon'

export function LoginPage() {
  const setSession = useAuthStore((state) => state.setSession)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function submit(event: FormEvent) {
    event.preventDefault()
    setBusy(true)
    setError(null)
    try {
      const result = await api.login(email.trim(), password)
      setSession(result)
      notify.success(`Chào mừng trở lại, ${result.user.email}`)
    } catch (reason) {
      setError(errorMessage(reason))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="login-page">
      <section className="login-hero">
        <div className="login-brand">
          <span className="brand-mark">
            <Icon name="spark" size={20} />
          </span>
          <span className="brand-text">
            <strong>Campus</strong>
            <small>Assistant</small>
          </span>
        </div>

        <div className="login-hero-copy">
          <span className="hero-badge">Trợ lý học vụ thông minh</span>
          <h1>
            Mọi câu hỏi về học vụ,
            <br />
            <em>một nơi để hỏi.</em>
          </h1>
          <p>
            Trò chuyện với trợ lý AI, tra cứu ngành học, tài liệu và thông tin sinh viên
            trong cùng một không gian.
          </p>
        </div>

        <ul className="hero-points">
          <li>
            <span>
              <Icon name="chat" size={18} />
            </span>
            <div>
              <strong>Hỏi đáp theo ngữ cảnh</strong>
              <small>Trợ lý ghi nhớ cuộc trò chuyện và tra cứu dữ liệu thật.</small>
            </div>
          </li>
          <li>
            <span>
              <Icon name="document" size={18} />
            </span>
            <div>
              <strong>Kho tài liệu học vụ</strong>
              <small>Tìm, xem và tải tài liệu theo niên khóa.</small>
            </div>
          </li>
          <li>
            <span>
              <Icon name="major" size={18} />
            </span>
            <div>
              <strong>Thông tin ngành học</strong>
              <small>Chương trình, ngôn ngữ giảng dạy và đơn vị quản lý.</small>
            </div>
          </li>
        </ul>

        <div className="login-hero-foot">
          <span className="dot" />
          Hệ thống dành cho sinh viên và quản trị viên của trường.
        </div>
      </section>

      <section className="login-panel">
        <form className="login-card" onSubmit={submit}>
          <header>
            <span className="login-eyebrow">Đăng nhập</span>
            <h2>Tiếp tục với tài khoản của bạn</h2>
            <p>Nhập email và mật khẩu đã được cấp bởi nhà trường.</p>
          </header>

          {error && (
            <div className="alert alert-error" role="alert">
              <Icon name="info" size={16} />
              <span>{error}</span>
            </div>
          )}

          <label className="field">
            <span>Email</span>
            <div className="input-with-icon">
              <Icon name="mail" size={16} />
              <input
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                placeholder="email@campus.edu.vn"
                required
                autoComplete="username"
                autoFocus
              />
            </div>
          </label>

          <label className="field">
            <span>Mật khẩu</span>
            <div className="input-with-icon">
              <Icon name="lock" size={16} />
              <input
                type={showPassword ? 'text' : 'password'}
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                placeholder="Nhập mật khẩu"
                required
                autoComplete="current-password"
              />
              <button
                type="button"
                className="input-action"
                onClick={() => setShowPassword((value) => !value)}
                aria-label={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
              >
                <Icon name="eye" size={16} />
              </button>
            </div>
          </label>

          <button type="submit" className="btn btn-primary btn-block btn-lg" disabled={busy}>
            {busy ? 'Đang đăng nhập…' : 'Đăng nhập'}
            {!busy && <Icon name="chevronRight" size={18} />}
          </button>

          <footer className="login-card-foot">
            <Icon name="info" size={14} />
            <span>Tài khoản được cấp bởi quản trị viên. Hệ thống không hỗ trợ tự đăng ký.</span>
          </footer>
        </form>
      </section>
    </div>
  )
}
