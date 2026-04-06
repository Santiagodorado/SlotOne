import { apiFetch } from './client';

export interface Negocio {
  id: number;
  nombre: string;
  descripcion?: string | null;
  direccion: string;
  telefono: string;
  logoUrl?: string | null;
  duenioId: number;
}

export interface CrearNegocioRequest {
  nombre: string;
  descripcion?: string;
  direccion: string;
  telefono: string;
  logoUrl?: string | null;
  duenioId: number;
}

export async function listarNegocios(): Promise<Negocio[]> {
  return apiFetch<Negocio[]>('/negocios');
}

export async function listarNegociosPorDuenio(duenioId: number): Promise<Negocio[]> {
  return apiFetch<Negocio[]>(`/negocios?duenioId=${duenioId}`);
}

export async function crearNegocio(data: CrearNegocioRequest): Promise<Negocio> {
  return apiFetch<Negocio>('/negocios', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export async function actualizarNegocio(id: number, data: CrearNegocioRequest): Promise<Negocio> {
  return apiFetch<Negocio>(`/negocios/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

