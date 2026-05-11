# Resumen del trabajo realizado — Seguridad SlotOne (exploración y remediación)

**Proyecto:** SlotOne (UNICauca · Proyecto II)  
**Marco:** Plan de exploración y reparación de vulnerabilidades (Sprint 3, documento académico)  
**Este documento:** consolida **qué se hizo**, **dónde está en el repo** y **qué queda como riesgo residual o mejora futura**.  
**Fecha de redacción:** mayo 2026

---

## 1. Objetivo

Aplicar un proceso **reproducible** (detección → análisis → documentación → remediación donde cupiera) sobre el código y la configuración del stack **Spring Boot (microservicios) + Spring Cloud Gateway + React/Vite**, sin sustituir la **revisión humana** ni una auditoría formal.

---

## 2. Artefactos creados (documentación)

| Ruta | Uso |
|------|-----|
| `docs/security/vuln-plan/README.md` | Índice del plan |
| `docs/security/vuln-plan/METODOLOGIA.md` | Fases y alcance aplicado al repo |
| `docs/security/vuln-plan/FASE_1_ESCANEO_AUTOMATIZADO.md` | `npm audit` (histórico y estado tras `audit fix`) |
| `docs/security/vuln-plan/FASE_2_ANALISIS_ESTRUCTURAL.md` | Tabla inicial OWASP/CWE con archivos tocados |
| `docs/security/vuln-plan/prompt-templates.md` | Plantillas de prompts para IA (roles, contextos) |
| `docs/security/vuln-plan/ANEXO_A_CHECKLIST_IA.md` | Checklist de validación de salidas de herramientas/IA |
| `docs/security/vuln-plan/ANEXO_B_MATRIZ_SEGUIMIENTO.md` | Matriz `VULN-SL-*` con estado y fichas cortas |

En **`README.md`** del proyecto hay un enlace a esta carpeta bajo «Documentación».

---

## 3. Primera iteración — Remediaciones en código

### 3.1 Autenticación y contraseñas (`usuarios`)

- **`BCryptPasswordEncoder`** sustituye a **`NoOpPasswordEncoder`** (`WebSecurityConfig`).
- **`data.sql`:** semillas de usuario con hashes **BCrypt** (las contraseñas en claro documentadas siguen siendo las de prueba del README).
- **`UsuarioServiceImpl`:** la **actualización** de usuario solo re-hashea si la clave nueva **no viene vacía**; método **login** alineado con `passwordEncoder.matches`.
- **`schema.sql`:** columna `clave` ampliada a **VARCHAR(255)** por margen ante hashes BCrypt.

### 3.2 API Gateway (`gateway`)

- **`JwtGatewayFilter`** aplicado también a **`/api/negocios/**`** y **`/api/agenda/**`** (antes solo parte de usuarios).
- Reglas explícitas de **ruta pública** vs **JWT obligatorio**, documentadas en el README del proyecto.

### 3.3 Frontend

- **`apiFetch`** adjunta **`Authorization: Bearer`** si existe token en **`localStorage`**, sin pisar headers explícitos del llamador.

### 3.4 Dependencias

- **`npm audit fix`** en `frontend/` hasta **0 vulnerabilidades** reportadas por npm (repetir ante cambios de lockfile).

### 3.5 H2

- Consola **H2 desactivada por defecto** en `negocios`/`agenda`; en `usuarios` vía **`ENABLE_H2_CONSOLE`** (principalmente desarrollo).

### 3.6 JWT en logs

- Eliminados **prints/logs del token JWT completo** en `usuarios` y `gateway`; validación con **`parseClaimsJws`**.

### 3.7 Pruebas

- **`JwtGatewayFilterRouteRulesTest`** (gateway): reglas de rutas públicas/protegidas.

---

## 4. Segunda iteración — Defensa en profundidad y secretos

### 4.1 Reglas públicas centralizadas (gateway)

- Clase **`gateway.security.PublicApiRouteRules`** con la tabla de comportamiento público (`normalizePath`, `isPublicGatewayRoute`, ramas usuarios/negocios/agenda).

### 4.2 Microservicios `negocios` y `agenda`

- **Spring Security** + **OAuth2 Resource Server** (`JwtDecoder` HS256,mismo secreto Base64 que `usuarios`/gateway).
- **`PublicApiRouteRules`** locales (subconjunto por servicio): **hay que mantenerlos alineados** con las reglas del gateway si cambian rutas públicas.
- **`JwtSecretStartupValidator`** + **`StrictJwtSecretPolicy`** (misma lógica de arranque estricto que los demás backends).

### 4.3 Validación del `JWT_SECRET` en arranque

En **los cuatro** servicios JWT (`usuarios`, `gateway`, `negocios`, `agenda`):

- Modo **estricto** si **`SLOTONE_STRICT_JWT_SECRET=true`**, perfiles **`prod`/`production`**, o **`RENDER=true`** (típico en Render): **no arrancan** con el secreto **por defecto del repo** ni con Base64 que decodifique a **menos de 32 bytes**.

### 4.4 Cabeceras HTTP

- **Gateway:** `GatewaySecurityHeadersFilter` (`nosniff`, referrer-policy, permissions-policy, `X-Frame-Options: DENY`).
- **Vercel (opcional si despliegan ahí):** `frontend/vercel.json` con CSP inicial y cabeceras de endurecimiento (ajustar `connect-src` si el dominio del API es fijo).

---

## 5. Matriz de hallazgos y estado consolidado (`VULN-SL-*`)

Referencias detalladas y CVSS orientativos: **`ANEXO_B_MATRIZ_SEGUIMIENTO.md`**.

| ID | Temática | Estado resumido |
|----|-----------|-----------------|
| SL-001 | Contraseñas sin hash fuerte | Remediado (BCrypt + seeds) |
| SL-002 | Gateway/microsin coherencia de auth | Mitigado (JWT en gateway + resource server micros + sync de rutas) |
| SL-003 | Secreto JWT débil/versionado | Mitigado (validación en arranque en modo estricto) |
| SL-004 | JWT en logs | Remediado |
| SL-005 | H2 expuesto por consola | Parcial (console off por defecto; usuarios opcional por env) |
| SL-006 | Token en `localStorage` (XSS) | Parcial (headers/CSP donde aplica; diseño futuro cookie HttpOnly + mismo dominio/BFF) |
| SL-007 | Update usuario sin hash | Remediado |
| SL-008 | CVEs npm | Remediado (último `audit fix`; vigilar CI) |

---

## 6. Riesgos / mejoras conscientemente no cerrados

1. **Token en `localStorage`:** mejor defensa ante XSS sería sesión por **cookie HttpOnly** + **origen único** o **BFF**; implica cambio de flujo de login, CORS con credenciales o proxy bajo mismo host (explicación ya plasmada cuando el equipo lo evalúe).
2. **Sincronización de reglas públicas:** `negocios`/`agenda` deben copiar/reflejar cambios que se hagan en **`gateway.security.PublicApiRouteRules`**.
3. **Autorización de negocio (roles, dueño vs cliente):** el JWT valida identidad y presencia del token donde aplica; la **autorización fina** (por ejemplo quién puede `duenioId=X`) puede requerir más lógica en servicios más adelante.
4. **CI con SAST/backend:** sigue recomendado **Maven dependency-check**, Dependabot o equivalente (**[PENDIENTE DE EQUIPO]**).

---

## 7. Cómo usar esto ante evaluación o auditoría rápida

1. Describir las **fases** (ver `METODOLOGIA.md`).
2. Mostrar **`ANEXO_B`** como trazabilidad.
3. Completar **`ANEXO_A`** después de usar IA o antes de cerrar sprint.
4. Dejar explícito: **revisiones humanas obligatorias** y que los CVSS del anexo son **estimaciones**.

---

## 8. Referencias rápidas de código tocado (lista no exhaustiva)

- Gateway: `JwtGatewayFilter`, `GatewayConfig`, `JwtUtils`, `PublicApiRouteRules`, `StrictJwtSecretPolicy`, `JwtSecretStartupValidator`, `GatewaySecurityHeadersFilter`.
- Usuarios: `WebSecurityConfig`, `JwtUtils`, `UsuarioServiceImpl`, `data.sql`, `schema.sql`, `StrictJwtSecretPolicy`, `JwtSecretStartupValidator`.
- Negocios / Agenda: `pom.xml` (security + oauth2 + jjwt), `NegociosSecurityConfig`/`AgendaSecurityConfig`, `JwtDecoderConfig`, `PublicApiRouteRules`, `JwtSecretStartupValidator`.
- Frontend: `src/api/client.ts`, `frontend/vercel.json`, `package-lock.json` tras `audit fix`.

---

*Documento descriptivo del trabajo ejecutado sobre el repositorio; no sustituye informe firmado ni certificación.*
