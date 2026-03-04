import { Link } from 'react-router-dom'

export default function Home() {
  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h1>SlotOne</h1>
      <p>Plataforma de agendamiento y reservas – Proyecto II</p>
      <Link to="/login">Iniciar sesión</Link>
    </div>
  )
}
