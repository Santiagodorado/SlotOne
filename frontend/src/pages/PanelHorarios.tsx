import { useEffect, useState, type FormEvent } from 'react'
import BusinessLayout from '../components/layout/BusinessLayout'
import './Dashboard.css'
import {
  listarHorarios,
  listarServicios,
  crearHorario,
  actualizarHorario,
  eliminarHorario,
  type Horario,
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

const dias = ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado']

/** Muestra "HH:mm" o "HH:mm:ss" del API en 12 h con a. m. / p. m. */
function formatHoraAmPm(raw: string): string {
  const t = raw?.trim() ?? ''
  if (!t) return '—'
  const [hs, ms] = t.split(':')
  const h = parseInt(hs, 10)
  const m = parseInt(ms ?? '0', 10)
  if (Number.isNaN(h) || Number.isNaN(m)) return raw
  const d = new Date(2000, 0, 1, h, m, 0)
  return d.toLocaleTimeString('es-CO', {
    hour: 'numeric',
    minute: '2-digit',
    hour12: true,
  })
}

function toTimeInputValue(raw: string): string {
  const t = raw?.trim() ?? ''
  return t.length >= 5 ? t.slice(0, 5) : t
}

export default function PanelHorarios() {
  const [user, setUser] = useState<User | null>(null)
  const [negocioId, setNegocioId] = useState<number | null>(null)
  const [servicios, setServicios] = useState<Servicio[]>([])
  const [servicioId, setServicioId] = useState<number | null>(null)
  const [horarios, setHorarios] = useState<Horario[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [saveError, setSaveError] = useState<string | null>(null)
  const [saveOk, setSaveOk] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)

  const [diaSemana, setDiaSemana] = useState(1) // Lunes
  const [horaInicio, setHoraInicio] = useState('08:00')
  const [horaFin, setHoraFin] = useState('18:00')

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
      const [data, svc] = await Promise.all([
        listarHorarios(idNegocio),
        listarServicios(idNegocio),
      ])
      setServicios(svc)
      setServicioId((prev) => {
        if (prev != null && svc.some((s) => s.id === prev)) return prev
        return svc[0]?.id ?? null
      })
      setHorarios(data)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar horarios.')
    } finally {
      setLoading(false)
    }
  }

  function clearSaveFeedback() {
    setSaveError(null)
    setSaveOk(false)
  }

  function cancelarEdicion() {
    setEditingId(null)
    setDiaSemana(1)
    setHoraInicio('08:00')
    setHoraFin('18:00')
    clearSaveFeedback()
  }

  function empezarEditar(h: Horario) {
    setEditingId(h.id)
    if (h.servicioId != null) setServicioId(h.servicioId)
    setDiaSemana(h.diaSemana)
    setHoraInicio(toTimeInputValue(h.horaInicio))
    setHoraFin(toTimeInputValue(h.horaFin))
    clearSaveFeedback()
  }

  async function handleDelete(id: number) {
    if (!negocioId || !confirm('¿Eliminar este horario?')) return
    setSaveError(null)
    setSaveOk(false)
    try {
      await eliminarHorario(id, negocioId)
      setHorarios((prev) => prev.filter((x) => x.id !== id))
      if (editingId === id) cancelarEdicion()
    } catch (e) {
      setSaveError(e instanceof Error ? e.message : 'No se pudo eliminar el horario.')
    }
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (!servicioId) return
    setSaveOk(false)
    setSaveError(null)
    try {
      const payload = { servicioId, diaSemana, horaInicio, horaFin }
      if (editingId != null) {
        const actualizado = await actualizarHorario(editingId, payload)
        setHorarios((prev) => prev.map((x) => (x.id === editingId ? actualizado : x)))
        cancelarEdicion()
      } else {
        const nuevo = await crearHorario(payload)
        setHorarios((prev) => [...prev, nuevo])
      }
      setSaveError(null)
      setSaveOk(true)
    } catch (e) {
      const msg =
        e instanceof Error
          ? e.message
          : 'No se pudo guardar el horario. Revisa los datos o tu conexión.'
      setSaveError(msg)
    }
  }

  return (
    <BusinessLayout>
      <div className="dashboard-welcome-bar">
        <h1>Horarios</h1>
        {user && <span className="dashboard-role">Negocio</span>}
      </div>

      <div className="dash-section">
        <div className="dash-table-wrap">
          <h2>Horarios de atención</h2>
          {loading && <p className="dash-loading">Cargando...</p>}
          {error && !loading && <p className="dash-empty">{error}</p>}
          {!loading && !error && horarios.length === 0 && (
            <p className="dash-empty">Aún no has definido horarios.</p>
          )}
          {!loading && !error && horarios.length > 0 && (
            <table className="dash-table">
              <thead>
                <tr>
                  <th>Servicio</th>
                  <th>Día</th>
                  <th>Inicio</th>
                  <th>Fin</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {horarios.map((h) => (
                  <tr key={h.id}>
                    <td>
                      {h.servicioId != null
                        ? servicios.find((s) => s.id === h.servicioId)?.nombre ?? `#${h.servicioId}`
                        : '—'}
                    </td>
                    <td>{dias[h.diaSemana]}</td>
                    <td>{formatHoraAmPm(h.horaInicio)}</td>
                    <td>{formatHoraAmPm(h.horaFin)}</td>
                    <td className="dash-actions-cell">
                      <button type="button" className="dash-link-btn" onClick={() => empezarEditar(h)}>
                        Editar
                      </button>
                      <button
                        type="button"
                        className="dash-link-btn danger"
                        onClick={() => void handleDelete(h.id)}
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
          <h2>{editingId != null ? 'Editar horario' : 'Agregar horario'}</h2>
          {saveError && (
            <p className="dash-alert dash-alert--error" role="alert">
              <strong>No se puede guardar.</strong> {saveError}
            </p>
          )}
          {saveOk && !saveError && (
            <p className="dash-alert dash-alert--success" role="status">
              Horario guardado correctamente.
            </p>
          )}
          <form onSubmit={handleSubmit} className="cb-form">
            <div className="form-group">
              <label>Servicio</label>
              <select
                value={servicioId ?? ''}
                onChange={(e) => {
                  clearSaveFeedback()
                  setServicioId(Number(e.target.value) || null)
                }}
                disabled={!servicios.length}
              >
                {!servicios.length && <option value="">Sin servicios — créalos en Servicios</option>}
                {servicios.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.nombre}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Día de la semana</label>
              <select
                value={diaSemana}
                onChange={(e) => {
                  clearSaveFeedback()
                  setDiaSemana(Number(e.target.value))
                }}
              >
                {dias.map((d, index) => (
                  <option key={d} value={index}>
                    {d}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Hora inicio</label>
              <input
                type="time"
                value={horaInicio}
                onChange={(e) => {
                  clearSaveFeedback()
                  setHoraInicio(e.target.value)
                }}
              />
            </div>
            <div className="form-group">
              <label>Hora fin</label>
              <input
                type="time"
                value={horaFin}
                onChange={(e) => {
                  clearSaveFeedback()
                  setHoraFin(e.target.value)
                }}
              />
            </div>
            <div className="dash-form-actions">
              <button
                type="submit"
                className="dash-btn dash-btn-primary"
                disabled={!negocioId || !servicioId}
              >
                {editingId != null ? 'Guardar cambios' : 'Guardar horario'}
              </button>
              {editingId != null && (
                <button type="button" className="dash-btn dash-btn-secondary" onClick={cancelarEdicion}>
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

