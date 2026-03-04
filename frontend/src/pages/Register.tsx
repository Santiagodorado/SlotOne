import { Link } from 'react-router-dom'
import './Login.css'

export default function Register() {
  return (
    <div className="login-page">
      <header className="login-header">
        <h1 className="login-brand">SlotOne</h1>
        <p className="login-tagline">Plataforma de reservas para negocios</p>
      </header>

      <div className="login-card">
        <h2 className="login-title">Registro</h2>
        <p style={{ color: '#6b6b6b', marginBottom: '1rem' }}>
          Formulario de registro en construcción.
        </p>
        <p className="login-register">
          ¿Ya tienes cuenta? <Link to="/login">Iniciar sesión</Link>
        </p>
      </div>
    </div>
  )
}
