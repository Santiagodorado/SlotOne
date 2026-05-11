import { describe, it, expect, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import Navbar from './Navbar'

describe('Navbar', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('invitado: enlaces a login y registro', async () => {
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    )

    await waitFor(() => {
      expect(screen.getByRole('link', { name: /iniciar sesión/i })).toBeInTheDocument()
    })
    expect(screen.getByRole('link', { name: /registrarse/i })).toBeInTheDocument()
  })

  it('usuario BUSINESS: muestra Mi panel', async () => {
    localStorage.setItem(
      'user',
      JSON.stringify({
        id: 2,
        nombres: 'Pedro',
        apellidos: 'Negocio',
        correo: 'p@ejemplo.local',
        rol: 'BUSINESS',
      })
    )

    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    )

    await waitFor(() => {
      expect(screen.getByRole('link', { name: 'Mi panel' })).toBeInTheDocument()
    })
  })

  it('usuario CLIENT: muestra botón con nombre y menú tras click', async () => {
    localStorage.setItem(
      'user',
      JSON.stringify({
        id: 1,
        nombres: 'Laura',
        apellidos: 'Cliente',
        correo: 'l@ejemplo.local',
        rol: 'CLIENT',
      })
    )

    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    )

    const user = userEvent.setup()
    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Laura Cliente/i })).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: /Laura Cliente/i }))

    expect(screen.getByRole('link', { name: /mis reservas/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /cerrar sesión/i })).toBeInTheDocument()
  })
})
