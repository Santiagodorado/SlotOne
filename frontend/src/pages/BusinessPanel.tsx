import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import BusinessLayout from '../components/layout/BusinessLayout'
import { listarNegociosPorDuenio } from '../api/negocios'
import './Dashboard.css'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string
}

export default function BusinessPanel() {
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [tieneNegocio, setTieneNegocio] = useState<boolean | null>(null)

  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (!stored) {
      navigate('/login', { replace: true })
      return
    }
    try {
      const parsed = JSON.parse(stored)
      if (parsed.rol !== 'BUSINESS' && parsed.rol !== 'ROLE_BUSINESS') {
        navigate('/', { replace: true })
        return
      }
      setUser(parsed)
      void listarNegociosPorDuenio(parsed.id)
        .then((list) => setTieneNegocio(list.length > 0))
        .catch(() => setTieneNegocio(false))
    } catch {
      navigate('/login', { replace: true })
    }
  }, [navigate])

  if (!user) return null
  const displayName = [user.nombres, user.apellidos].filter(Boolean).join(' ') || user.correo

  return (
    <BusinessLayout>
      <div className="dashboard-welcome-bar">
        <h1>Bienvenido, {displayName}</h1>
        <span className="dashboard-role">Negocio</span>
      </div>
      <div className="dash-section">
        <div className="dash-cards">
          <div className="dash-card">
            <span className="dash-card-value">0</span>
            <span className="dash-card-label">Reservas hoy</span>
          </div>
          <div className="dash-card">
            <span className="dash-card-value">0</span>
            <span className="dash-card-label">Pendientes</span>
          </div>
          <div className="dash-card">
            <span className="dash-card-value">0</span>
            <span className="dash-card-label">Servicios activos</span>
          </div>
          <div className="dash-card">
            <span className="dash-card-value">0</span>
            <span className="dash-card-label">Clientes</span>
          </div>
        </div>
        <div className="dash-actions">
          {tieneNegocio === false && (
            <Link to="/panel/crear" className="dash-action-btn">
              Crear mi negocio
            </Link>
          )}
          {tieneNegocio === true && (
            <Link to="/panel/editar" className="dash-action-btn">
              Editar datos del negocio
            </Link>
          )}
          <Link
            to="/panel/servicios"
            className={`dash-action-btn ${tieneNegocio === false ? 'outline' : ''}`}
          >
            Servicios
          </Link>
          <Link
            to="/panel/horarios"
            className={`dash-action-btn ${tieneNegocio === false ? 'outline' : ''}`}
          >
            Horarios
          </Link>
        </div>
      </div>
    </BusinessLayout>
  )
}
