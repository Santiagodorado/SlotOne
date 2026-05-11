import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { apiFetch } from './client'

function mockResponse(partial: {
  ok: boolean
  status: number
  statusText?: string
  jsonText?: string
  plainText?: string
}): Response {
  const textBody = partial.jsonText ?? partial.plainText ?? ''
  return {
    ok: partial.ok,
    status: partial.status,
    statusText: partial.statusText ?? (partial.ok ? 'OK' : 'Error'),
    text: async () => textBody,
  } as Response
}

describe('apiFetch', () => {
  const fetchMock = vi.fn()

  beforeEach(() => {
    fetchMock.mockReset()
    vi.stubGlobal('fetch', fetchMock)
    localStorage.clear()
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('prefija la URL con /api y devuelve JSON parseado', async () => {
    fetchMock.mockResolvedValueOnce(mockResponse({ ok: true, status: 200, jsonText: '{"a":1}' }))

    const data = await apiFetch<{ a: number }>('/usuarios/ping')

    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining('/usuarios/ping'),
      expect.objectContaining({ headers: expect.any(Headers) })
    )
    expect(data.a).toBe(1)
  })

  it('añade Bearer si hay token y no viene Authorization explícito', async () => {
    localStorage.setItem('token', 'abc123')
    fetchMock.mockResolvedValueOnce(mockResponse({ ok: true, status: 200, jsonText: '{}' }))

    await apiFetch<object>('/x')

    const init = fetchMock.mock.calls[0]?.[1] as RequestInit
    expect(init.headers).toBeInstanceOf(Headers)
    expect((init.headers as Headers).get('Authorization')).toBe('Bearer abc123')
  })

  it('no sobrescribe Authorization si ya viene en opciones', async () => {
    localStorage.setItem('token', 'ignored')
    fetchMock.mockResolvedValueOnce(mockResponse({ ok: true, status: 200, jsonText: '{}' }))

    await apiFetch<object>('/x', { headers: { Authorization: 'Bearer custom' } })

    const init = fetchMock.mock.calls[0]?.[1] as RequestInit
    expect((init.headers as Headers).get('Authorization')).toBe('Bearer custom')
  })

  it('lanza Error con mensaje JSON (detail/message)', async () => {
    fetchMock.mockResolvedValueOnce(
      mockResponse({
        ok: false,
        status: 400,
        jsonText: JSON.stringify({ detail: 'Email inválido' }),
      })
    )

    await expect(apiFetch('/x')).rejects.toThrow('Email inválido')
  })

  it('respuesta 204 devuelve undefined', async () => {
    fetchMock.mockResolvedValueOnce({
      ok: true,
      status: 204,
      statusText: 'No Content',
      text: async () => '',
    } as Response)

    const body = await apiFetch<object | undefined>('/x', { method: 'DELETE' })
    expect(body).toBeUndefined()
  })
})
