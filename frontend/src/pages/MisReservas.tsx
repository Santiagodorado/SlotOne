import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/layout/Navbar'
import './Home.css'
import './Dashboard.css'
import { listarNegocios, type Negocio } from '../api/negocios'
import { listarReservasPorCliente, listarServicios, type Reserva, type Servicio } from '../api/agenda'
import { estadoClass, estadoLabel } from '../utils/reservaEstado'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string
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

export default function MisReservas() {
  const [reservas, setReservas] = useState<Reserva[]>([])
  const [negocios, setNegocios] = useState<Negocio[]>([])
  const [servicios, setServicios] = useState<Servicio[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function load() {
      const stored = localStorage.getItem('user')
      if (!stored) {
        setError('Debes iniciar sesión para ver tus reservas.')
        setLoading(false)
        return
      }
      try {
        const user = JSON.parse(stored) as User
        const [rs, ns] = await Promise.all([
          listarReservasPorCliente(user.id),
          listarNegocios(),
        ])
        setReservas(rs)
        setNegocios(ns)
        const ids = Array.from(new Set(rs.map((r) => r.negocioId)))
        const svc = (await Promise.all(ids.map((id) => listarServicios(id)))).flat()
        setServicios(svc)
      } catch (e) {
        setError(e instanceof Error ? e.message : 'No se pudieron cargar tus reservas.')
      } finally {
        setLoading(false)
      }
    }
    void load()
  }, [])

  return (
    <div className="home-page">
      <Navbar />
      <main className="business-detail">
        <div className="dashboard-welcome-bar">
          <h1>Mis reservas</h1>
          <Link to="/" className="dash-btn dash-btn-secondary">Buscar negocios</Link>
        </div>
        <div className="dash-table-wrap">
          {loading && <p className="dash-loading">Cargando...</p>}
          {error && !loading && <p className="dash-alert dash-alert--error">{error}</p>}
          {!loading && !error && reservas.length === 0 && (
            <p className="dash-empty">Aún no tienes reservas registradas.</p>
          )}
          {!loading && !error && reservas.length > 0 && (
            <table className="dash-table">
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Negocio</th>
                  <th>Servicio</th>
                  <th>Fecha</th>
                  <th>Hora</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {reservas.map((r) => (
                  <tr key={r.id}>
                    <td>{r.codigoReserva}</td>
                    <td>{negocios.find((n) => n.id === r.negocioId)?.nombre ?? `#${r.negocioId}`}</td>
                    <td>{servicios.find((s) => s.id === r.servicioId)?.nombre ?? `#${r.servicioId}`}</td>
                    <td>{r.fecha}</td>
                    <td>{formatHora(r.horaInicio)} - {formatHora(r.horaFin)}</td>
                    <td>
                      <span className={`dash-status-badge ${estadoClass(r.estado)}`}>
                        {estadoLabel(r.estado)}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </div>
  )
}
