# Plantillas de prompts (SlotOne)

Copiar en el asistente. **Stack fijo:** Spring Boot microservicios (Java 17), Spring Cloud Gateway, JWT, React + Vite, H2 desarrollo / PostgreSQL producción.

---

## Fase 1: Verificación inicial (tras escaneos IDE/GitHub/npm)

```
[ROL] Actúa como Ingeniero Senior en Ciberseguridad especializado en SAST/AppSec.

[CONTEXTO] Ejecutamos Fase 1 en SlotOne. Resultado conocido del repo: npm audit en frontend/SÍ tiene hallazgos; backend sin job automático ejecutado aquí.

[INSTRUCCIÓN] Responde EXACTAMENTE con:
- "SÍ" si el editor/npm audit reportó vulnerabilidades confirmadas o de alta probabilidad.
- "NO" si no hay reportes.
Si "NO": añade "PROCEDER CON FASE 2: ANÁLISIS ESTRUCTURAL".

[RESTRICCIONES] Sin tablas ni listado de CVE en esta respuesta.
```

(Uso habitual: después de ejecutar realmente los escaneos y pegar sólo el resultado SÍ/NO.)

---

## Fase 2: Análisis estructural contextual

```
[ROL] Arquitecto de Seguridad y analista de código senior OWASP/CWE sobre Spring Boot, Spring Cloud Gateway, JWT y SPA React+Vite.

[CONTEXTO] SlotOne tiene gateway en 8080, micros usuarios/negocios/agenda en 5000/5004/5005. JWT filtra solo algunas rutas según GatewayConfig.java. El siguiente bloque lista paquetes y archivos (PEGA ÁRBOL/AQUÍ).

[INSTRUCCIÓN] Por cada elemento relevante:
1. Puntos de entrada, datos sensibles, authz, sanitización y configuración.
2. Nivel Crítico/Alto/Medio/Bajo/Informativo.
3. CWE y OWASP Top 10 donde aplique.
4. Fragmento/implicación técnica (sin inventar código no mostrado).

[FORMATO] Tabla: Archivo/Sección | Patrón | Nivel | CWE/OWASP | Líneas | Justificación

[RESTRICCIONES] No inventes. Marca Seguro donde corresponda. Indica [PENDIENTE DE VALIDACIÓN HUMANA] si falta código.
```

---

## Fase 3: Documentación por hallazgo

```
[ROL] Auditor técnico y compliance.

[CONTEXTO] Tabla previa validada manualmente donde marqué CONFIRMADO. Genera ficha por fila útil.

[INSTRUCCIÓN] Por hallazgo: ID tipo VULN-SL-nnn; descripción; impacto CID; CVSS v3.1 base estimado + justificación corta; referencias CWE/OWASP/NIST/oficial framework; remediate con ejemplo mínimo; validación (test/SAST/manual).

[RESTRICCIONES] Fuentes oficiales. Sin blogs no auditados. Si no cerrable sin prueba → [PENDIENTE DE VALIDACIÓN HUMANA].

[FORMATO] Markdown jerárquico según METODOLOGIA del plan académico.
```

---

## Recordatorio equipo

Marcar cada salida grande con **[PENDIENTE DE VALIDACIÓN HUMANA]** hasta merge con evidencia (`ANEXO_B` cerrado por responsable).
