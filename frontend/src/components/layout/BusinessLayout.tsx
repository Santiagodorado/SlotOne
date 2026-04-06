import { NavLink, Link, useNavigate } from 'react-router-dom'
import type { ReactNode } from 'react'
import './BusinessLayout.css'

interface Props {
  children: ReactNode
}

export default function BusinessLayout({ children }: Props) {
  const navigate = useNavigate()

  function handleLogout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    navigate('/login', { replace: true })
  }

  return (
    <div className="bl-wrapper">
      <aside className="bl-sidebar">
        <div className="bl-sidebar-header">
          <Link to="/" className="bl-brand">SlotOne</Link>
          <span className="bl-brand-sub">Panel de negocio</span>
        </div>

        <nav className="bl-nav">
          <NavLink to="/panel" end className={({ isActive }) => `bl-nav-item ${isActive ? 'active' : ''}`}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
            Mi negocio
          </NavLink>
          <NavLink to="/panel/servicios" className={({ isActive }) => `bl-nav-item ${isActive ? 'active' : ''}`}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
            Servicios
          </NavLink>
          <NavLink to="/panel/horarios" className={({ isActive }) => `bl-nav-item ${isActive ? 'active' : ''}`}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
            Horarios
          </NavLink>
        </nav>

        <div className="bl-sidebar-footer">
          <button className="bl-logout" onClick={handleLogout}>
            Cerrar sesión
          </button>
        </div>
      </aside>

      <main className="bl-content">
        {children}
      </main>
    </div>
  )
}
