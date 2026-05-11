import { useEffect, useMemo, useState } from 'react'
import BusinessLayout from '../components/layout/BusinessLayout'
import './Dashboard.css'
import { listarNegociosPorDuenio } from '../api/negocios'
import {
  listarReservasPorNegocio,
  listarServicios,
  listarTrabajadores,
  type Reserva,
  type Servicio,
  type Trabajador,
} from '../api/agenda'
import { estadoClass, estadoLabel } from '../utils/reservaEstado'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string
}

function todayISO() {
  return new Date().toISOString().slice(0, 10)
}

type VistaAgenda = 'dia' | 'semana' | 'mes'
type EstadoFiltro = 'TODOS' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED'

function toISODate(d: Date) {
  return d.toISOString().slice(0, 10)
}

function rangoPorVista(fechaBase: string, vista: VistaAgenda) {
  const base = new Date(`${fechaBase}T00:00:00`)
  if (vista === 'dia') {
    return { desde: toISODate(base), hasta: toISODate(base) }
  }
  if (vista === 'semana') {
    const inicio = new Date(base)
    const offset = (inicio.getDay() + 6) % 7
    inicio.setDate(inicio.getDate() - offset)
    const fin = new Date(inicio)
    fin.setDate(inicio.getDate() + 6)
    return { desde: toISODate(inicio), hasta: toISODate(fin) }
  }
  const inicio = new Date(base.getFullYear(), base.getMonth(), 1)
  const fin = new Date(base.getFullYear(), base.getMonth() + 1, 0)
  return { desde: toISODate(inicio), hasta: toISODate(fin) }
}

function formatHora(raw: string) {
  const [h, m] = raw.split(':').map(Number)
  if (Number.isNaN(h) || Number.isNaN(m)) return raw
  return new Date(2000, 0, 1, h, m).toLocaleTimeString('es-CO', {
    hour: 'numeric',
    minute: '2-digit',
    hour12: true,
  })
}

export default function PanelReservas() {
  const [negocioId, setNegocioId] = useState<number | null>(null)
  const [reservas, setReservas] = useState<Reserva[]>([])
  const [servicios, setServicios] = useState<Servicio[]>([])
  const [trabajadores, setTrabajadores] = useState<Trabajador[]>([])
  const [vista, setVista] = useState<VistaAgenda>('semana')
  const [fechaBase, setFechaBase] = useState(todayISO())
  const [estado, setEstado] = useState<EstadoFiltro>('TODOS')
  const [seleccionada, setSeleccionada] = useState<Reserva | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const rango = useMemo(() => rangoPorVista(fechaBase, vista), [fechaBase, vista])
  const reservasFiltradas = useMemo(
    () => reservas.filter((r) => estado === 'TODOS' || r.estado === estado),
    [reservas, estado]
  )

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
      void cargarBase(user.id)
    } catch {
      setError('Información de usuario inválida. Vuelve a iniciar sesión.')
      setLoading(false)
    }
  }, [])

  async function cargarBase(duenioId: number) {
    try {
      const negocios = await listarNegociosPorDuenio(duenioId)
      if (!negocios.length) {
        setError('Primero debes crear un negocio.')
        return
      }
      const id = negocios[0].id
      setNegocioId(id)
      const rangoInicial = rangoPorVista(fechaBase, vista)
      const [svc, trs, rs] = await Promise.all([
        listarServicios(id),
        listarTrabajadores(id),
        listarReservasPorNegocio(id, rangoInicial.desde, rangoInicial.hasta),
      ])
      setServicios(svc)
      setTrabajadores(trs)
      setReservas(rs)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudieron cargar las reservas.')
    } finally {
      setLoading(false)
    }
  }

  async function filtrar() {
    if (!negocioId) return
    setLoading(true)
    setError(null)
    try {
      setReservas(await listarReservasPorNegocio(negocioId, rango.desde, rango.hasta))
      setSeleccionada(null)
    } catch (e) {
      setError(e instanceof Error ? e.message : 'No se pudieron cargar las reservas.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <BusinessLayout>
      <div className="dashboard-welcome-bar">
        <h1>Reservas</h1>
        <span className="dashboard-role">Agenda</span>
      </div>

      <div className="dash-section">
        <div className="dash-table-wrap">
          <h2>Calendario de citas</h2>
          <div className="dash-filter-row">
            <div className="form-group">
              <label>Vista</label>
              <select value={vista} onChange={(e) => setVista(e.target.value as VistaAgenda)}>
                <option value="dia">Día</option>
                <option value="semana">Semana</option>
                <option value="mes">Mes</option>
              </select>
            </div>
            <div className="form-group">
              <label>Fecha base</label>
              <input type="date" value={fechaBase} onChange={(e) => setFechaBase(e.target.value)} />
            </div>
            <div className="form-group">
              <label>Estado</label>
              <select value={estado} onChange={(e) => setEstado(e.target.value as EstadoFiltro)}>
                <option value="TODOS">Todos</option>
                <option value="CONFIRMED">Confirmadas</option>
                <option value="CANCELLED">Canceladas</option>
                <option value="COMPLETED">Completadas</option>
              </select>
            </div>
          </div>
          <p className="dash-hint">Rango consultado: {rango.desde} a {rango.hasta}</p>
          <button type="button" className="dash-btn dash-btn-primary" onClick={() => void filtrar()}>
            Consultar
          </button>
        </div>

        <div className="dash-table-wrap">
          <h2>Citas del negocio</h2>
          {loading && <p className="dash-loading">Cargando...</p>}
          {error && !loading && <p className="dash-alert dash-alert--error">{error}</p>}
          {!loading && !error && reservasFiltradas.length === 0 && (
            <p className="dash-empty">No hay reservas para el rango seleccionado.</p>
          )}
          {!loading && !error && reservasFiltradas.length > 0 && (
            <table className="dash-table">
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Hora</th>
                  <th>Cliente</th>
                  <th>Servicio</th>
                  <th>Trabajador</th>
                  <th>Estado</th>
                  <th>Código</th>
                </tr>
              </thead>
              <tbody>
                {reservasFiltradas.map((r) => (
                  <tr key={r.id} className="dash-clickable-row" onClick={() => setSeleccionada(r)}>
                    <td>{r.fecha}</td>
                    <td>{formatHora(r.horaInicio)} - {formatHora(r.horaFin)}</td>
                    <td>{r.clienteNombre}<br /><span className="dash-empty">{r.clienteTelefono}</span></td>
                    <td>{servicios.find((s) => s.id === r.servicioId)?.nombre ?? `#${r.servicioId}`}</td>
                    <td>{trabajadores.find((t) => t.id === r.trabajadorId)?.nombre ?? `#${r.trabajadorId ?? '—'}`}</td>
                    <td>
                      <span className={`dash-status-badge ${estadoClass(r.estado)}`}>
                        {estadoLabel(r.estado)}
                      </span>
                    </td>
                    <td>{r.codigoReserva}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {seleccionada && (
          <div className="dash-table-wrap">
            <h2>Detalle de cita</h2>
            <div className="reservation-detail">
              <p><strong>Código:</strong> {seleccionada.codigoReserva}</p>
              <p><strong>Cliente:</strong> {seleccionada.clienteNombre}</p>
              <p><strong>Email:</strong> {seleccionada.clienteEmail}</p>
              <p><strong>Teléfono:</strong> {seleccionada.clienteTelefono}</p>
              <p><strong>Servicio:</strong> {servicios.find((s) => s.id === seleccionada.servicioId)?.nombre ?? `#${seleccionada.servicioId}`}</p>
              <p><strong>Trabajador:</strong> {trabajadores.find((t) => t.id === seleccionada.trabajadorId)?.nombre ?? `#${seleccionada.trabajadorId ?? '—'}`}</p>
              <p><strong>Fecha y hora:</strong> {seleccionada.fecha}, {formatHora(seleccionada.horaInicio)} - {formatHora(seleccionada.horaFin)}</p>
              <p><strong>Estado:</strong> {estadoLabel(seleccionada.estado)}</p>
              {seleccionada.notas && <p><strong>Notas:</strong> {seleccionada.notas}</p>}
            </div>
            <button type="button" className="dash-btn dash-btn-secondary" onClick={() => setSeleccionada(null)}>
              Cerrar detalle
            </button>
          </div>
        )}
      </div>
    </BusinessLayout>
  )
}
