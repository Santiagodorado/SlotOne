import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Navbar from '../components/layout/Navbar'
import './Home.css'
import { listarNegocios, type Negocio } from '../api/negocios'

const categories = [
  'Peluquería', 'Barbería', 'Spa', 'Gimnasio',
  'Consultorio', 'Veterinaria', 'Taller', 'Otro',
]

export default function Home() {
  const [negocios, setNegocios] = useState<Negocio[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    listarNegocios()
      .then(setNegocios)
      .catch((err) =>
        setError(err instanceof Error ? err.message : 'No se pudieron cargar los negocios.')
      )
      .finally(() => setLoading(false))
  }, [])
  return (
    <div className="home-page">
      <Navbar />

      <header className="home-hero">
        <h1>Encuentra y reserva en un clic</h1>
        <p>Peluquerías, consultorios, gimnasios y más negocios cerca de ti</p>
        <div className="home-search">
          <input
            type="text"
            placeholder="¿Qué servicio buscas?"
            className="home-search-input"
          />
          <button className="home-search-btn">Buscar</button>
        </div>
      </header>

      <section className="home-categories">
        {categories.map((cat) => (
          <button key={cat} className="home-cat-chip">{cat}</button>
        ))}
      </section>

      <section className="home-grid">
        {loading && <p>Cargando negocios...</p>}
        {error && !loading && <p className="dash-empty">{error}</p>}
        {!loading && !error && negocios.length === 0 && (
          <p className="dash-empty">Aún no hay negocios registrados.</p>
        )}
        {!loading && !error && negocios.map((b) => (
          <Link to={`/negocio/${b.id}`} key={b.id} className="home-card">
            {b.logoUrl ? (
              <img
                src={b.logoUrl}
                alt=""
                className="home-card-img"
              />
            ) : (
              <div className="home-card-img home-card-img--placeholder" aria-hidden />
            )}
            <div className="home-card-body">
              <h3>{b.nombre}</h3>
              <span className="home-card-cat">Negocio</span>
              <p className="home-card-addr">{b.direccion}</p>
              {b.telefono && (
                <p className="home-card-addr">{b.telefono}</p>
              )}
            </div>
          </Link>
        ))}
      </section>
    </div>
  )
}
