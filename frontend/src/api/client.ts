const baseURL = import.meta.env.VITE_API_URL ?? '/api';

function usesAuthorizationHeader(headers?: HeadersInit): boolean {
  if (!headers) return false;
  if (headers instanceof Headers) return headers.has('Authorization');
  if (Array.isArray(headers))
    return headers.some(([name]) => name.toLowerCase() === 'authorization');
  return Object.keys(headers).some((k) => k.toLowerCase() === 'authorization');
}

function friendlyHttpMessage(status: number): string {
  switch (status) {
    case 409:
      return 'Esta acción entra en conflicto con lo que ya existe. En horarios: no puedes repetir o solapar franjas el mismo día para el mismo servicio.';
    case 404:
      return 'No se encontró lo solicitado.';
    case 400:
      return 'Los datos enviados no son válidos.';
    case 403:
      return 'No tienes permiso para esta acción.';
    default:
      return '';
  }
}

/** Mensaje legible desde respuestas de error (`message`, ProblemDetail `detail`, etc.). */
function messageFromErrorBody(body: unknown, statusText: string, status: number): string {
  if (body == null) {
    return friendlyHttpMessage(status) || statusText || 'Error en la solicitud';
  }
  if (typeof body === 'string' && body.trim()) {
    return body.trim();
  }
  if (typeof body !== 'object') {
    return friendlyHttpMessage(status) || statusText || 'Error en la solicitud';
  }
  const o = body as Record<string, unknown>;
  if (typeof o.message === 'string' && o.message.trim()) {
    return o.message.trim();
  }
  if (typeof o.detail === 'string' && o.detail.trim()) {
    return o.detail.trim();
  }
  if (Array.isArray(o.errors) && o.errors.length > 0) {
    const first = o.errors[0];
    if (typeof first === 'string') return first;
    if (first && typeof first === 'object' && 'defaultMessage' in first) {
      const dm = (first as { defaultMessage?: string }).defaultMessage;
      if (typeof dm === 'string' && dm) return dm;
    }
  }
  if (typeof o.error === 'string' && o.error.trim()) {
    const err = o.error.trim();
    if (err.toLowerCase() === 'conflict' || err.toLowerCase() === 'bad request') {
      const friendly = friendlyHttpMessage(status);
      if (friendly) return friendly;
    }
    return err;
  }
  const fallback = friendlyHttpMessage(status) || statusText || 'Error en la solicitud';
  return fallback;
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const url = path.startsWith('http') ? path : `${baseURL}${path}`;
  const token =
    typeof localStorage !== 'undefined' ? localStorage.getItem('token') : null;

  const merged = new Headers({ 'Content-Type': 'application/json' });
  if (token && !usesAuthorizationHeader(options.headers)) {
    merged.set('Authorization', `Bearer ${token}`);
  }
  if (options.headers) {
    const extra = new Headers(options.headers);
    extra.forEach((value, key) => merged.set(key, value));
  }

  const res = await fetch(url, {
    ...options,
    headers: merged,
  });

  if (!res.ok) {
    const text = await res.text();
    let parsed: unknown = null;
    if (text) {
      try {
        parsed = JSON.parse(text) as unknown;
      } catch {
        parsed = text.trim() || null;
      }
    }
    throw new Error(messageFromErrorBody(parsed, res.statusText, res.status));
  }

  if (res.status === 204) {
    return undefined as T;
  }

  const text = await res.text();
  if (!text) {
    return undefined as T;
  }
  return JSON.parse(text) as T;
}
