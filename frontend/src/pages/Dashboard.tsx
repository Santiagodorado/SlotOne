import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Dashboard.css'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string
}

const rolLabels: Record<string, string> = {
  ROLE_CLIENT: 'Cliente',
  ROLE_BUSINESS: 'Negocio',
  ROLE_PLATFORM_ADMIN: 'Administrador',
}

export default function Dashboard() {
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)

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

  function handleLogout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    navigate('/login', { replace: true })
  }

  if (!user) return null

  const displayName = [user.nombres, user.apellidos].filter(Boolean).join(' ') || user.correo
  const rolLabel = rolLabels[user.rol] ?? user.rol

  return (
    <div className="dashboard-page">
      <nav className="dashboard-nav">
        <span className="dashboard-nav-brand">SlotOne</span>
        <button className="dashboard-logout" onClick={handleLogout}>
          Cerrar sesión
        </button>
      </nav>

      <main className="dashboard-content">
        <div className="dashboard-welcome">
          <h1>Bienvenido, {displayName}</h1>
          <span className="dashboard-role">{rolLabel}</span>
        </div>
      </main>
    </div>
  )
}
