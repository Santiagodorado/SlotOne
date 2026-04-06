import { Link, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import './Navbar.css'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  rol: string
}

export default function Navbar() {
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [menuOpen, setMenuOpen] = useState(false)

  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (stored) {
      try { setUser(JSON.parse(stored)) } catch { /* empty */ }
    }
  }, [])

  function handleLogout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
    setMenuOpen(false)
    navigate('/login', { replace: true })
  }

  const displayName = user
    ? ([user.nombres, user.apellidos].filter(Boolean).join(' ') || user.correo)
    : null

  const isBusinessRole = user?.rol === 'BUSINESS' || user?.rol === 'ROLE_BUSINESS'
  const isAdminRole = user?.rol === 'PLATFORM_ADMIN' || user?.rol === 'ROLE_PLATFORM_ADMIN'

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">SlotOne</Link>

      <div className="navbar-right">
        {user ? (
          <div className="navbar-user">
            {isBusinessRole && (
              <Link to="/panel" className="navbar-link">Mi panel</Link>
            )}
            {isAdminRole && (
              <Link to="/admin" className="navbar-link">Admin</Link>
            )}
            <button className="navbar-user-btn" onClick={() => setMenuOpen(!menuOpen)}>
              {displayName}
            </button>
            {menuOpen && (
              <div className="navbar-dropdown">
                <Link to="/mis-reservas" className="navbar-dropdown-item" onClick={() => setMenuOpen(false)}>
                  Mis reservas
                </Link>
                <button className="navbar-dropdown-item" onClick={handleLogout}>
                  Cerrar sesión
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="navbar-auth">
            <Link to="/login" className="navbar-link">Iniciar sesión</Link>
            <Link to="/registro" className="navbar-btn">Registrarse</Link>
          </div>
        )}
      </div>
    </nav>
  )
}
