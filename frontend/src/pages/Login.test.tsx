import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import Login from './Login'

vi.mock('../api/auth', () => ({
  login: vi.fn(),
}))

import { login } from '../api/auth'

const mockedLogin = vi.mocked(login)

function formScope() {
  const el = document.querySelector('.login-form')
  if (!el) throw new Error('falta .login-form')
  return within(el as HTMLElement)
}

function renderLogin(route = '/login') {
  return render(
    <MemoryRouter initialEntries={[route]}>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<span />} />
      </Routes>
    </MemoryRouter>
  )
}

describe('Login', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('muestra titulo del formulario', () => {
    renderLogin()
    const card = document.querySelector('.login-card')
    expect(card).toBeTruthy()
    expect(within(card as HTMLElement).getByRole('heading', { name: 'Iniciar sesión' })).toBeInTheDocument()
  })

  it('valida campos vacios', async () => {
    const user = userEvent.setup()
    renderLogin()

    await user.click(formScope().getByRole('button', { name: 'Iniciar sesión' }))
    expect(await screen.findByRole('alert')).toHaveTextContent('Ingresa tu correo y contraseña.')
    expect(mockedLogin).not.toHaveBeenCalled()
  })

  it('tras login exitoso guarda sesión', async () => {
    mockedLogin.mockResolvedValueOnce({
      token: 'jwt-test',
      type: 'Bearer',
      id: 9,
      nombres: 'Ana',
      apellidos: 'Ruiz',
      correo: 'ana@test.com',
      telefono: '',
      tipoIdentificacion: 'CC',
      numIdentificacion: '1010',
      rol: 'CLIENT',
    })

    const user = userEvent.setup()
    renderLogin()
    const fm = formScope()

    await user.type(fm.getByLabelText(/correo electrónico/i), 'ana@test.com')
    await user.type(fm.getByLabelText(/^contraseña$/i), 'secret')
    await user.click(fm.getByRole('button', { name: 'Iniciar sesión' }))

    await waitFor(() => {
      expect(mockedLogin).toHaveBeenCalledWith({ email: 'ana@test.com', password: 'secret' })
    })
    expect(localStorage.getItem('token')).toBe('jwt-test')

    const stored = JSON.parse(localStorage.getItem('user') ?? '{}')
    expect(stored.correo).toBe('ana@test.com')
    expect(stored.rol).toBe('CLIENT')
  })
})
