import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch } from '../api/client'
import Navbar from '../components/layout/Navbar'
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
  CLIENT: 'Cliente', BUSINESS: 'Negocio', PLATFORM_ADMIN: 'Administrador',
  ROLE_CLIENT: 'Cliente', ROLE_BUSINESS: 'Negocio', ROLE_PLATFORM_ADMIN: 'Administrador',
}

export default function AdminPanel() {
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [users, setUsers] = useState<UserListItem[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (!stored) { navigate('/login', { replace: true }); return }
    try {
      const parsed = JSON.parse(stored)
      if (parsed.rol !== 'PLATFORM_ADMIN' && parsed.rol !== 'ROLE_PLATFORM_ADMIN') {
        navigate('/', { replace: true }); return
      }
      setUser(parsed)
    } catch { navigate('/login', { replace: true }) }
  }, [navigate])

  useEffect(() => {
    if (!user) return
    const token = localStorage.getItem('token')
    if (!token) return

    setLoading(true)
    apiFetch<UserListItem[]>('/usuarios', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(setUsers)
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [user])

  if (!user) return null

  return (
    <div className="dashboard-page">
      <Navbar />
      <main className="dashboard-main">
        <div className="dashboard-welcome-bar">
          <h1>Panel de administración</h1>
          <span className="dashboard-role">Administrador</span>
        </div>

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
      </main>
    </div>
  )
}
