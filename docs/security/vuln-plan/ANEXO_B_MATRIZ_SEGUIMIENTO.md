# Anexo B — Matriz de seguimiento de vulnerabilidades (SlotOne)

Actualizar ante cada PR de remediación. Estados sugeridos: `Abierto` | `En remediación` | `PR en revisión` | `Validado` | `Aceptado riesgo`.

**Todos los CVSS siguientes:** estimación inicial técnica; **[PENDIENTE DE VALIDACIÓN HUMANA]** para producción real.

---

## Registro rápido

| ID | Componente | Vulnerabilidad | Nivel | CVSS estimado\* | Estado | Responsable | Evidencia cierre |
|----|-------------|----------------|-------|-----------------|--------|----------------|------------------|
| VULN-SL-001 | `usuarios` | `NoOpPasswordEncoder` sin hash fuerte | Alto | 8.8 (ej.) | **Remediado en código** | | BCrypt + `data.sql`; validar en prod |
| VULN-SL-002 | `gateway` + `negocios` + `agenda` | Bypass de gateway / micros sin auth propia | Alto | ~7–8 | **Mitigado (defensa en prof.)** | | Gateway + JWT en cada micro (`oauth2-resource-server`) con mismas rutas públicas; seguir ocultando URL directa micro en prod |
| VULN-SL-003 | todos los backends con JWT | Secreto por defecto versionado | Alto | 7.7 | **Mitigado** | | Modo estricto: Render / `prod` / `SLOTONE_STRICT_JWT_SECRET`; falla arranque si secret débil/default |
| VULN-SL-004 | `gateway`,`usuarios` | Token JWT registrado completo en logs | Medio | 5.3 | **Remediado en código** | | Quitado print/log del token |
| VULN-SL-005 | varios backends | Console H2 `permitAll` / H2 prod | Medio† | 5.5† | **Parcial** | | H2 console desactivada por defecto (`usuarios`: `ENABLE_H2_CONSOLE=true` solo local) |
| VULN-SL-006 | `frontend` | Bearer en `localStorage` | Medio | 4.9 (prep. XSS) | **Parcial** | | CSP + cabeceras en `vercel.json`; gateway envía nosniff/referrer/frame; sanitizar vistas / migrar cookie HttpOnly requiere diseño posterior |
| VULN-SL-007 | `UsuarioServiceImpl.update` | Clave actualizada sin re-hash | Alto | 7.8 | **Remediado en código** | | Solo re-hashea si la clave viene no vacía |
| VULN-SL-008 | `frontend` | Dependencias npm — `npm audit` | Med–High | Variable | **Remediado en código** | | `npm audit fix` → 0 hallazgos (revalidar en CI) |

\*Valores ejemplo; el revisor debe ajustar vector y impacto por despliegue.  
†Alto si H2/expuesto público; bajo/informativo sólo desarrollo local aislado.

---

## VULN-SL-001 — Encoder de contraseñas

- **Descripción:** Uso explícito de `NoOpPasswordEncoder` como bean en Spring Security (`WebSecurityConfig`).
- **Impacto:** Confidencialidad e integridad de credenciales de usuario.
- **CVSS:** 8.8 (estimación) \| AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:N — *[PENDIENTE DE VALIDACIÓN HUMANA]*.
- **Referencias:** [CWE-916](https://cwe.mitre.org/data/definitions/916.html), [Spring PasswordEncoder BCrypt](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html).
- **Remediación:** Sustituir por `new BCryptPasswordEncoder()` u otro recomendado; migración gradual de usuarios demo (seed) con nuevo hash en entorno nuevo.
- **Validación:** Test de login; inspeccionar tabla `usuario` tras registro (**no** plano BCrypt esperado `$2a$`).

---

## VULN-SL-002 — Broken access control gateway / micros

- **Descripción:** Rutas `/api/negocios/**` y `/api/agenda/**` no pasan por `JwtGatewayFilter`. Microservicios no montan seguridad declarada en `pom.xml`.
- **Impacto:** Manipulación de negocios, reservas, horarios si el host del micro/gateway es alcanzable.
- **CVSS:** 9.1 (estimación) \| *[PENDIENTE DE VALIDACIÓN HUMANA]*.
- **Referencias:** [OWASP API1 Broken Object Level Authorization](https://owasp.org/API-Security/).
- **Remediación (opciones):** (a) mismo filtro JWT en gateway para rutas protegidas + opcional forwarding claims; (b) mTLS/red privada exclusiva gateway→micro sin exposición directa en Render/VPS + validación JWT en cada micro; (c) Spring Security `@PreAuthorize`/resource server en cada servicio con validación JWKS mismo secret.
- **Validación:** Pruebas con `curl` sin `Authorization` → 401; con token válido → 200 sólo donde corresponde.

---

### VULN-SL-003 a SL-008 (resumen ejecutivo)

- **SL-003:** Forzar ausencia de default: fallar arranque si `JWT_SECRET` vacío/corto; rotar secreto tras exposición documentada del valor por defecto.
- **SL-004:** Quitar prints/log de secreto/session; nivel trace sólo desarrollo opt-in.
- **SL-005:** `spring.h2.console.enabled=false` perfil prod; cerrar rutas `/h2-console` en perfil público.
- **SL-006:** Minimizar datos en `localStorage`; endurecer XSS en React + headers `Content-Security-Policy`.
- **SL-007:** En `update`, si `dto.getClave()` no vacía → `passwordEncoder.encode` antes de persistir (y considerar política si vacía = mantener anterior).
- **SL-008:** Ejecutar `npm audit fix` y congelar versión después de QA.

---

Última edición máquina/analista IA: mayo 2026 — debe **firmarse** tras revisión humana.
