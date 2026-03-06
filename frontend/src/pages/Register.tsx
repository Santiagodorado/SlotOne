import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../api/auth'
import './Login.css'

export default function Register() {
  const navigate = useNavigate()
  const [nombre, setNombre] = useState('')
  const [apellido, setApellido] = useState('')
  const [phone, setPhone] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [accountType, setAccountType] = useState<'client' | 'business'>('client')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)

    if (!nombre.trim() || !apellido.trim() || !email.trim() || !password || !confirmPassword) {
      setError('Completa todos los campos obligatorios.')
      return
    }

    if (password.length < 8) {
      setError('La contraseña debe tener mínimo 8 caracteres.')
      return
    }

    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden.')
      return
    }

    setLoading(true)
    try {
      await register({
        nombres: nombre.trim(),
        apellidos: apellido.trim(),
        correo: email.trim(),
        clave: password,
        tipoIdentificacion: 'CC',
        numIdentificacion: 0,
        idRol: accountType === 'client' ? 1 : 2,
      })
      navigate('/login', { state: { registered: true } })
    } catch (err) {
      setError(
        err instanceof Error ? err.message : 'No se pudo crear la cuenta. Intenta de nuevo.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <header className="login-header">
        <h1 className="login-brand">SlotOne</h1>
        <p className="login-tagline">Plataforma de reservas para negocios</p>
      </header>

      <div className="login-card">
        <h2 className="login-title">Crear cuenta</h2>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="nombre">Nombre</label>
              <input
                id="nombre"
                type="text"
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
                placeholder="Pedro"
                autoComplete="given-name"
                disabled={loading}
              />
            </div>
            <div className="form-group">
              <label htmlFor="apellido">Apellido</label>
              <input
                id="apellido"
                type="text"
                value={apellido}
                onChange={(e) => setApellido(e.target.value)}
                placeholder="Gómez"
                autoComplete="family-name"
                disabled={loading}
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="email">Correo electrónico</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="tu@email.com"
              autoComplete="email"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="phone">Teléfono <span className="form-optional">(opcional)</span></label>
            <input
              id="phone"
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="300 123 4567"
              autoComplete="tel"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Contraseña</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              autoComplete="new-password"
              disabled={loading}
            />
            <span className="form-hint">Mínimo 8 caracteres</span>
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirmar contraseña</label>
            <input
              id="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="••••••••"
              autoComplete="new-password"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label>Tipo de cuenta</label>
            <div className="account-type-toggle">
              <button
                type="button"
                className={`toggle-btn ${accountType === 'client' ? 'active' : ''}`}
                onClick={() => setAccountType('client')}
                disabled={loading}
              >
                Soy cliente
              </button>
              <button
                type="button"
                className={`toggle-btn ${accountType === 'business' ? 'active' : ''}`}
                onClick={() => setAccountType('business')}
                disabled={loading}
              >
                Soy negocio
              </button>
            </div>
          </div>

          {error && (
            <div className="login-error" role="alert">
              {error}
            </div>
          )}

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Creando cuenta...' : 'Registrarme'}
          </button>
        </form>

        <hr className="login-separator" />

        <p className="login-register">
          ¿Ya tienes cuenta? <Link to="/login">Iniciar sesión</Link>
        </p>
      </div>
    </div>
  )
}
