import { useUiStore } from '../store/uiStore'
import { Icon } from './Icon'

export function Toasts() {
  const toasts = useUiStore((state) => state.toasts)
  const dismiss = useUiStore((state) => state.dismiss)

  if (toasts.length === 0) return null

  return (
    <div className="toast-stack" role="status" aria-live="polite">
      {toasts.map((toast) => (
        <div key={toast.id} className={`toast toast-${toast.kind}`}>
          <span className="toast-icon">
            <Icon name={toast.kind === 'success' ? 'check' : toast.kind === 'error' ? 'close' : 'info'} size={16} />
          </span>
          <p>{toast.message}</p>
          <button type="button" className="toast-close" onClick={() => dismiss(toast.id)} aria-label="Đóng">
            <Icon name="close" size={14} />
          </button>
        </div>
      ))}
    </div>
  )
}
