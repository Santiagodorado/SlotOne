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

