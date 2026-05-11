import { useEffect, useRef, useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import Navbar from '../components/layout/Navbar'
import './Home.css'
import './Dashboard.css'
import { listarNegocios, type Negocio } from '../api/negocios'
import {
  consultarDisponibilidad,
  crearReserva,
  listarServicios,
  listarTrabajadoresPorServicio,
  type Servicio,
  type SlotDisponible,
  type Trabajador,
} from '../api/agenda'

interface User {
  id: number
  nombres: string
  apellidos: string
  correo: string
  telefono?: string
  rol: string
}

const RESERVA_BORRADOR_KEY = 'slotone_reserva_borrador'

interface ReservaBorradorGuardado {
  negocioId: number
  servicioId: number
  trabajadorId: number | null
  fecha: string
  slot: SlotDisponible
}

interface ReservaExitosaModal {
  codigoReserva: string
  servicioNombre: string
  fecha: string
  horaInicio: string
  horaFin: string
  trabajadorEtiqueta: string
}

function pad2(n: number) {
  return n < 10 ? `0${n}` : String(n)
}

function todayISO() {
  const t = new Date()
  return `${t.getFullYear()}-${pad2(t.getMonth() + 1)}-${pad2(t.getDate())}`
}

/** Calendario con lunes en la primera columna */
function buildCalendarGrid(year: number, monthIndex: number) {
  const first = new Date(year, monthIndex, 1)
  const dow = first.getDay()
  const pad = dow === 0 ? 6 : dow - 1
  const lastDay = new Date(year, monthIndex + 1, 0).getDate()
  const cells: Array<{ iso: string | null; label: number | null }> = []
  for (let i = 0; i < pad; i++) cells.push({ iso: null, label: null })
  for (let d = 1; d <= lastDay; d++) {
    cells.push({
      iso: `${year}-${pad2(monthIndex + 1)}-${pad2(d)}`,
      label: d,
    })
  }
  while (cells.length % 7 !== 0) cells.push({ iso: null, label: null })
  return cells
}

function formatHora(raw: string) {
  const [h, m] = raw.split(':').map(Number)
  if (Number.isNaN(h) || Number.isNaN(m)) return raw
  return new Date(2000, 0, 1, h, m).toLocaleTimeString('es-CO', {
    hour: 'numeric',
    minute: '2-digit',
    hour12: true,
  })
}

function formatFechaReserva(iso: string) {
  const parts = iso.split('-').map(Number)
  const y = parts[0]
  const m = parts[1]
  const d = parts[2]
  if (!y || !m || !d || Number.isNaN(y)) return iso
  return new Date(y, m - 1, d).toLocaleDateString('es-CO', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

/** Ej. «15 de abril de 2026» para el resumen */
function formatFechaResumen(iso: string) {
  const parts = iso.split('-').map(Number)
  const y = parts[0]
  const m = parts[1]
  const d = parts[2]
  if (!y || !m || !d || Number.isNaN(y)) return iso
  return new Date(y, m - 1, d).toLocaleDateString('es-CO', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  })
}

function formatDuracionServicio(minutos: number) {
  const total = Math.max(0, Math.round(minutos))
  if (total === 0) return '—'
  if (total < 60) return `${total} minutos`
  const h = Math.floor(total / 60)
  const r = total % 60
  const parteHoras = h === 1 ? '1 hora' : `${h} horas`
  if (r === 0) return parteHoras
  return `${parteHoras} ${r} min`
}

function monthTitle(year: number, monthIndex: number) {
  return new Date(year, monthIndex, 1).toLocaleDateString('es-CO', {
    month: 'long',
    year: 'numeric',
  })
}

function SummaryIconNegocio() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" aria-hidden>
      <path d="M4 11V20h16V11l-8-5-8 5Z" strokeLinejoin="round" />
      <path d="M9 20v-5h6v5M11 15h2" strokeLinecap="round" />
    </svg>
  )
}

function SummaryIconDocumento() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" aria-hidden>
      <path d="M14 2H8a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7l-4-5Z" strokeLinejoin="round" />
      <path d="M14 2v5h5M10 13h8M10 17h8" strokeLinecap="round" />
    </svg>
  )
}

function SummaryIconCalendario() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" aria-hidden>
      <rect x="4" y="5" width="16" height="15" rx="2" strokeLinejoin="round" />
      <path d="M8 3v4M16 3v4M4 11h16" strokeLinecap="round" />
    </svg>
  )
}

function SummaryIconReloj() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" aria-hidden>
      <circle cx="12" cy="13" r="8" />
      <path d="M12 9v5l3 2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

function SummaryIconUsuario() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" aria-hidden>
      <circle cx="12" cy="8.5" r="3.5" />
      <path d="M6 20c0-3.75 4-6 6-6s6 2.25 6 6" strokeLinecap="round" />
    </svg>
  )
}

function clearReservaBorrador() {
  sessionStorage.removeItem(RESERVA_BORRADOR_KEY)
}

const PREFETCH_CHUNK = 6

export default function BusinessDetail() {
  const { id } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const negocioId = Number(id)
  const checkoutRef = useRef<HTMLElement | null>(null)
  const checkoutClienteAnchorRef = useRef<HTMLDivElement | null>(null)
  const prevServicioIdRef = useRef<number | null>(null)
  const prefetchRequestIdRef = useRef(0)

  const [user, setUser] = useState<User | null>(null)
  const [negocio, setNegocio] = useState<Negocio | null>(null)
  const [servicios, setServicios] = useState<Servicio[]>([])
  const [trabajadores, setTrabajadores] = useState<Trabajador[]>([])
  const [servicioId, setServicioId] = useState<number | null>(null)
  const [trabajadorId, setTrabajadorId] = useState<number | null>(null)
  const [fechaReserva, setFechaReserva] = useState<string | null>(null)

  const t0 = new Date()
  const [vistaMes, setVistaMes] = useState(() => ({
    y: t0.getFullYear(),
    m: t0.getMonth(),
  }))
  const [fechasDisponibles, setFechasDisponibles] = useState<Record<string, boolean>>({})
  const [prefetchMes, setPrefetchMes] = useState(false)

  const [slots, setSlots] = useState<SlotDisponible[]>([])
  const [slot, setSlot] = useState<SlotDisponible | null>(null)
  const [clienteNombre, setClienteNombre] = useState('')
  const [clienteEmail, setClienteEmail] = useState('')
  const [clienteTelefono, setClienteTelefono] = useState('')
  const [notas, setNotas] = useState('')
  const [aceptaTerminos, setAceptaTerminos] = useState(false)
  const [loading, setLoading] = useState(true)
  const [checkoutVisible, setCheckoutVisible] = useState(false)
  const [reservaExitosa, setReservaExitosa] = useState<ReservaExitosaModal | null>(null)
  const [error, setError] = useState<string | null>(null)
  const borradorRestauradoRef = useRef(false)

  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (!stored) {
      setUser(null)
      setClienteNombre('')
      setClienteEmail('')
      setClienteTelefono('')
      return
    }
    try {
      const u = JSON.parse(stored) as User
      setUser(u)
      setClienteNombre([u.nombres, u.apellidos].filter(Boolean).join(' '))
      setClienteEmail(u.correo)
      setClienteTelefono(u.telefono ?? '')
    } catch {
      localStorage.removeItem('user')
      setUser(null)
      setClienteNombre('')
      setClienteEmail('')
      setClienteTelefono('')
    }
  }, [location.key, location.pathname])

  useEffect(() => {
    borradorRestauradoRef.current = false
    prevServicioIdRef.current = null
  }, [negocioId])

  useEffect(() => {
    async function load() {
      try {
        const negocios = await listarNegocios()
        const n = negocios.find((x) => x.id === negocioId)
        if (!n) {
          setError('Negocio no encontrado.')
          return
        }
        setNegocio(n)
        const svc = await listarServicios(negocioId)
        let nextServicioId = svc[0]?.id ?? null
        let nextFechaReserva: string | null = null
        let nextTrabajadorId: number | null = null
        try {
          const raw = sessionStorage.getItem(RESERVA_BORRADOR_KEY)
          if (raw) {
            const p = JSON.parse(raw) as Partial<ReservaBorradorGuardado>
            if (p.negocioId === negocioId) {
              if (typeof p.servicioId === 'number' && svc.some((s) => s.id === p.servicioId)) {
                nextServicioId = p.servicioId
              }
              if (typeof p.fecha === 'string' && p.fecha.trim()) nextFechaReserva = p.fecha
              if ('trabajadorId' in p) {
                nextTrabajadorId =
                  typeof p.trabajadorId === 'number' && !Number.isNaN(p.trabajadorId) ? p.trabajadorId : null
              }
            }
          }
        } catch {
          clearReservaBorrador()
        }
        setServicios(svc)
        setServicioId(nextServicioId)
        setFechaReserva(nextFechaReserva)
        setTrabajadorId(nextTrabajadorId)
        prevServicioIdRef.current = nextServicioId
        if (nextFechaReserva) {
          const [yy, mm] = nextFechaReserva.split('-').map(Number)
          if (yy && mm) setVistaMes({ y: yy, m: mm - 1 })
        }
        if (!nextFechaReserva) setCheckoutVisible(false)
      } catch (e) {
        setError(e instanceof Error ? e.message : 'No se pudo cargar el negocio.')
      } finally {
        setLoading(false)
      }
    }
    if (Number.isFinite(negocioId)) void load()
  }, [negocioId])

  useEffect(() => {
    async function loadTrabajadores() {
      if (!servicioId) return
      const anterior = prevServicioIdRef.current
      prevServicioIdRef.current = servicioId
      if (anterior !== null && anterior !== servicioId) {
        setTrabajadorId(null)
        setFechaReserva(null)
        setSlot(null)
        setCheckoutVisible(false)
      }
      try {
        setTrabajadores(await listarTrabajadoresPorServicio(servicioId))
      } catch {
        setTrabajadores([])
      }
    }
    void loadTrabajadores()
  }, [servicioId])

  useEffect(() => {
    async function loadSlots() {
      if (!servicioId || !fechaReserva) {
        setSlots([])
        setSlot(null)
        return
      }
      try {
        const data = await consultarDisponibilidad(servicioId, fechaReserva, trabajadorId)
        setSlots(data.slots)
        setSlot((prev) => {
          if (!prev) return null
          return (
            data.slots.find((s) => s.horaInicio === prev.horaInicio && s.horaFin === prev.horaFin) ??
            null
          )
        })
      } catch (e) {
        setSlots([])
        setSlot(null)
        setError(e instanceof Error ? e.message : 'No se pudo consultar disponibilidad.')
      }
    }
    void loadSlots()
  }, [servicioId, fechaReserva, trabajadorId])

  useEffect(() => {
    if (!servicioId) return

    const requestId = ++prefetchRequestIdRef.current

    async function prefetch() {
      const sid = servicioId
      if (sid == null) return
      setPrefetchMes(true)
      const nextAvail: Record<string, boolean> = {}
      try {
        const today = todayISO()
        const { y, m } = vistaMes
        const calGrid = buildCalendarGrid(y, m)
        const isos = calGrid.map((c) => c.iso).filter((iso): iso is string => iso != null && iso >= today)

        for (let i = 0; i < isos.length; i += PREFETCH_CHUNK) {
          if (prefetchRequestIdRef.current !== requestId) return
          const slice = isos.slice(i, i + PREFETCH_CHUNK)
          const settled = await Promise.all(
            slice.map((iso) =>
              consultarDisponibilidad(sid, iso, trabajadorId)
                .then((d) => ({ iso, ok: d.slots.length > 0 }))
                .catch(() => ({ iso, ok: false }))
            )
          )
          if (prefetchRequestIdRef.current !== requestId) return
          settled.forEach(({ iso, ok }) => {
            nextAvail[iso] = ok
          })
        }
        if (prefetchRequestIdRef.current !== requestId) return
        setFechasDisponibles(nextAvail)
      } finally {
        if (prefetchRequestIdRef.current === requestId) setPrefetchMes(false)
      }
    }

    void prefetch()
  }, [servicioId, trabajadorId, vistaMes])

  useEffect(() => {
    setSlot(null)
  }, [trabajadorId])

  useEffect(() => {
    if (borradorRestauradoRef.current || !slots.length) return
    const raw = sessionStorage.getItem(RESERVA_BORRADOR_KEY)
    if (!raw) return
    try {
      const p = JSON.parse(raw) as Partial<ReservaBorradorGuardado>
      if (
        p.negocioId !== negocioId ||
        p.servicioId !== servicioId ||
        p.fecha !== fechaReserva ||
        (p.trabajadorId ?? null) !== (trabajadorId ?? null)
      ) {
        return
      }
      if (!p.slot?.horaInicio || !p.slot?.horaFin) return
      const found = slots.find(
        (s) => s.horaInicio === p.slot!.horaInicio && s.horaFin === p.slot!.horaFin
      )
      if (found) {
        setSlot(found)
        borradorRestauradoRef.current = true
        setCheckoutVisible(true)
      }
    } catch {
      clearReservaBorrador()
    }
  }, [slots, servicioId, fechaReserva, trabajadorId, negocioId])

  useEffect(() => {
    if (!checkoutVisible || loading || negocio == null || servicioId == null || !fechaReserva || !slot) {
      return
    }
    const t = window.setTimeout(() => {
      checkoutClienteAnchorRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }, 160)
    return () => window.clearTimeout(t)
  }, [checkoutVisible, user, loading, negocio, servicioId, fechaReserva, slot])

  function guardarReservaBorrador() {
    if (!servicioId || !slot || !fechaReserva) return
    const payload: ReservaBorradorGuardado = {
      negocioId,
      servicioId,
      trabajadorId,
      fecha: fechaReserva,
      slot,
    }
    sessionStorage.setItem(RESERVA_BORRADOR_KEY, JSON.stringify(payload))
  }

  function irAlHomeTrasReserva() {
    setReservaExitosa(null)
    clearReservaBorrador()
    navigate('/', { replace: true })
  }

  function handleContinuar() {
    setCheckoutVisible(true)
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (!checkoutVisible) return
    if (!servicioId || !slot || !fechaReserva) return
    if (!user) {
      guardarReservaBorrador()
      const redirect = `${location.pathname}${location.search}`
      navigate(`/login?redirect=${encodeURIComponent(redirect)}`)
      return
    }
    if (!aceptaTerminos) {
      setError('Debes aceptar los términos y condiciones para continuar.')
      return
    }
    setError(null)
    try {
      const svcNombre = servicios.find((s) => s.id === servicioId)?.nombre ?? 'Servicio'
      const reserva = await crearReserva({
        servicioId,
        trabajadorId: trabajadorId ?? undefined,
        clienteId: user.id,
        clienteNombre,
        clienteEmail,
        clienteTelefono,
        fecha: fechaReserva,
        horaInicio: slot.horaInicio,
        notas: notas.trim() || undefined,
      })
      const tid = reserva.trabajadorId ?? null
      const profesionalEtiqueta =
        tid != null
          ? trabajadores.find((t) => t.id === tid)?.nombre ?? 'Profesional asignado'
          : 'Cualquier trabajador disponible'
      clearReservaBorrador()
      borradorRestauradoRef.current = false
      setReservaExitosa({
        codigoReserva: reserva.codigoReserva,
        servicioNombre: svcNombre,
        fecha: fechaReserva,
        horaInicio: reserva.horaInicio,
        horaFin: reserva.horaFin,
        trabajadorEtiqueta: profesionalEtiqueta,
      })
      try {
        const data = await consultarDisponibilidad(servicioId, fechaReserva, trabajadorId)
        setSlots(data.slots)
        setSlot((prev) => {
          if (!prev) return null
          return (
            data.slots.find((s) => s.horaInicio === prev.horaInicio && s.horaFin === prev.horaFin) ??
            null
          )
        })
      } catch {
        /* opcional */
      }
      setNotas('')
      setAceptaTerminos(false)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo confirmar la reserva.')
    }
  }

  const servicioSeleccionado = servicios.find((s) => s.id === servicioId) ?? null
  const trabajadorSeleccionado = trabajadores.find((t) => t.id === trabajadorId) ?? null
  const subtitleReserva =
    negocio && servicioSeleccionado
      ? `${negocio.nombre} · ${servicioSeleccionado.nombre} — ${servicioSeleccionado.duracionMinutos} min`
      : ''

  const hoyISO = todayISO()
  const zonaId = Intl.DateTimeFormat().resolvedOptions().timeZone ?? 'hora local'
  const zonaEtiquetaTexto =
    zonaId === 'America/Bogota' ? 'America/Bogotá (COT)' : zonaId.replace(/_/g, ' ')
  const grid = buildCalendarGrid(vistaMes.y, vistaMes.m)
  const puedeContinuar = Boolean(servicioId && fechaReserva && slot && servicios.length)

  function prevMonth() {
    setVistaMes(({ y, m }) => {
      if (m === 0) return { y: y - 1, m: 11 }
      return { y, m: m - 1 }
    })
  }

  function nextMonth() {
    setVistaMes(({ y, m }) => {
      if (m === 11) return { y: y + 1, m: 0 }
      return { y, m: m + 1 }
    })
  }

  function onPickDay(iso: string | null) {
    if (!iso || iso < hoyISO) return
    setError(null)
    setFechaReserva(iso)
    setCheckoutVisible(false)
  }

  return (
    <div className="home-page">
      <Navbar />
      {reservaExitosa && (
        <div
          className="booking-modal-overlay"
          role="presentation"
          aria-hidden={false}
          onMouseDown={(ev) => {
            if (ev.target === ev.currentTarget) irAlHomeTrasReserva()
          }}
        >
          <div
            className="booking-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="booking-success-title"
            onMouseDown={(click) => click.stopPropagation()}
          >
            <h2 id="booking-success-title" className="booking-modal-title">
              Reserva confirmada
            </h2>
            <p className="booking-modal-intro">
              Tu cita quedó agendada. Guarda tu código por si necesitas referencia:
            </p>
            <p className="booking-modal-code">{reservaExitosa.codigoReserva}</p>
            <ul className="booking-modal-summary">
              <li>
                <span>Servicio</span>
                <strong>{reservaExitosa.servicioNombre}</strong>
              </li>
              <li>
                <span>Fecha</span>
                <strong>{formatFechaReserva(reservaExitosa.fecha)}</strong>
              </li>
              <li>
                <span>Horario</span>
                <strong>
                  {formatHora(reservaExitosa.horaInicio)} a {formatHora(reservaExitosa.horaFin)}
                </strong>
              </li>
              <li>
                <span>Profesional</span>
                <strong>{reservaExitosa.trabajadorEtiqueta}</strong>
              </li>
            </ul>
            <p className="booking-modal-hint">
              También puedes revisar tu <strong className="booking-modal-strong">bandeja de correo</strong> o tus
              citas en{' '}
              <Link
                to="/mis-reservas"
                className="booking-modal-inline-link"
                onClick={() => {
                  setReservaExitosa(null)
                  clearReservaBorrador()
                }}
              >
                Mis reservas
              </Link>
              .
            </p>
            <div className="booking-modal-actions">
              <button
                type="button"
                className="dash-btn dash-btn-primary booking-modal-primary"
                onClick={irAlHomeTrasReserva}
              >
                Ir al inicio
              </button>
            </div>
          </div>
        </div>
      )}
      <main className="business-detail">
        {loading && <p className="dash-loading">Cargando...</p>}
        {!loading && error && !negocio && <p className="dash-alert dash-alert--error">{error}</p>}
        {negocio && (
          <>
            <Link to="/" className="dash-link-btn">
              Volver
            </Link>
            <section className="business-profile">
              {negocio.logoUrl ? (
                <img src={negocio.logoUrl} alt="" className="business-profile-logo" />
              ) : (
                <div className="business-profile-logo business-profile-logo--placeholder" />
              )}
              <div>
                <h1>{negocio.nombre}</h1>
                <p>{negocio.descripcion || 'Reserva uno de los servicios disponibles.'}</p>
                <p className="home-card-addr">
                  {negocio.direccion} · {negocio.telefono}
                </p>
              </div>
            </section>

            <section className="dash-section">
              {!checkoutVisible && (
                <div className="dash-table-wrap booking-service-strip">
                  <h2>Elegí tu servicio</h2>
                  {servicios.length === 0 && <p className="dash-empty">Este negocio aún no tiene servicios.</p>}
                  <div className="slot-grid">
                    {servicios.map((s) => (
                      <button
                        type="button"
                        key={s.id}
                        className={`slot-chip ${servicioId === s.id ? 'active' : ''}`}
                        onClick={() => {
                          setServicioId(s.id)
                        }}
                      >
                        {s.nombre} · {s.duracionMinutos} min · ${s.precio.toFixed(0)}
                      </button>
                    ))}
                  </div>
                  <div className="booking-worker-row">
                    <label htmlFor="booking-worker">Profesional</label>
                    <select
                      id="booking-worker"
                      value={trabajadorId ?? ''}
                      onChange={(e) => setTrabajadorId(Number(e.target.value) || null)}
                      disabled={!servicioId || !trabajadores.length}
                    >
                      <option value="">Cualquier trabajador disponible</option>
                      {trabajadores.map((t) => (
                        <option key={t.id} value={t.id}>
                          {t.nombre}
                        </option>
                      ))}
                    </select>
                    {!trabajadores.length && servicioId ? (
                      <p className="dash-hint">Este servicio aún no tiene trabajadores asignados.</p>
                    ) : null}
                  </div>
                </div>
              )}

              <form onSubmit={handleSubmit} className="booking-datetime-shell">
                {servicioSeleccionado && (
                  <>
                    {error && (
                      <p className="dash-alert dash-alert--error booking-flow-error" role="alert">
                        {error}
                      </p>
                    )}
                    {!checkoutVisible && (
                      <>
                    <div className="booking-datetime-head">
                      <h2 className="booking-datetime-title">Selecciona fecha y hora</h2>
                      <p className="booking-datetime-sub">{subtitleReserva}</p>
                    </div>

                    <div className="booking-datetime-grid">
                      <div className="booking-card booking-card-cal">
                        <div className="booking-card-heading">
                          <span className="booking-card-icon" aria-hidden>
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                              <rect x="3" y="4" width="18" height="18" rx="2" />
                              <path d="M16 2v4M8 2v4M3 10h18" strokeLinecap="round" />
                            </svg>
                          </span>
                          <div>
                            <h3 className="booking-card-title">Selecciona una fecha</h3>
                            <p className="booking-card-hint">Solo fechas con disponibilidad</p>
                          </div>
                        </div>
                        <div className="booking-cal-nav">
                          <button type="button" className="booking-cal-nav-btn" onClick={prevMonth} aria-label="Mes anterior">
                            ‹
                          </button>
                          <span className="booking-cal-caption">{monthTitle(vistaMes.y, vistaMes.m)}</span>
                          <button type="button" className="booking-cal-nav-btn" onClick={nextMonth} aria-label="Mes siguiente">
                            ›
                          </button>
                        </div>
                        {prefetchMes && (
                          <p className="booking-cal-loading" aria-live="polite">
                            Actualizando días disponibles…
                          </p>
                        )}
                        <div className="booking-weekdays">
                          {['LU', 'MA', 'MI', 'JU', 'VI', 'SÁ', 'DO'].map((d) => (
                            <span key={d}>{d}</span>
                          ))}
                        </div>
                        <div className="booking-cal-cells">
                          {grid.map((cell, idx) => {
                            if (!cell.iso) {
                              return <div key={`e-${idx}`} className="booking-cal-cell muted" aria-hidden />
                            }
                            const past = cell.iso < hoyISO
                            const tieneClave =
                              prefetchMes === false &&
                              Object.prototype.hasOwnProperty.call(fechasDisponibles, cell.iso)
                            const hasAvail = tieneClave ? fechasDisponibles[cell.iso] : undefined
                            const diaDeshabilitado =
                              past ||
                              !servicioId ||
                              prefetchMes ||
                              (tieneClave && hasAvail === false)
                            const selected = fechaReserva === cell.iso
                            let title = ''
                            if (prefetchMes) title = 'Calculando disponibilidad'
                            else if (tieneClave && !hasAvail) title = 'Sin horarios ese día'

                            return (
                              <button
                                key={cell.iso}
                                type="button"
                                disabled={diaDeshabilitado}
                                title={title}
                                className={`booking-cal-cell${selected ? ' selected' : ''}${
                                  diaDeshabilitado ? ' disabled' : ''
                                }`}
                                onClick={() => onPickDay(cell.iso)}
                              >
                                {cell.label}
                              </button>
                            )
                          })}
                        </div>
                      </div>

                      <div className="booking-card booking-card-times">
                        <div className="booking-card-heading">
                          <span className="booking-card-icon" aria-hidden>
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                              <circle cx="12" cy="12" r="9" />
                              <path d="M12 7v6l4 2" strokeLinecap="round" />
                            </svg>
                          </span>
                          <div>
                            <h3 className="booking-card-title">Horarios disponibles</h3>
                            <p className="booking-card-hint">{fechaReserva ? formatFechaReserva(fechaReserva) : '—'}</p>
                          </div>
                        </div>
                        <div className="booking-slot-panel">
                          {!fechaReserva && (
                            <div className="booking-slot-placeholder">
                              <span className="booking-slot-placeholder-icon" aria-hidden>
                                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#cbd5e1" strokeWidth="1.25">
                                  <rect x="3.5" y="5.5" width="17" height="15" rx="2" />
                                  <path d="M8 3.5v3M16 3.5v3M4 11h16" strokeLinecap="round" />
                                </svg>
                              </span>
                              <p>Selecciona una fecha para ver los horarios disponibles</p>
                            </div>
                          )}
                          {fechaReserva && slots.length === 0 && (
                            <p className="dash-empty">No hay horarios disponibles para esta fecha.</p>
                          )}
                          {fechaReserva && slots.length > 0 && (
                            <div className="booking-slot-buttons">
                              {slots.map((s) => (
                                <button
                                  type="button"
                                  key={`${s.horaInicio}-${s.horaFin}`}
                                  className={`booking-time-chip ${slot?.horaInicio === s.horaInicio && slot?.horaFin === s.horaFin ? 'selected' : ''}`}
                                  onClick={() => {
                                    setSlot(s)
                                    setCheckoutVisible(false)
                                  }}
                                  aria-pressed={
                                    slot?.horaInicio === s.horaInicio && slot?.horaFin === s.horaFin
                                  }
                                  aria-label={`Reservar de ${formatHora(s.horaInicio)} a ${formatHora(s.horaFin)}`}
                                >
                                  {formatHora(s.horaInicio)} · {formatHora(s.horaFin)}
                                </button>
                              ))}
                            </div>
                          )}
                        </div>
                      </div>
                    </div>

                    <div className="booking-tz-banner" role="status">
                      <span className="booking-tz-icon" aria-hidden>
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                          <circle cx="12" cy="12" r="9" />
                          <path d="M12 7v5l3 2" strokeLinecap="round" />
                        </svg>
                      </span>
                      Zona horaria: {zonaEtiquetaTexto}
                    </div>

                    <div className="booking-datetime-footer">
                      <button
                        type="button"
                        className="dash-btn dash-btn-primary booking-btn-continue"
                        disabled={!puedeContinuar}
                        onClick={handleContinuar}
                      >
                        Continuar
                      </button>
                    </div>
                      </>
                    )}
                  </>
                )}

                {checkoutVisible && servicioSeleccionado && fechaReserva && slot && negocio && (
                  <section ref={checkoutRef} id="booking-checkout" className="booking-checkout">
                    <button
                      type="button"
                      className="dash-link-btn booking-back-to-datetime"
                      onClick={() => setCheckoutVisible(false)}
                    >
                      ← Volver a elegir fecha u horario
                    </button>
                    <div className="booking-summary-card">
                      <h3 className="booking-summary-headline">Resumen de la reserva</h3>
                      <ul className="booking-summary-list">
                        <li className="booking-summary-item">
                          <span className="booking-summary-icon-wrap">
                            <SummaryIconNegocio />
                          </span>
                          <div className="booking-summary-text">
                            <span className="booking-summary-label">Negocio</span>
                            <span className="booking-summary-value booking-summary-capline">
                              {negocio.nombre}
                            </span>
                          </div>
                        </li>
                        <li className="booking-summary-item">
                          <span className="booking-summary-icon-wrap">
                            <SummaryIconDocumento />
                          </span>
                          <div className="booking-summary-text">
                            <span className="booking-summary-label">Servicio</span>
                            <span className="booking-summary-value booking-summary-capline">
                              {servicioSeleccionado.nombre}
                            </span>
                          </div>
                        </li>
                        <li className="booking-summary-item">
                          <span className="booking-summary-icon-wrap">
                            <SummaryIconUsuario />
                          </span>
                          <div className="booking-summary-text">
                            <span className="booking-summary-label">Profesional</span>
                            <span className="booking-summary-value booking-summary-capline">
                              {trabajadorSeleccionado
                                ? trabajadorSeleccionado.nombre
                                : 'Asignación según disponibilidad'}
                            </span>
                          </div>
                        </li>
                        <li className="booking-summary-item">
                          <span className="booking-summary-icon-wrap">
                            <SummaryIconCalendario />
                          </span>
                          <div className="booking-summary-text">
                            <span className="booking-summary-label">Fecha</span>
                            <span className="booking-summary-value">{formatFechaResumen(fechaReserva)}</span>
                          </div>
                        </li>
                        <li className="booking-summary-item">
                          <span className="booking-summary-icon-wrap">
                            <SummaryIconReloj />
                          </span>
                          <div className="booking-summary-text">
                            <span className="booking-summary-label">Hora de inicio</span>
                            <span className="booking-summary-value">{formatHora(slot.horaInicio)}</span>
                          </div>
                        </li>
                        <li className="booking-summary-item booking-summary-item--noborder">
                          <span className="booking-summary-icon-wrap">
                            <SummaryIconReloj />
                          </span>
                          <div className="booking-summary-text">
                            <span className="booking-summary-label">Duración estimada</span>
                            <span className="booking-summary-value">
                              {formatDuracionServicio(servicioSeleccionado.duracionMinutos)}
                            </span>
                          </div>
                        </li>
                      </ul>
                      <div className="booking-summary-status">
                        <span className="booking-summary-status-text">Estado</span>
                        <span className="booking-summary-pill booking-summary-pill--pending">
                          Pendiente de confirmación
                        </span>
                      </div>
                    </div>

                    <div ref={checkoutClienteAnchorRef} className="booking-client-anchor-scroll">
                      <h3 className="booking-client-heading">Información del cliente</h3>
                      {user ? (
                        <>
                          <div className="checkout-review-card checkout-review-card--readonly">
                            <div className="checkout-review-main">
                              <span className="checkout-review-radio" aria-hidden />
                              <div>
                                <h3>Datos de tu cuenta</h3>
                                <p>{clienteNombre}</p>
                                <p>{clienteEmail}</p>
                                <p>{clienteTelefono || 'Sin teléfono registrado'}</p>
                                <p className="dash-hint booking-account-hint">
                                  La reserva usará estos datos. Si necesitas otra persona, crea o usa una cuenta con
                                  sus datos de contacto.
                                </p>
                              </div>
                            </div>
                          </div>
                          <div className="form-group">
                            <label>Notas (opcional)</label>
                            <textarea value={notas} onChange={(change) => setNotas(change.target.value)} />
                          </div>
                          <label className="dash-check">
                            <input
                              type="checkbox"
                              checked={aceptaTerminos}
                              onChange={(change) => setAceptaTerminos(change.target.checked)}
                              required
                            />
                            Acepto los términos y condiciones de la reserva.
                          </label>
                          <button type="submit" className="dash-btn dash-btn-primary" disabled={!slot || !aceptaTerminos}>
                            Confirmar reserva
                          </button>
                        </>
                      ) : (
                        <div className="dash-form-actions booking-login-actions">
                          <button type="submit" className="dash-btn dash-btn-primary">
                            Iniciar sesión para reservar
                          </button>
                          <p className="booking-register-hint">
                            ¿No tienes cuenta?{' '}
                            <Link
                              className="booking-register-link"
                              to={`/registro?redirect=${encodeURIComponent(`${location.pathname}${location.search}`)}`}
                              onClick={() => guardarReservaBorrador()}
                            >
                              Regístrate
                            </Link>
                          </p>
                        </div>
                      )}
                    </div>
                  </section>
                )}
              </form>
            </section>
          </>
        )}
      </main>
    </div>
  )
}
