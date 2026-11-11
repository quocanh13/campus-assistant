import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { api, errorMessage } from '../api/client'
import type { DocumentItem } from '../api/types'
import { useAuthStore } from '../store/authStore'
import { notify } from '../store/uiStore'
import { PageHeader, EmptyState } from '../components/Page'
import { Icon } from '../components/Icon'
import { Modal } from '../components/Modal'
import { ConfirmDialog } from '../components/ConfirmDialog'
import { formatBytes, formatDateTime, toDateTimeLocal, withSeconds } from '../utils/format'

const PAGE_SIZE = 10

interface UploadForm {
  fromCohort: string
  toCohort: string
  keywords: string
  validAt: string
  expiredAt: string
}

const emptyUpload: UploadForm = {
  fromCohort: '',
  toCohort: '',
  keywords: '',
  validAt: '',
  expiredAt: '',
}

export function DocumentsPage() {
  const role = useAuthStore((state) => state.user?.role)
  const isAdmin = role === 'ADMIN'

  const [documents, setDocuments] = useState<DocumentItem[]>([])
  const [total, setTotal] = useState(0)
  const [index, setIndex] = useState(0)
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)

  const [uploadOpen, setUploadOpen] = useState(false)
  const [file, setFile] = useState<File | null>(null)
  const [uploadForm, setUploadForm] = useState<UploadForm>(emptyUpload)

  const [editTarget, setEditTarget] = useState<DocumentItem | null>(null)
  const [editName, setEditName] = useState('')
  const [editDescription, setEditDescription] = useState('')
  const [editValidAt, setEditValidAt] = useState('')
  const [editExpiredAt, setEditExpiredAt] = useState('')

  const [deleteTarget, setDeleteTarget] = useState<DocumentItem | null>(null)

  const [viewer, setViewer] = useState<{ name: string; mimeType: string; url: string } | null>(null)
  const [opening, setOpening] = useState<string | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const [items, count] = await Promise.all([
        api.documents.list(index, PAGE_SIZE),
        api.documents.count(),
      ])
      setDocuments(items)
      setTotal(count)
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [index])

  useEffect(() => {
    void load()
  }, [load])

  const pageCount = Math.max(1, Math.ceil(total / PAGE_SIZE))

  function openUpload() {
    setFile(null)
    setUploadForm(emptyUpload)
    setUploadOpen(true)
  }

  async function submitUpload(event: FormEvent) {
    event.preventDefault()
    if (!file) {
      notify.error('Vui lòng chọn tệp tài liệu')
      return
    }
    const keywords = uploadForm.keywords
      .split(',')
      .map((keyword) => keyword.trim())
      .filter(Boolean)

    if (keywords.length === 0) {
      notify.error('Vui lòng nhập ít nhất một từ khoá')
      return
    }

    setBusy(true)
    try {
      const form = new FormData()
      form.append('file', file)
      form.append('from_cohort', uploadForm.fromCohort)
      form.append('to_cohort', uploadForm.toCohort)
      keywords.forEach((keyword) => form.append('keywords', keyword))
      form.append('valid_at', withSeconds(uploadForm.validAt))
      if (uploadForm.expiredAt) {
        form.append('expired_at', withSeconds(uploadForm.expiredAt))
      }
      await api.documents.upload(form)
      notify.success('Đã tải lên tài liệu')
      setUploadOpen(false)
      setIndex(0)
      await load()
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  function openEdit(document: DocumentItem) {
    setEditTarget(document)
    setEditName(document.name ?? '')
    setEditDescription(document.description ?? '')
    setEditValidAt(toDateTimeLocal(document.validAt))
    setEditExpiredAt(toDateTimeLocal(document.expriedAt))
  }

  async function submitEdit(event: FormEvent) {
    event.preventDefault()
    if (!editTarget) return

    const body: Record<string, unknown> = { name: editName.trim() }
    if (editDescription.trim()) body.description = editDescription.trim()
    if (editValidAt) body.valid_at = withSeconds(editValidAt)
    if (editExpiredAt) body.expired_at = withSeconds(editExpiredAt)

    setBusy(true)
    try {
      await api.documents.patch(editTarget.id, body)
      notify.success('Đã cập nhật tài liệu')
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
      await api.documents.remove(deleteTarget.id)
      notify.success('Đã xoá tài liệu')
      setDeleteTarget(null)
      if (documents.length === 1 && index > 0) {
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

  async function openFile(document: DocumentItem, download: boolean) {
    const key = `${document.id}:${download ? 'download' : 'view'}`
    setOpening(key)
    try {
      const blob = await api.documents.file(document.id, download)
      const url = URL.createObjectURL(blob)
      if (download) {
        const anchor = window.document.createElement('a')
        anchor.href = url
        anchor.download = document.name ?? 'document'
        window.document.body.appendChild(anchor)
        anchor.click()
        anchor.remove()
        window.setTimeout(() => URL.revokeObjectURL(url), 4000)
      } else {
        setViewer((current) => {
          if (current) URL.revokeObjectURL(current.url)
          return { name: document.name ?? 'Tài liệu', mimeType: document.mimeType, url }
        })
      }
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setOpening(null)
    }
  }

  function closeViewer() {
    setViewer((current) => {
      if (current) URL.revokeObjectURL(current.url)
      return null
    })
  }

  function canPreview(mimeType: string): boolean {
    return (
      mimeType.startsWith('image/') ||
      mimeType === 'application/pdf' ||
      mimeType.startsWith('text/') ||
      mimeType.includes('json') ||
      mimeType.includes('xml')
    )
  }

  function isExpired(document: DocumentItem): boolean {
    if (!document.expriedAt) return false
    return new Date(document.expriedAt).getTime() < Date.now()
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Học vụ"
        title="Kho tài liệu"
        subtitle="Tài liệu được phân theo niên khóa, có thời gian hiệu lực rõ ràng."
        actions={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => void load()} disabled={loading}>
              <Icon name="refresh" size={16} />
              Làm mới
            </button>
            {isAdmin && (
              <button type="button" className="btn btn-primary" onClick={openUpload}>
                <Icon name="upload" size={16} />
                Tải lên
              </button>
            )}
          </>
        }
      />

      <div className="card table-card">
        {loading ? (
          <div className="table-loading">
            <span className="spinner" />
            Đang tải tài liệu…
          </div>
        ) : documents.length === 0 ? (
          <EmptyState
            icon={<Icon name="document" size={26} />}
            title="Chưa có tài liệu"
            message={isAdmin ? 'Tải lên tài liệu đầu tiên để bắt đầu.' : 'Kho tài liệu hiện đang trống.'}
            action={
              isAdmin ? (
                <button type="button" className="btn btn-primary" onClick={openUpload}>
                  <Icon name="upload" size={16} />
                  Tải lên tài liệu
                </button>
              ) : undefined
            }
          />
        ) : (
          <div className="table-scroll">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Tài liệu</th>
                  <th>Kích thước</th>
                  <th>Hiệu lực</th>
                  <th>Người đăng</th>
                  <th className="align-right">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {documents.map((document) => (
                  <tr key={document.id}>
                    <td>
                      <div className="cell-title">
                        <span className="file-icon">
                          <Icon name="document" size={16} />
                        </span>
                        <div>
                          <strong>{document.name}</strong>
                          <small>{document.mimeType}</small>
                          {document.description && <p className="cell-desc">{document.description}</p>}
                        </div>
                      </div>
                    </td>
                    <td>{formatBytes(document.size)}</td>
                    <td>
                      <div className="cell-dates">
                        <span>{formatDateTime(document.validAt)}</span>
                        <span className="muted">→ {formatDateTime(document.expriedAt)}</span>
                        {isExpired(document) && <span className="badge badge-danger">Hết hiệu lực</span>}
                      </div>
                    </td>
                    <td>
                      <code className="mono muted">{document.userId?.slice(0, 8)}…</code>
                    </td>
                    <td>
                      <div className="row-actions">
                        <button
                          type="button"
                          className="icon-btn"
                          onClick={() => void openFile(document, false)}
                          aria-label="Xem"
                          title="Xem tài liệu"
                          disabled={opening === `${document.id}:view`}
                        >
                          <Icon name="eye" size={16} />
                        </button>
                        <button
                          type="button"
                          className="icon-btn"
                          onClick={() => void openFile(document, true)}
                          aria-label="Tải xuống"
                          title="Tải xuống"
                          disabled={opening === `${document.id}:download`}
                        >
                          <Icon name="download" size={16} />
                        </button>
                        {isAdmin && (
                          <>
                            <button
                              type="button"
                              className="icon-btn"
                              onClick={() => openEdit(document)}
                              aria-label="Sửa"
                              title="Sửa tài liệu"
                            >
                              <Icon name="edit" size={16} />
                            </button>
                            <button
                              type="button"
                              className="icon-btn icon-btn-danger"
                              onClick={() => setDeleteTarget(document)}
                              aria-label="Xoá"
                              title="Xoá tài liệu"
                            >
                              <Icon name="trash" size={16} />
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        <footer className="table-foot">
          <span className="muted">
            {total === 0
              ? '0 tài liệu'
              : `${index * PAGE_SIZE + 1}–${Math.min((index + 1) * PAGE_SIZE, total)} / ${total} tài liệu`}
          </span>
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
            <span className="pager-count">
              {index + 1} / {pageCount}
            </span>
            <button
              type="button"
              className="btn btn-ghost btn-sm"
              disabled={index + 1 >= pageCount || loading}
              onClick={() => setIndex((value) => value + 1)}
            >
              Sau
              <Icon name="chevronRight" size={16} />
            </button>
          </div>
        </footer>
      </div>

      <Modal
        open={uploadOpen}
        title="Tải lên tài liệu"
        description="Tệp sẽ được đánh chỉ mục để trợ lý có thể tra cứu."
        onClose={() => setUploadOpen(false)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setUploadOpen(false)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="upload-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang tải lên…' : 'Tải lên'}
            </button>
          </>
        }
      >
        <form id="upload-form" className="form-stack" onSubmit={submitUpload}>
          <label className="field">
            <span>Tệp tài liệu</span>
            <input
              type="file"
              onChange={(event) => setFile(event.target.files?.[0] ?? null)}
              required
            />
            <small className="field-hint">Tên tài liệu sẽ lấy theo tên tệp.</small>
          </label>

          <div className="field-row">
            <label className="field">
              <span>Từ niên khóa</span>
              <input
                type="number"
                min={1}
                value={uploadForm.fromCohort}
                onChange={(event) => setUploadForm({ ...uploadForm, fromCohort: event.target.value })}
                placeholder="66"
                required
              />
            </label>
            <label className="field">
              <span>Đến niên khóa</span>
              <input
                type="number"
                min={1}
                value={uploadForm.toCohort}
                onChange={(event) => setUploadForm({ ...uploadForm, toCohort: event.target.value })}
                placeholder="70"
                required
              />
            </label>
          </div>

          <label className="field">
            <span>Từ khoá</span>
            <input
              value={uploadForm.keywords}
              onChange={(event) => setUploadForm({ ...uploadForm, keywords: event.target.value })}
              placeholder="quy chế, đào tạo, tín chỉ"
              required
            />
            <small className="field-hint">Phân tách bằng dấu phẩy.</small>
          </label>

          <div className="field-row">
            <label className="field">
              <span>Có hiệu lực từ</span>
              <input
                type="datetime-local"
                value={uploadForm.validAt}
                onChange={(event) => setUploadForm({ ...uploadForm, validAt: event.target.value })}
                required
              />
            </label>
            <label className="field">
              <span>Hết hiệu lực</span>
              <input
                type="datetime-local"
                value={uploadForm.expiredAt}
                onChange={(event) => setUploadForm({ ...uploadForm, expiredAt: event.target.value })}
              />
            </label>
          </div>
        </form>
      </Modal>

      <Modal
        open={editTarget !== null}
        title="Cập nhật tài liệu"
        description="Thay đổi tên hoặc thời gian hiệu lực sẽ đồng bộ sang dịch vụ tra cứu."
        onClose={() => setEditTarget(null)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setEditTarget(null)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="edit-document-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang lưu…' : 'Lưu thay đổi'}
            </button>
          </>
        }
      >
        <form id="edit-document-form" className="form-stack" onSubmit={submitEdit}>
          <label className="field">
            <span>Tên tài liệu</span>
            <input value={editName} onChange={(event) => setEditName(event.target.value)} maxLength={255} required />
          </label>
          <label className="field">
            <span>Mô tả</span>
            <textarea
              value={editDescription}
              onChange={(event) => setEditDescription(event.target.value)}
              rows={3}
              maxLength={2500}
              placeholder="Mô tả ngắn về tài liệu"
            />
          </label>
          <div className="field-row">
            <label className="field">
              <span>Có hiệu lực từ</span>
              <input
                type="datetime-local"
                value={editValidAt}
                onChange={(event) => setEditValidAt(event.target.value)}
              />
            </label>
            <label className="field">
              <span>Hết hiệu lực</span>
              <input
                type="datetime-local"
                value={editExpiredAt}
                onChange={(event) => setEditExpiredAt(event.target.value)}
              />
            </label>
          </div>
          <small className="field-hint">Để trống thời gian nếu muốn giữ nguyên giá trị hiện tại.</small>
        </form>
      </Modal>

      <Modal
        open={viewer !== null}
        title={viewer?.name ?? 'Xem tài liệu'}
        description={viewer ? `${viewer.mimeType} · xem trước trong ứng dụng` : undefined}
        size="lg"
        onClose={closeViewer}
        footer={
          viewer ? (
            <>
              <button type="button" className="btn btn-ghost" onClick={closeViewer}>
                Đóng
              </button>
              <button
                type="button"
                className="btn btn-ghost"
                onClick={() => window.open(viewer.url, '_blank', 'noopener')}
              >
                <Icon name="external" size={16} />
                Mở tab mới
              </button>
              <a className="btn btn-primary" href={viewer.url} download={viewer.name}>
                <Icon name="download" size={16} />
                Tải xuống
              </a>
            </>
          ) : null
        }
      >
        {viewer &&
          (canPreview(viewer.mimeType) ? (
            <div className="doc-viewer">
              {viewer.mimeType.startsWith('image/') ? (
                <img src={viewer.url} alt={viewer.name} />
              ) : (
                <iframe src={viewer.url} title={viewer.name} />
              )}
            </div>
          ) : (
            <div className="doc-viewer-fallback">
              <span className="empty-icon">
                <Icon name="document" size={26} />
              </span>
              <strong>Không hỗ trợ xem trước</strong>
              <p>
                Định dạng {viewer.mimeType} không thể hiển thị trực tiếp trong trình duyệt. Hãy tải
                xuống để mở bằng ứng dụng phù hợp.
              </p>
              <a className="btn btn-primary" href={viewer.url} download={viewer.name}>
                <Icon name="download" size={16} />
                Tải xuống tài liệu
              </a>
            </div>
          ))}
      </Modal>

      <ConfirmDialog
        open={deleteTarget !== null}
        title="Xoá tài liệu"
        message={`Tài liệu "${deleteTarget?.name ?? ''}" sẽ bị xoá khỏi kho và dịch vụ tra cứu.`}
        busy={busy}
        onClose={() => setDeleteTarget(null)}
        onConfirm={confirmDelete}
      />
    </div>
  )
}
