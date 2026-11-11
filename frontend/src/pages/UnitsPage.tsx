import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react'
import { api, errorMessage } from '../api/client'
import type { Major, Unit } from '../api/types'
import { notify } from '../store/uiStore'
import { PageHeader, EmptyState } from '../components/Page'
import { Icon } from '../components/Icon'
import { Modal } from '../components/Modal'
import { ConfirmDialog } from '../components/ConfirmDialog'

export function UnitsPage() {
  const [units, setUnits] = useState<Unit[]>([])
  const [majors, setMajors] = useState<Major[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [createOpen, setCreateOpen] = useState(false)
  const [code, setCode] = useState('')
  const [name, setName] = useState('')
  const [deleteTarget, setDeleteTarget] = useState<Unit | null>(null)
  const [busy, setBusy] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const [unitList, majorList] = await Promise.all([api.units.list(), api.majors.list()])
      setUnits(unitList)
      setMajors(majorList)
    } catch (error) {
      notify.error(errorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void load()
  }, [load])

  const majorCounts = useMemo(() => {
    const map = new Map<string, number>()
    majors.forEach((major) => map.set(major.unitCode, (map.get(major.unitCode) ?? 0) + 1))
    return map
  }, [majors])

  const visibleUnits = useMemo(() => {
    const term = search.trim().toLowerCase()
    if (!term) return units
    return units.filter(
      (unit) => unit.code.toLowerCase().includes(term) || unit.name.toLowerCase().includes(term),
    )
  }, [units, search])

  async function submitCreate(event: FormEvent) {
    event.preventDefault()
    setBusy(true)
    try {
      await api.units.create({ code: code.trim(), name: name.trim() })
      notify.success('Đã thêm đơn vị')
      setCreateOpen(false)
      setCode('')
      setName('')
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
      await api.units.remove(deleteTarget.code)
      notify.success('Đã xoá đơn vị')
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
        eyebrow="Tổ chức"
        title="Đơn vị quản lý"
        subtitle="Khoa, trường và bộ môn trực thuộc quản lý các ngành đào tạo."
        actions={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => void load()} disabled={loading}>
              <Icon name="refresh" size={16} />
              Làm mới
            </button>
            <button type="button" className="btn btn-primary" onClick={() => setCreateOpen(true)}>
              <Icon name="plus" size={16} />
              Thêm đơn vị
            </button>
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
              placeholder="Tìm theo mã hoặc tên đơn vị…"
            />
          </label>
          <span className="muted">
            {visibleUnits.length} / {units.length} đơn vị
          </span>
        </div>

        {loading ? (
          <div className="table-loading">
            <span className="spinner" />
            Đang tải đơn vị…
          </div>
        ) : visibleUnits.length === 0 ? (
          <EmptyState
            icon={<Icon name="unit" size={26} />}
            title={units.length === 0 ? 'Chưa có đơn vị' : 'Không tìm thấy kết quả'}
            message={
              units.length === 0
                ? 'Tạo đơn vị đầu tiên trước khi thêm ngành học.'
                : 'Thử từ khoá khác hoặc xoá bộ lọc.'
            }
            action={
              units.length === 0 ? (
                <button type="button" className="btn btn-primary" onClick={() => setCreateOpen(true)}>
                  <Icon name="plus" size={16} />
                  Thêm đơn vị
                </button>
              ) : undefined
            }
          />
        ) : (
          <div className="table-scroll">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Mã đơn vị</th>
                  <th>Tên đơn vị</th>
                  <th>Số ngành quản lý</th>
                  <th className="align-right">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {visibleUnits.map((unit) => {
                  const count = majorCounts.get(unit.code) ?? 0
                  return (
                    <tr key={unit.code}>
                      <td>
                        <span className="code-chip">{unit.code}</span>
                      </td>
                      <td>
                        <strong>{unit.name}</strong>
                      </td>
                      <td>
                        <span className={`badge ${count > 0 ? 'badge-soft' : 'badge-muted'}`}>
                          {count} ngành
                        </span>
                      </td>
                      <td>
                        <div className="row-actions">
                          <button
                            type="button"
                            className="icon-btn icon-btn-danger"
                            onClick={() => setDeleteTarget(unit)}
                            aria-label="Xoá"
                            title="Xoá đơn vị"
                          >
                            <Icon name="trash" size={16} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <Modal
        open={createOpen}
        title="Thêm đơn vị"
        onClose={() => setCreateOpen(false)}
        footer={
          <>
            <button type="button" className="btn btn-ghost" onClick={() => setCreateOpen(false)} disabled={busy}>
              Huỷ
            </button>
            <button type="submit" form="create-unit-form" className="btn btn-primary" disabled={busy}>
              {busy ? 'Đang lưu…' : 'Thêm đơn vị'}
            </button>
          </>
        }
      >
        <form id="create-unit-form" className="form-stack" onSubmit={submitCreate}>
          <label className="field">
            <span>Mã đơn vị</span>
            <input
              value={code}
              onChange={(event) => setCode(event.target.value)}
              placeholder="SOICT"
              required
              autoFocus
            />
          </label>
          <label className="field">
            <span>Tên đơn vị</span>
            <input
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="Trường Công nghệ Thông tin và Truyền thông"
              required
            />
          </label>
        </form>
      </Modal>

      <ConfirmDialog
        open={deleteTarget !== null}
        title="Xoá đơn vị"
        message={
          deleteTarget && (majorCounts.get(deleteTarget.code) ?? 0) > 0
            ? `Đơn vị "${deleteTarget.name}" đang quản lý ${majorCounts.get(deleteTarget.code)} ngành. Hãy chuyển các ngành sang đơn vị khác trước khi xoá.`
            : `Đơn vị "${deleteTarget?.name ?? ''}" sẽ bị xoá khỏi danh mục.`
        }
        busy={busy}
        onClose={() => setDeleteTarget(null)}
        onConfirm={confirmDelete}
      />
    </div>
  )
}
