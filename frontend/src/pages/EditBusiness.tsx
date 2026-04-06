import { useState, useEffect, useRef, type FormEvent, type ChangeEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import BusinessLayout from '../components/layout/BusinessLayout'
import { actualizarNegocio, listarNegociosPorDuenio, type Negocio } from '../api/negocios'
import { fileToCompressedDataUrl } from '../utils/imageDataUrl'
import './CreateBusiness.css'

export default function EditBusiness() {
  const navigate = useNavigate()
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [negocio, setNegocio] = useState<Negocio | null>(null)
  const [name, setName] = useState('')
  const [address, setAddress] = useState('')
  const [phone, setPhone] = useState('')
  const [description, setDescription] = useState('')
  const [logoFile, setLogoFile] = useState<File | null>(null)
  const [logoPreview, setLogoPreview] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    if (!storedUser) {
      navigate('/login', { replace: true })
      return
    }
    try {
      const parsed = JSON.parse(storedUser) as { id: number; rol?: string }
      if (parsed.rol !== 'BUSINESS' && parsed.rol !== 'ROLE_BUSINESS') {
        navigate('/', { replace: true })
        return
      }
      void listarNegociosPorDuenio(parsed.id)
        .then((list) => {
          if (!list.length) {
            navigate('/panel/crear', { replace: true })
            return
          }
          const n = list[0]
          setNegocio(n)
          setName(n.nombre)
          setAddress(n.direccion)
          setPhone(n.telefono)
          setDescription(n.descripcion ?? '')
          setLogoPreview(n.logoUrl ?? null)
          setLogoFile(null)
        })
        .catch(() => setError('No se pudo cargar el negocio.'))
        .finally(() => setLoading(false))
    } catch {
      navigate('/login', { replace: true })
    }
  }, [navigate])

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
    if (!negocio) return

    if (!name.trim() || !address.trim() || !phone.trim()) {
      setError('Nombre, dirección y teléfono son obligatorios.')
      return
    }

    const storedUser = localStorage.getItem('user')
    if (!storedUser) {
      setError('Vuelve a iniciar sesión.')
      return
    }
    const parsed = JSON.parse(storedUser) as { id: number }
    setSaving(true)
    try {
      let logoUrl: string | null = negocio.logoUrl ?? null
      if (logoFile) {
        try {
          logoUrl = await fileToCompressedDataUrl(logoFile)
        } catch {
          setError('No se pudo procesar el logo. Prueba con otra imagen.')
          setSaving(false)
          return
        }
      }
      await actualizarNegocio(negocio.id, {
        nombre: name.trim(),
        descripcion: description.trim() || undefined,
        direccion: address.trim(),
        telefono: phone.trim(),
        logoUrl,
        duenioId: parsed.id,
      })
      navigate('/panel', { replace: true })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo guardar.')
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <BusinessLayout>
        <p className="dash-loading">Cargando datos del negocio…</p>
      </BusinessLayout>
    )
  }

  if (!negocio) {
    return (
      <BusinessLayout>
        <p className="dash-empty">{error ?? 'No encontramos un negocio para editar.'}</p>
      </BusinessLayout>
    )
  }

  return (
    <BusinessLayout>
      <div className="cb-page">
        <header className="cb-header">
          <h1 className="cb-heading">Editar mi negocio</h1>
          <p className="cb-subtitle">Actualiza los datos visibles en la plataforma</p>
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
                  <img src={logoPreview} alt="Logo" className="cb-logo-preview" />
                ) : (
                  <div className="cb-logo-placeholder">
                    <span>Haz clic para cambiar el logo</span>
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
                disabled={saving}
              />
            </div>

            <div className="form-group">
              <label htmlFor="address">Dirección</label>
              <input
                id="address"
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                disabled={saving}
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">Teléfono</label>
              <input
                id="phone"
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                disabled={saving}
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">Descripción <span className="form-optional">(opcional)</span></label>
              <textarea
                id="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={3}
                disabled={saving}
              />
            </div>

            {error && (
              <div className="login-error" role="alert">
                {error}
              </div>
            )}

            <button type="submit" className="login-btn" disabled={saving}>
              {saving ? 'Guardando…' : 'Guardar cambios'}
            </button>
          </form>
        </div>
      </div>
    </BusinessLayout>
  )
}
