import { describe, it, expect, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import App from './App'

vi.mock('./api/negocios', () => ({
  listarNegocios: vi.fn(() =>
    Promise.resolve([
      {
        id: 1,
        nombre: 'Spa prueba',
        descripcion: '',
        direccion: '',
        telefono: '',
        correo: '',
        logoUrl: null,
        duenioId: 1,
      },
    ])
  ),
}))

describe('App', () => {
  it('monta la home y el navbar', async () => {
    render(<App />)

    await waitFor(() => {
      expect(screen.getByRole('link', { name: 'SlotOne' })).toBeInTheDocument()
    })

    expect(screen.getByRole('heading', { level: 1, name: /Encuentra y reserva en un clic/i })).toBeInTheDocument()

    await waitFor(() => {
      expect(screen.getByRole('link', { name: /Spa prueba/i })).toBeInTheDocument()
    })
  })
})
