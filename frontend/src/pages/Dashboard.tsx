import { useEffect, useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { apiFetch } from '../api/client'
import BusinessLayout from '../components/layout/BusinessLayout'
import './Dashboard.css'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string
}

interface UserListItem {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string | { id: number; nombre: string }
}

const rolLabels: Record<string, string> = {
  CLIENT: 'Cliente',
  BUSINESS: 'Negocio',
  PLATFORM_ADMIN: 'Administrador',
  ROLE_CLIENT: 'Cliente',
  ROLE_BUSINESS: 'Negocio',
  ROLE_PLATFORM_ADMIN: 'Administrador',
}

export default function Dashboard() {
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [users, setUsers] = useState<UserListItem[]>([])
  const [loadingUsers, setLoadingUsers] = useState(false)

  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (!stored) {
      navigate('/login', { replace: true })
      return
    }
    try {
      setUser(JSON.parse(stored))
    } catch {
      navigate('/login', { replace: true })
    }
  }, [navigate])

  useEffect(() => {
    if (user?.rol !== 'PLATFORM_ADMIN' && user?.rol !== 'ROLE_PLATFORM_ADMIN') return
    const token = localStorage.getItem('token')
    if (!token) return

    setLoadingUsers(true)
    apiFetch<UserListItem[]>('/usuarios', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(setUsers)
      .catch(() => {})
      .finally(() => setLoadingUsers(false))
  }, [user])

  function handleLogout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    navigate('/login', { replace: true })
  }

  if (!user) return null

  const displayName = [user.nombres, user.apellidos].filter(Boolean).join(' ') || user.correo
  const rolLabel = rolLabels[user.rol] ?? user.rol

  if (user.rol === 'BUSINESS' || user.rol === 'ROLE_BUSINESS') {
    return (
      <BusinessLayout>
        <div className="dashboard-welcome-bar">
          <h1>Bienvenido, {displayName}</h1>
          <span className="dashboard-role">{rolLabel}</span>
        </div>
        <BusinessDashboard />
      </BusinessLayout>
    )
  }

  return (
    <div className="dashboard-page">
      <nav className="dashboard-nav">
        <span className="dashboard-nav-brand">SlotOne</span>
        <div className="dashboard-nav-right">
          <span className="dashboard-nav-user">{displayName}</span>
          <button className="dashboard-logout" onClick={handleLogout}>
            Cerrar sesión
          </button>
        </div>
      </nav>

      <main className="dashboard-main">
        <div className="dashboard-welcome-bar">
          <h1>Bienvenido, {displayName}</h1>
          <span className="dashboard-role">{rolLabel}</span>
        </div>

        {(user.rol === 'CLIENT' || user.rol === 'ROLE_CLIENT') && <ClientDashboard />}
        {(user.rol === 'PLATFORM_ADMIN' || user.rol === 'ROLE_PLATFORM_ADMIN') && (
          <AdminDashboard users={users} loading={loadingUsers} />
        )}
      </main>
    </div>
  )
}

function BusinessDashboard() {
  return (
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
        <Link to="/crear-negocio" className="dash-action-btn">Crear mi negocio</Link>
        <button className="dash-action-btn outline" disabled>Crear servicio</button>
        <button className="dash-action-btn outline" disabled>Ver calendario</button>
      </div>
    </div>
  )
}

function ClientDashboard() {
  return (
    <div className="dash-section">
      <div className="dash-cards">
        <div className="dash-card">
          <span className="dash-card-value">0</span>
          <span className="dash-card-label">Mis reservas</span>
        </div>
        <div className="dash-card">
          <span className="dash-card-value">0</span>
          <span className="dash-card-label">Próximas</span>
        </div>
      </div>

      <div className="dash-search-box">
        <h2>Buscar negocios</h2>
        <div className="dash-search-input-wrap">
          <input
            type="text"
            placeholder="Peluquería, gimnasio, consultorio..."
            className="dash-search-input"
            disabled
          />
          <button className="dash-search-btn" disabled>Buscar</button>
        </div>
        <p className="dash-search-hint">Próximamente podrás buscar negocios y reservar citas.</p>
      </div>
    </div>
  )
}

function AdminDashboard({ users, loading }: { users: UserListItem[]; loading: boolean }) {
  return (
    <div className="dash-section">
      <div className="dash-cards">
        <div className="dash-card">
          <span className="dash-card-value">{users.length}</span>
          <span className="dash-card-label">Usuarios registrados</span>
        </div>
      </div>

      <div className="dash-table-wrap">
        <h2>Usuarios del sistema</h2>
        {loading ? (
          <p className="dash-loading">Cargando usuarios...</p>
        ) : users.length === 0 ? (
          <p className="dash-empty">No se encontraron usuarios.</p>
        ) : (
          <table className="dash-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Correo</th>
                <th>Rol</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{[u.nombres, u.apellidos].filter(Boolean).join(' ')}</td>
                  <td>{u.correo}</td>
                  <td>
                    <span className="dash-role-badge">
                      {typeof u.rol === 'object' 
                        ? (rolLabels[u.rol.nombre] ?? u.rol.nombre)
                        : (rolLabels[u.rol] ?? u.rol)}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
