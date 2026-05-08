import { apiFetch } from './client';

export interface Servicio {
  id: number;
  negocioId: number;
  nombre: string;
  duracionMinutos: number;
  precio: number;
  descripcion?: string | null;
}

export interface Horario {
  id: number;
  negocioId: number;
  servicioId?: number;
  diaSemana: number; // 0=Dom, 1=Lun, ...
  horaInicio: string;
  horaFin: string;
}

export interface SlotDisponible {
  horaInicio: string;
  horaFin: string;
}

export interface Disponibilidad {
  servicioId: number;
  fecha: string;
  slots: SlotDisponible[];
}

export interface Trabajador {
  id: number;
  negocioId: number;
  nombre: string;
  email?: string | null;
  telefono?: string | null;
  activo: boolean;
  servicioIds: number[];
}

export interface Reserva {
  id: number;
  codigoReserva: string;
  negocioId: number;
  servicioId: number;
  trabajadorId?: number | null;
  clienteId?: number | null;
  clienteNombre: string;
  clienteEmail: string;
  clienteTelefono: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  estado: string;
  notas?: string | null;
}

export interface CrearReservaRequest {
  servicioId: number;
  trabajadorId?: number;
  clienteId?: number;
  clienteNombre: string;
  clienteEmail: string;
  clienteTelefono: string;
  fecha: string;
  horaInicio: string;
  notas?: string;
}

export interface CrearServicioRequest {
  negocioId: number;
  nombre: string;
  duracionMinutos: number;
  precio: number;
  descripcion?: string;
}

export interface CrearHorarioRequest {
  servicioId: number;
  diaSemana: number;
  horaInicio: string;
  horaFin: string;
}

export interface CrearTrabajadorRequest {
  negocioId: number;
  nombre: string;
  email?: string;
  telefono?: string;
  activo?: boolean;
  servicioIds: number[];
}

export function listarServicios(negocioId: number) {
  return apiFetch<Servicio[]>(`/agenda/servicios?negocioId=${negocioId}`);
}

export function crearServicio(data: CrearServicioRequest) {
  return apiFetch<Servicio>('/agenda/servicios', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function actualizarServicio(id: number, data: CrearServicioRequest) {
  return apiFetch<Servicio>(`/agenda/servicios/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function eliminarServicio(id: number, negocioId: number) {
  return apiFetch<void>(`/agenda/servicios/${id}?negocioId=${negocioId}`, {
    method: 'DELETE',
  });
}

/** Indica si la hora cae dentro de los horarios de atención del día (para reservas). */
export function consultarHorarioCubre(servicioId: number, diaSemana: number, hora: string) {
  const q = new URLSearchParams({
    servicioId: String(servicioId),
    diaSemana: String(diaSemana),
    hora,
  });
  return apiFetch<{ cubre: boolean }>(`/agenda/horarios/cubre?${q.toString()}`);
}

export function listarHorarios(negocioId: number) {
  return apiFetch<Horario[]>(`/agenda/horarios?negocioId=${negocioId}`);
}

export function crearHorario(data: CrearHorarioRequest) {
  return apiFetch<Horario>('/agenda/horarios', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function actualizarHorario(id: number, data: CrearHorarioRequest) {
  return apiFetch<Horario>(`/agenda/horarios/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function eliminarHorario(id: number, negocioId: number) {
  return apiFetch<void>(`/agenda/horarios/${id}?negocioId=${negocioId}`, {
    method: 'DELETE',
  });
}

export function consultarDisponibilidad(servicioId: number, fecha: string, trabajadorId?: number | null) {
  const q = new URLSearchParams({
    servicioId: String(servicioId),
    fecha,
  });
  if (trabajadorId) q.set('trabajadorId', String(trabajadorId));
  return apiFetch<Disponibilidad>(`/agenda/disponibilidad?${q.toString()}`);
}

export function crearReserva(data: CrearReservaRequest) {
  return apiFetch<Reserva>('/agenda/reservas', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function listarReservasPorCliente(clienteId: number) {
  return apiFetch<Reserva[]>(`/agenda/reservas?clienteId=${clienteId}`);
}

export function listarReservasPorNegocio(negocioId: number, desde: string, hasta: string) {
  const q = new URLSearchParams({
    negocioId: String(negocioId),
    desde,
    hasta,
  });
  return apiFetch<Reserva[]>(`/agenda/reservas?${q.toString()}`);
}

export function listarTrabajadores(negocioId: number) {
  return apiFetch<Trabajador[]>(`/agenda/trabajadores?negocioId=${negocioId}`);
}

export function listarTrabajadoresPorServicio(servicioId: number) {
  return apiFetch<Trabajador[]>(`/agenda/trabajadores?servicioId=${servicioId}`);
}

export function crearTrabajador(data: CrearTrabajadorRequest) {
  return apiFetch<Trabajador>('/agenda/trabajadores', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export function actualizarTrabajador(id: number, data: CrearTrabajadorRequest) {
  return apiFetch<Trabajador>(`/agenda/trabajadores/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export function eliminarTrabajador(id: number, negocioId: number) {
  return apiFetch<void>(`/agenda/trabajadores/${id}?negocioId=${negocioId}`, {
    method: 'DELETE',
  });
}
