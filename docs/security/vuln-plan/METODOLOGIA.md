# Metodología aplicada a SlotOne

**Versión:** 1.0 · **Última actualización:** mayo 2026

## Objetivo y alcance (SlotOne)

- **Código:** `frontend/` (React + Vite), `backend/usuarios`, `backend/gateway`, `backend/negocios`, `backend/agenda` (Spring Boot).
- **Dependencias:** npm (frontend), Maven (backend).
- **Configuración:** `application.properties`, variables `JWT_*`, `SLOTONE_*`, `SMTP_*`, `.env` del frontend (no versionadas).
- **Superficie:** JWT en gateway y `usuarios`, CORS configurable, comunicación REST entre microservicios, H2/PG según entorno.

## Fases ejecutadas sobre el repositorio

1. **Fase 1 – Detección inicial:** herramienta automatizada en frontend (`npm audit`). Backend: recomendar OWASP Dependency-Check o dependabot/Snyk en CI (**[PENDIENTE DE VALIDACIÓN HUMANA]** si no está en pipeline).
2. **Fase 2 – Análisis estructural:** revisión de rutas públicas vs protegidas, codificación de credenciales, secretos por defecto, almacenamiento de tokens en el cliente y exposición H2 — ver `FASE_2_ANALISIS_ESTRUCTURAL.md`.
3. **Fase 3 – Documentación:** fichas CVSS/CWE en matriz (`ANEXO_B_MATRIZ_SEGUIMIENTO.md`). Ampliación con fichas Markdown por hallazgo al priorizar fixes.
4. **Fase 4 – Remediación:** rama por hallazgo `fix/vuln-SL-<n>`, PR con evidencia (tests + escaneo post-fix).
5. **Fase 5 – Integración continua:** incorporar escaneos de dependencias y SAST al flujo definido por el equipo (Render/Vercel + Git).

## Estándares de referencia

- OWASP Top 10 (2021/2023)
- CWE/SANS Top 25
- CVSS v3.1
- NIST SSDF SP 800-218

## Trazabilidad

No fusionar cambios solo con “opinión de IA”: checklist en `ANEXO_A_CHECKLIST_IA.md` + aprobación de par en PR.
