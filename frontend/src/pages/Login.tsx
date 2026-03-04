import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { login } from '../api/auth'
import './Login.css'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)

    if (!email.trim() || !password) {
      setError('Ingresa tu correo y contraseña.')
      return
    }

    setLoading(true)
    try {
      const { token, user } = await login({ email: email.trim(), password })
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      window.location.href = '/'
    } catch (err) {
      setError(
        err instanceof Error ? err.message : 'Credenciales incorrectas. Verifica tu correo y contraseña.'
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
        <h2 className="login-title">Iniciar sesión</h2>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="email">Correo electrónico</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="tu@email.com"
              autoComplete="email"
              className={error ? 'input-error' : ''}
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
              autoComplete="current-password"
              className={error ? 'input-error' : ''}
              disabled={loading}
            />
          </div>

          {error && (
            <div className="login-error" role="alert">
              {error}
            </div>
          )}

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Iniciando sesión...' : 'Iniciar sesión'}
          </button>
        </form>

        <hr className="login-separator" />

        <p className="login-register">
          ¿No tienes cuenta? <Link to="/registro">Regístrate</Link>
        </p>
      </div>
    </div>
  )
}
