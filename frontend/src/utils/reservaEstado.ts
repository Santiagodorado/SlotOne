/** Estado que devuelve el micro agenda (`Reserva.estado`). */

export function estadoLabel(estado: string) {
  const e = String(estado ?? '')
    .trim()
    .toUpperCase()
  if (e === 'CONFIRMED') return 'Confirmada'
  if (e === 'CANCELLED') return 'Cancelada'
  if (e === 'COMPLETED') return 'Completada'
  if (e === 'PENDING') return 'Pendiente'
  return estado
}

export function estadoClass(estado: string): 'danger' | 'muted' | 'success' {
  const e = String(estado ?? '')
    .trim()
    .toUpperCase()
  if (e === 'CANCELLED') return 'danger'
  if (e === 'COMPLETED') return 'muted'
  return 'success'
}
