import { apiFetch } from './client';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  nombres: string;
  apellidos: string;
  correo: string;
  tipoIdentificacion: string;
  numIdentificacion: string;
  rol: string;
}

export interface RegisterRequest {
  nombres: string;
  apellidos: string;
  correo: string;
  clave: string;
  tipoIdentificacion: 'CC' | 'CE';
  numIdentificacion: number;
  idRol: number;
}

export interface RegisterResponse {
  id: number;
  nombres: string;
  apellidos: string;
  correo: string;
}

export async function login(data: LoginRequest): Promise<AuthResponse> {
  return apiFetch<AuthResponse>('/usuarios/auth/login', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export async function register(data: RegisterRequest): Promise<RegisterResponse> {
  return apiFetch<RegisterResponse>('/usuarios', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}
