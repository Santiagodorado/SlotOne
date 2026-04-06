import { useState, useRef } from 'react'
import type { FormEvent, ChangeEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import BusinessLayout from '../components/layout/BusinessLayout'
import { crearNegocio } from '../api/negocios'
import { fileToCompressedDataUrl } from '../utils/imageDataUrl'
import './CreateBusiness.css'

export default function CreateBusiness() {
  const navigate = useNavigate()
  const fileInputRef = useRef<HTMLInputElement>(null)

  const [logoFile, setLogoFile] = useState<File | null>(null)
  const [logoPreview, setLogoPreview] = useState<string | null>(null)
  const [name, setName] = useState('')
  const [address, setAddress] = useState('')
  const [phone, setPhone] = useState('')
  const [description, setDescription] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  function handleLogoClick() {
    fileInputRef.current?.click()
  }

  function handleLogoChange(e: ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file) return

    if (!['image/png', 'image/jpeg', 'image/jpg'].includes(file.type)) {
      setError('Solo se permiten imágenes PNG, JPG o JPEG.')
      return
    }
    if (file.size > 5 * 1024 * 1024) {
      setError('La imagen no puede superar los 5MB.')
      return
    }

    setLogoFile(file)
    setLogoPreview(URL.createObjectURL(file))
    setError(null)
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)

    if (!name.trim()) {
      setError('El nombre del negocio es obligatorio.')
      return
    }
    if (!address.trim()) {
      setError('La dirección es obligatoria.')
      return
    }
    if (!phone.trim()) {
      setError('El teléfono es obligatorio.')
      return
    }

    const storedUser = localStorage.getItem('user')
    if (!storedUser) {
      setError('Debes iniciar sesión como negocio para crear un negocio.')
      return
    }

    let duenioId: number
    try {
      const parsed = JSON.parse(storedUser) as { id: number; rol?: string }
      duenioId = parsed.id
      if (parsed.rol !== 'BUSINESS' && parsed.rol !== 'ROLE_BUSINESS') {
        setError('Solo los usuarios con rol negocio pueden crear negocios.')
        return
      }
    } catch {
      setError('Información de usuario inválida. Vuelve a iniciar sesión.')
      return
    }

    setLoading(true)
    try {
      let logoUrl: string | null = null
      if (logoFile) {
        try {
          logoUrl = await fileToCompressedDataUrl(logoFile)
        } catch {
          setError('No se pudo procesar el logo. Prueba con otra imagen.')
          setLoading(false)
          return
        }
      }
      await crearNegocio({
        nombre: name.trim(),
        descripcion: description.trim() || undefined,
        direccion: address.trim(),
        telefono: phone.trim(),
        logoUrl,
        duenioId,
      })
      navigate('/panel', { replace: true })
    } catch (err) {
      setError(
        err instanceof Error ? err.message : 'No se pudo crear el negocio. Intenta de nuevo.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <BusinessLayout>
      <div className="cb-page">
        <header className="cb-header">
          <h1 className="cb-heading">Crear mi negocio</h1>
          <p className="cb-subtitle">Completa los datos de tu negocio para comenzar</p>
        </header>

        <div className="cb-card">
          <form onSubmit={handleSubmit} className="cb-form">
            <div className="form-group">
              <label>Logo del negocio</label>
              <div className="cb-logo-upload" onClick={handleLogoClick}>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/png,image/jpeg,image/jpg"
                  onChange={handleLogoChange}
                  hidden
                />
                {logoPreview ? (
                  <img src={logoPreview} alt="Logo preview" className="cb-logo-preview" />
                ) : (
                  <div className="cb-logo-placeholder">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#9ca3af" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                      <polyline points="17 8 12 3 7 8" />
                      <line x1="12" y1="3" x2="12" y2="15" />
                    </svg>
                    <span>Haz clic para subir una imagen</span>
                    <span className="cb-logo-hint">PNG, JPG o JPEG (máx. 5MB)</span>
                  </div>
                )}
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="businessName">Nombre del negocio</label>
              <input
                id="businessName"
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Ej: Peluquería Estilo"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="address">Dirección</label>
              <input
                id="address"
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="Calle Principal 123, Local 5"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">Teléfono</label>
              <input
                id="phone"
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="+34 600 000 000"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">Descripción <span className="form-optional">(opcional)</span></label>
              <textarea
                id="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Ej: Peluquería unisex en el centro"
                rows={3}
                disabled={loading}
              />
            </div>

            {error && (
              <div className="login-error" role="alert">
                {error}
              </div>
            )}

            <button type="submit" className="login-btn" disabled={loading}>
              {loading ? 'Creando...' : 'Crear negocio'}
            </button>
          </form>
        </div>
      </div>
    </BusinessLayout>
  )
}
