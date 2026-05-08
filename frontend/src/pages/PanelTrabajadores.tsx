import { useEffect, useState, type FormEvent } from 'react'
import BusinessLayout from '../components/layout/BusinessLayout'
import './Dashboard.css'
import { listarNegociosPorDuenio } from '../api/negocios'
import {
  actualizarTrabajador,
  crearTrabajador,
  eliminarTrabajador,
  listarServicios,
  listarTrabajadores,
  type Servicio,
  type Trabajador,
} from '../api/agenda'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string
}

export default function PanelTrabajadores() {
  const [negocioId, setNegocioId] = useState<number | null>(null)
  const [trabajadores, setTrabajadores] = useState<Trabajador[]>([])
  const [servicios, setServicios] = useState<Servicio[]>([])
  const [editingId, setEditingId] = useState<number | null>(null)
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [telefono, setTelefono] = useState('')
  const [activo, setActivo] = useState(true)
  const [servicioIds, setServicioIds] = useState<number[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (!stored) {
      setError('Debes iniciar sesión como negocio.')
      setLoading(false)
      return
    }
    try {
      const user = JSON.parse(stored) as User
      if (user.rol !== 'BUSINESS' && user.rol !== 'ROLE_BUSINESS') {
        setError('Solo los usuarios con rol negocio pueden ver esta sección.')
        setLoading(false)
        return
      }
      void cargarDatos(user.id)
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
      const id = negocios[0].id
      setNegocioId(id)
      const [svc, trs] = await Promise.all([listarServicios(id), listarTrabajadores(id)])
      setServicios(svc)
      setTrabajadores(trs)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Error al cargar trabajadores.')
    } finally {
      setLoading(false)
    }
  }

  function resetForm() {
    setEditingId(null)
    setNombre('')
    setEmail('')
    setTelefono('')
    setActivo(true)
    setServicioIds([])
  }

  function startEdit(t: Trabajador) {
    setEditingId(t.id)
    setNombre(t.nombre)
    setEmail(t.email ?? '')
    setTelefono(t.telefono ?? '')
    setActivo(t.activo)
    setServicioIds(t.servicioIds ?? [])
    setError(null)
  }

  function toggleServicio(id: number) {
    setServicioIds((prev) => prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id])
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (!negocioId) return
    if (servicioIds.length === 0) {
      setError('Selecciona al menos un servicio que puede atender este trabajador.')
      return
    }
    setError(null)
    const payload = {
      negocioId,
      nombre: nombre.trim(),
      email: email.trim() || undefined,
      telefono: telefono.trim() || undefined,
      activo,
      servicioIds,
    }
    try {
      if (editingId != null) {
        const actualizado = await actualizarTrabajador(editingId, payload)
        setTrabajadores((prev) => prev.map((x) => x.id === editingId ? actualizado : x))
      } else {
        const nuevo = await crearTrabajador(payload)
        setTrabajadores((prev) => [...prev, nuevo])
      }
      resetForm()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo guardar el trabajador.')
    }
  }

  async function handleDelete(id: number) {
    if (!negocioId || !confirm('¿Eliminar este trabajador?')) return
    try {
      await eliminarTrabajador(id, negocioId)
      setTrabajadores((prev) => prev.filter((t) => t.id !== id))
      if (editingId === id) resetForm()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudo eliminar el trabajador.')
    }
  }

  return (
    <BusinessLayout>
      <div className="dashboard-welcome-bar">
        <h1>Trabajadores</h1>
        <span className="dashboard-role">Prestadores</span>
      </div>

      <div className="dash-section">
        <div className="dash-table-wrap">
          <h2>Equipo del negocio</h2>
          {loading && <p className="dash-loading">Cargando...</p>}
          {error && !loading && <p className="dash-alert dash-alert--error">{error}</p>}
          {!loading && !error && trabajadores.length === 0 && (
            <p className="dash-empty">Aún no has registrado trabajadores.</p>
          )}
          {!loading && trabajadores.length > 0 && (
            <table className="dash-table">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Contacto</th>
                  <th>Servicios</th>
                  <th>Estado</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {trabajadores.map((t) => (
                  <tr key={t.id}>
                    <td>{t.nombre}</td>
                    <td>{t.email || '—'}<br /><span className="dash-empty">{t.telefono || 'Sin teléfono'}</span></td>
                    <td>{t.servicioIds.map((id) => servicios.find((s) => s.id === id)?.nombre ?? `#${id}`).join(', ')}</td>
                    <td><span className={`dash-status-badge ${t.activo ? 'success' : 'muted'}`}>{t.activo ? 'Activo' : 'Inactivo'}</span></td>
                    <td className="dash-actions-cell">
                      <button type="button" className="dash-link-btn" onClick={() => startEdit(t)}>Editar</button>
                      <button type="button" className="dash-link-btn danger" onClick={() => void handleDelete(t.id)}>Eliminar</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div className="dash-table-wrap">
          <h2>{editingId != null ? 'Editar trabajador' : 'Crear trabajador'}</h2>
          <form onSubmit={handleSubmit} className="cb-form">
            <div className="form-group">
              <label>Nombre</label>
              <input value={nombre} onChange={(e) => setNombre(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
            </div>
            <div className="form-group">
              <label>Teléfono</label>
              <input value={telefono} onChange={(e) => setTelefono(e.target.value)} />
            </div>
            <label className="dash-check">
              <input type="checkbox" checked={activo} onChange={(e) => setActivo(e.target.checked)} />
              Trabajador activo
            </label>
            <div className="form-group">
              <label>Servicios que puede atender</label>
              <div className="slot-grid">
                {servicios.map((s) => (
                  <button
                    type="button"
                    key={s.id}
                    className={`slot-chip ${servicioIds.includes(s.id) ? 'active' : ''}`}
                    onClick={() => toggleServicio(s.id)}
                  >
                    {s.nombre}
                  </button>
                ))}
              </div>
            </div>
            <div className="dash-form-actions">
              <button type="submit" className="dash-btn dash-btn-primary" disabled={!negocioId}>
                {editingId != null ? 'Actualizar trabajador' : 'Guardar trabajador'}
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
