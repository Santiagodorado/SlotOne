import { useEffect, useState, type FormEvent } from 'react'
import BusinessLayout from '../components/layout/BusinessLayout'
import './Dashboard.css'
import {
  listarServicios,
  crearServicio,
  actualizarServicio,
  eliminarServicio,
  type Servicio,
} from '../api/agenda'
import { listarNegociosPorDuenio } from '../api/negocios'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string
}

function toTotalMinutes(horas: number, minutos: number): number {
  return horas * 60 + minutos
}

export default function PanelServicios() {
  const [user, setUser] = useState<User | null>(null)
  const [negocioId, setNegocioId] = useState<number | null>(null)
  const [servicios, setServicios] = useState<Servicio[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [editingId, setEditingId] = useState<number | null>(null)
  const [nombre, setNombre] = useState('')
  const [duracionHoras, setDuracionHoras] = useState(0)
  const [duracionMinutos, setDuracionMinutos] = useState(30)
  const [precio, setPrecio] = useState(0)
  const [descripcion, setDescripcion] = useState('')

  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (!stored) {
      setError('Debes iniciar sesión como negocio.')
      setLoading(false)
      return
    }
    try {
      const parsed = JSON.parse(stored) as User
      if (parsed.rol !== 'BUSINESS' && parsed.rol !== 'ROLE_BUSINESS') {
        setError('Solo los usuarios con rol negocio pueden ver esta sección.')
        setLoading(false)
        return
      }
      setUser(parsed)
      void cargarDatos(parsed.id)
    } catch {
      setError('Información de usuario inválida. Vuelve a iniciar sesión.')
      setLoading(false)
    }
  }, [])

  async function cargarDatos(duenioId: number) {
    try {
      const negocios = await listarNegociosPorDuenio(duenioId)
      if (!negocios.length) {
        setError('Primero debes crear un negocio.')
        return
      }
      const idNegocio = negocios[0].id
      setNegocioId(idNegocio)
      const data = await listarServicios(idNegocio)
      setServicios(data)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar servicios.')
    } finally {
      setLoading(false)
    }
  }

  function resetForm() {
    setEditingId(null)
    setNombre('')
    setDuracionHoras(0)
    setDuracionMinutos(30)
    setPrecio(0)
    setDescripcion('')
  }

  function startEdit(s: Servicio) {
    setEditingId(s.id)
    setNombre(s.nombre)
    const total = s.duracionMinutos
    setDuracionHoras(Math.floor(total / 60))
    setDuracionMinutos(total % 60)
    setPrecio(s.precio)
    setDescripcion(s.descripcion ?? '')
    setError(null)
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (!negocioId) return
    const totalMin = toTotalMinutes(duracionHoras, duracionMinutos)
    if (totalMin < 1) {
      setError('La duración total debe ser mayor a cero (usa horas y/o minutos).')
      return
    }
    if (duracionMinutos < 0 || duracionMinutos > 59) {
      setError('Los minutos deben estar entre 0 y 59.')
      return
    }

    setError(null)
    try {
      const payload = {
        negocioId,
        nombre: nombre.trim(),
        duracionMinutos: totalMin,
        precio,
        descripcion: descripcion.trim() || undefined,
      }
      if (editingId != null) {
        const actualizado = await actualizarServicio(editingId, payload)
        setServicios((prev) => prev.map((x) => (x.id === editingId ? actualizado : x)))
        resetForm()
      } else {
        const nuevo = await crearServicio(payload)
        setServicios((prev) => [...prev, nuevo])
        resetForm()
      }
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo guardar el servicio.')
    }
  }

  async function handleDelete(id: number) {
    if (!negocioId || !confirm('¿Eliminar este servicio?')) return
    try {
      await eliminarServicio(id, negocioId)
      setServicios((prev) => prev.filter((s) => s.id !== id))
      if (editingId === id) resetForm()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo eliminar.')
    }
  }

  return (
    <BusinessLayout>
      <div className="dashboard-welcome-bar">
        <h1>Servicios</h1>
        {user && <span className="dashboard-role">Negocio</span>}
      </div>

      <div className="dash-section">
        <div className="dash-cards">
          <div className="dash-card">
            <span className="dash-card-value">{servicios.length}</span>
            <span className="dash-card-label">Servicios definidos</span>
          </div>
        </div>

        <div className="dash-table-wrap">
          <h2>Mis servicios</h2>
          {loading && <p className="dash-loading">Cargando...</p>}
          {error && !loading && <p className="dash-empty">{error}</p>}
          {!loading && !error && servicios.length === 0 && (
            <p className="dash-empty">Aún no has registrado servicios.</p>
          )}
          {!loading && !error && servicios.length > 0 && (
            <table className="dash-table">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Duración</th>
                  <th>Precio</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {servicios.map((s) => (
                  <tr key={s.id}>
                    <td>{s.nombre}</td>
                    <td>{s.duracionMinutos} min</td>
                    <td>${s.precio.toFixed(0)}</td>
                    <td className="dash-actions-cell">
                      <button type="button" className="dash-link-btn" onClick={() => startEdit(s)}>
                        Editar
                      </button>
                      <button
                        type="button"
                        className="dash-link-btn danger"
                        onClick={() => void handleDelete(s.id)}
                      >
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div className="dash-table-wrap">
          <h2>{editingId != null ? 'Editar servicio' : 'Crear servicio'}</h2>
          <form onSubmit={handleSubmit} className="cb-form">
            <div className="form-group">
              <label>Nombre</label>
              <input value={nombre} onChange={(e) => setNombre(e.target.value)} required />
            </div>
            <div className="form-row-duracion">
              <div className="form-group">
                <label>Horas</label>
                <input
                  type="number"
                  min={0}
                  value={duracionHoras}
                  onChange={(e) => setDuracionHoras(Number(e.target.value))}
                  required
                />
              </div>
              <div className="form-group">
                <label>Minutos (0–59)</label>
                <input
                  type="number"
                  min={0}
                  max={59}
                  value={duracionMinutos}
                  onChange={(e) => setDuracionMinutos(Number(e.target.value))}
                  required
                />
              </div>
            </div>
            <p className="dash-hint">
              Duración total:{' '}
              <strong>
                {toTotalMinutes(duracionHoras, duracionMinutos) || 0} minutos
              </strong>
            </p>
            <div className="form-group">
              <label>Precio</label>
              <input
                type="number"
                min={0}
                step="0.01"
                value={precio}
                onChange={(e) => setPrecio(Number(e.target.value))}
                required
              />
            </div>
            <div className="form-group">
              <label>Descripción (opcional)</label>
              <textarea value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
            </div>
            <div className="dash-form-actions">
              <button type="submit" className="dash-btn dash-btn-primary" disabled={!negocioId}>
                {editingId != null ? 'Actualizar servicio' : 'Guardar servicio'}
              </button>
              {editingId != null && (
                <button type="button" className="dash-btn dash-btn-secondary" onClick={resetForm}>
                  Cancelar edición
                </button>
              )}
            </div>
          </form>
        </div>
      </div>
    </BusinessLayout>
  )
}
