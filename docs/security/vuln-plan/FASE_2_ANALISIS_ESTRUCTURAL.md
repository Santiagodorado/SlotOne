# Fase 2 — Análisis estructural y contextual

**Stack insertado:** Java 17, Spring Boot 3.5 (`usuarios`, `gateway`, `negocios`, `agenda`), Spring Cloud Gateway (JWT filter selectivo), React + Vite, H2/PG.

Análisis basado en rutas configuradas en `gateway.config.GatewayConfig`, filtros JWT, seguridad Spring en `usuarios`, ausencia de Spring Security en `negocios`/`agenda`, y uso de cliente en frontend.

Todos los niveles CVSS deben tratarse como **estimaciones** hasta revisionar entorno real y amenazas; marcar donde aplique **[PENDIENTE DE VALIDACIÓN HUMANA]**.

---

## Tabla resumen (hallazgos iniciales)

| Archivo / sección | Patrón de riesgo | Nivel | CWE / OWASP | Líneas / bloque | Justificación técnica |
|-------------------|------------------|-------|-------------|----------------|------------------------|
| `backend/usuarios/.../WebSecurityConfig.java` | `NoOpPasswordEncoder` | Alto | CWE-916; OWASP Top 10 A02 (Cryptographic Failures) | `passwordEncoder()` bean | Contraseñas sin hash fuerte en reposo ni en tiempo de uso del encoder; cualquier cliente de datos ve credenciales en texto claro cuando NoOp coincide con texto plano. |
| `backend/gateway/.../GatewayConfig.java` | rutas sin filtro JWT | Crítico | CWE-306; OWASP A01 Broken Access Control | rutas `/api/negocios/**`, `/api/agenda/**` | El gateway solo aplica `JwtGatewayFilter` a `/api/usuarios/**` y `/api/roles/**`; negocios y agenda quedan accesibles sin `Authorization`. Los microservicios no incluyen `spring-boot-starter-security`. |
| `backend/*/application.properties` (usuarios/gateway) | secreto JWT por defecto | Alto | CWE-798; A02 | `jwtSecret=${JWT_SECRET:…}` fallback Base64 público | Cualquier despliegue sin `JWT_SECRET` comparte secreto conocido entre instancias; firma JWT comprometida. |
| `backend/gateway/.../JwtUtils.java`; `usuarios/.../jwt/JwtUtils.java` | JWT en logs | Medio | CWE-532; A09 Security Logging Failures | `logger.info` / `println` del token completo | Fuga del token JWT a logs/agregadores; elevación lateral si hay acceso a logs. |
| `backend/usuarios/.../WebSecurityConfig.java` | H2 `/h2-console/**` público | Medio/Informativo\* | CWE-749; A05 Security Misconfiguration | `permitAll` + console | En dev es cómodo; en prod/H2 expuesto es riesgo. \*Depende del entorno (**[PENDIENTE]**). |
| `frontend/` (ej. `Login.tsx`) | JWT en `localStorage` | Medio | CWE-922; relacionado XSS → A07 | `localStorage.setItem('token', …)` | Cualquier XSS roba el Bearer; mitigation: mitigar XSS + considerar cookies HttpOnly detrás del mismo dominio/policy. |
| `backend/usuarios/.../UsuarioServiceImpl.java` | Actualización usuario sin hash en `clave` | Alto | CWE-916; A02 | `update`: `setClave(dto.getClave())` | Registro nuevo usa encoder; actualización puede guardar texto plano e incoherente con login. **[PENDIENTE DE VALIDACIÓN HUMANA]** si flujo PUT no llega desde API. |

**Seguro / omitido tras muestreo:** consultas parametrizadas en `UsuarioRepository` con `PreparedStatement` — sin evidencia de concatenación dinámica en los métodos revisados (`findByEmail`, etc.).

---

## Falsos positivos / decisión abierta

- **Contraseña en README como credenciales de prueba:** riesgo de procedimiento/académico, no bug de código; mitigar política doc + rotación.
- **`path.contains("/auth/")` en gateway:** permite subrutas con segmento `/auth/`; revisar naming futuro (**[INFORMATIVO]**).

---

## Próximo paso

Formalizar fichas CVSS por ID en [ANEXO_B_MATRIZ_SEGUIMIENTO.md](./ANEXO_B_MATRIZ_SEGUIMIENTO.md); usar prompts de [prompt-templates.md](./prompt-templates.md) para iteraciones siguientes tras cambiar código.
