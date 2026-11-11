import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react'
import { api, errorMessage } from '../api/client'
import type { Major, Unit } from '../api/types'
import { useAuthStore } from '../store/authStore'
import { notify } from '../store/uiStore'
import { PageHeader, EmptyState } from '../components/Page'
import { Icon } from '../components/Icon'
import { Modal } from '../components/Modal'
import { ConfirmDialog } from '../components/ConfirmDialog'

interface MajorForm {
  code: string
  name: string
  instructionLanguage: string
  unitCode: string
  programType: string
}

const emptyMajor: MajorForm = {
  code: '',
  name: '',
  instructionLanguage: '',
  unitCode: '',
  programType: '',
}

export function MajorsPage() {
  const role = useAuthStore((state) => state.user?.role)
  const isAdmin = role === 'ADMIN'

  const [majors, setMajors] = useState<Major[]>([])
  const [units, setUnits] = useState<Unit[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')

  const [createOpen, setCreateOpen] = useState(false)
  const [createForm, setCreateForm] = useState<MajorForm>(emptyMajor)

  const [editTarget, setEditTarget] = useState<Major | null>(null)
  const [editForm, setEditForm] = useState<MajorForm>(emptyMajor)

  const [deleteTarget, setDeleteTarget] = useState<Major | null>(null)
  const [busy, setBusy] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const [majorList, unitList] = await Promise.all([api.majors.list(), api.units.list()])
      setMajors(majorList)
      setUnits(unitList)
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void load()
  }, [load])

  const unitNames = useMemo(() => {
    const map = new Map<string, string>()
    units.forEach((unit) => map.set(unit.code, unit.name))
    return map
  }, [units])

  const visibleMajors = useMemo(() => {
    const term = search.trim().toLowerCase()
    if (!term) return majors
    return majors.filter((major) => {
      const unitName = unitNames.get(major.unitCode) ?? ''
      return (
        major.code.toLowerCase().includes(term) ||
        major.name.toLowerCase().includes(term) ||
        (major.programType ?? '').toLowerCase().includes(term) ||
        unitName.toLowerCase().includes(term)
      )
    })
  }, [majors, search, unitNames])

  async function submitCreate(event: FormEvent) {
    event.preventDefault()
    setBusy(true)
    try {
      await api.majors.create({
        code: createForm.code.trim(),
        name: createForm.name.trim(),
        instruction_language: createForm.instructionLanguage.trim(),
        unit_code: createForm.unitCode.trim(),
        ...(createForm.programType.trim() ? { program_type: createForm.programType.trim() } : {}),
      })
      notify.success('Đã thêm ngành học')
      setCreateOpen(false)
      setCreateForm(emptyMajor)
      await load()
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  function openEdit(major: Major) {
    setEditTarget(major)
    setEditForm({
      code: major.code,
      name: major.name,
      instructionLanguage: major.instructionLanguage ?? '',
      unitCode: major.unitCode,
      programType: major.programType ?? '',
    })
  }

  async function submitEdit(event: FormEvent) {
    event.preventDefault()
    if (!editTarget) return
    setBusy(true)
    try {
      await api.majors.patch(editTarget.code, {
        name: editForm.name.trim(),
        instruction_language: editForm.instructionLanguage.trim(),
        unit_code: editForm.unitCode.trim(),
        ...(editForm.programType.trim() ? { program_type: editForm.programType.trim() } : {}),
      })
      notify.success('Đã cập nhật ngành học')
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
      await api.majors.remove(deleteTarget.code)
      notify.success('Đã xoá ngành học')
      setDeleteTarget(null)
      await load()
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="page">
      <PageHeader
        eyebrow="Đào tạo"
        title="Ngành học"
        subtitle="Danh mục ngành, ngôn ngữ giảng dạy và đơn vị quản lý chương trình."
        actions={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => void load()} disabled={loading}>
              <Icon name="refresh" size={16} />
              Làm mới
            </button>
            {isAdmin && (
              <button
                type="button"
                className="btn btn-primary"
                onClick={() => {
                  setCreateForm(emptyMajor)
                  setCreateOpen(true)
                }}
              >
                <Icon name="plus" size={16} />
                Thêm ngành
              </button>
            )}
          </>
        }
      />

      <div className="card table-card">
        <div className="table-toolbar">
          <label className="search-box">
            <Icon name="search" size={16} />
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Tìm theo mã, tên ngành, đơn vị…"
            />
          </label>
          <span className="muted">
            {visibleMajors.length} / {majors.length} ngành
          </span>
        </div>

        {loading ? (
          <div className="table-loading">
            <span className="spinner" />
            Đang tải ngành học…
          </div>
        ) : visibleMajors.length === 0 ? (
          <EmptyState
            icon={<Icon name="major" size={26} />}
            title={majors.length === 0 ? 'Chưa có ngành học' : 'Không tìm thấy kết quả'}
            message={
              majors.length === 0
                ? isAdmin
                  ? 'Thêm ngành học đầu tiên để bắt đầu.'
                  : 'Danh mục ngành học hiện đang trống.'
                : 'Thử từ khoá khác hoặc xoá bộ lọc.'
            }
          />
        ) : (
          <div className="table-scroll">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Mã</th>
                  <th>Ngành học</th>
                  <th>Ngôn ngữ</th>
                  <th>Chương trình</th>
                  <th>Đơn vị quản lý</th>
                  {isAdmin && <th className="align-right">Thao tác</th>}
                </tr>
              </thead>
              <tbody>
                {visibleMajors.map((major) => (
                  <tr key={major.code}>
                    <td>
                      <span className="code-chip">{major.code}</span>
                    </td>
                    <td>
                      <strong>{major.name}</strong>
                    </td>
                    <td>{major.instructionLanguage ?? '—'}</td>
                    <td>
                      {major.programType ? (
                        <span className="badge badge-soft">{major.programType}</span>
                      ) : (
                        <span className="muted">—</span>
                      )}
                    </td>
                    <td>
                      <div className="cell-dates">
                        <span>{unitNames.get(major.unitCode) ?? 'Không xác định'}</span>
                        <span className="muted mono">{major.unitCode}</span>
                      </div>
                    </td>
                    {isAdmin && (
                      <td>
                        <div className="row-actions">
                          <button
                            type="button"
                            className="icon-btn"
                            onClick={() => openEdit(major)}
                            aria-label="Sửa"
                            title="Sửa ngành học"
                          >
                            <Icon name="edit" size={16} />
                          </button>
                          <button
                            type="button"
                            className="icon-btn icon-btn-danger"
                            onClick={() => setDeleteTarget(major)}
                            aria-label="Xoá"
                            title="Xoá ngành học"
                          >
                            <Icon name="trash" size={16} />
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <datalist id="unit-options">
        {units.map((unit) => (
          <option key={unit.code} value={unit.code}>
            {unit.name}
          </option>
        ))}
      </datalist>

      <Modal
        open={createOpen}
        title="Thêm ngành học"
        onClose={() => setCreateOpen(false)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setCreateOpen(false)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="create-major-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang lưu…' : 'Thêm ngành'}
            </button>
          </>
        }
      >
        <form id="create-major-form" className="form-stack" onSubmit={submitCreate}>
          <div className="field-row">
            <label className="field">
              <span>Mã ngành</span>
              <input
                value={createForm.code}
                onChange={(event) => setCreateForm({ ...createForm, code: event.target.value })}
                placeholder="IT1"
                maxLength={10}
                required
                autoFocus
              />
            </label>
            <label className="field">
              <span>Mã đơn vị</span>
              <input
                list="unit-options"
                value={createForm.unitCode}
                onChange={(event) => setCreateForm({ ...createForm, unitCode: event.target.value })}
                placeholder="SOICT"
                maxLength={10}
                required
              />
            </label>
          </div>
          <label className="field">
            <span>Tên ngành</span>
            <input
              value={createForm.name}
              onChange={(event) => setCreateForm({ ...createForm, name: event.target.value })}
              placeholder="CNTT: Khoa học Máy tính"
              maxLength={100}
              required
            />
          </label>
          <div className="field-row">
            <label className="field">
              <span>Ngôn ngữ giảng dạy</span>
              <input
                value={createForm.instructionLanguage}
                onChange={(event) =>
                  setCreateForm({ ...createForm, instructionLanguage: event.target.value })
                }
                placeholder="Tiếng Việt"
                maxLength={50}
                required
              />
            </label>
            <label className="field">
              <span>Loại chương trình</span>
              <input
                value={createForm.programType}
                onChange={(event) => setCreateForm({ ...createForm, programType: event.target.value })}
                placeholder="Chương trình chuẩn"
                maxLength={100}
              />
            </label>
          </div>
          {units.length === 0 && (
            <div className="alert alert-info">
              <Icon name="info" size={16} />
              <span>Chưa có đơn vị nào. Hãy tạo đơn vị trước khi thêm ngành học.</span>
            </div>
          )}
        </form>
      </Modal>

      <Modal
        open={editTarget !== null}
        title="Cập nhật ngành học"
        description="Mã ngành không thể thay đổi sau khi tạo."
        onClose={() => setEditTarget(null)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setEditTarget(null)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="edit-major-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang lưu…' : 'Lưu thay đổi'}
            </button>
          </>
        }
      >
        <form id="edit-major-form" className="form-stack" onSubmit={submitEdit}>
          <label className="field">
            <span>Mã ngành</span>
            <input value={editForm.code} readOnly disabled />
          </label>
          <label className="field">
            <span>Tên ngành</span>
            <input
              value={editForm.name}
              onChange={(event) => setEditForm({ ...editForm, name: event.target.value })}
              maxLength={100}
              required
            />
          </label>
          <div className="field-row">
            <label className="field">
              <span>Ngôn ngữ giảng dạy</span>
              <input
                value={editForm.instructionLanguage}
                onChange={(event) =>
                  setEditForm({ ...editForm, instructionLanguage: event.target.value })
                }
                maxLength={50}
                required
              />
            </label>
            <label className="field">
              <span>Mã đơn vị</span>
              <input
                list="unit-options"
                value={editForm.unitCode}
                onChange={(event) => setEditForm({ ...editForm, unitCode: event.target.value })}
                maxLength={10}
                required
              />
            </label>
          </div>
          <label className="field">
            <span>Loại chương trình</span>
            <input
              value={editForm.programType}
              onChange={(event) => setEditForm({ ...editForm, programType: event.target.value })}
              placeholder="Chương trình chuẩn"
              maxLength={100}
            />
          </label>
        </form>
      </Modal>

      <ConfirmDialog
        open={deleteTarget !== null}
        title="Xoá ngành học"
        message={`Ngành "${deleteTarget?.name ?? ''}" (${deleteTarget?.code ?? ''}) sẽ bị xoá khỏi danh mục.`}
        busy={busy}
        onClose={() => setDeleteTarget(null)}
        onConfirm={confirmDelete}
      />
    </div>
  )
}
