# Fase 1 — Escaneo automatizado (SlotOne)

## Respuesta estricta (plantilla oficial)

**¿El escáner automatizado encontró hallazgos?** En la corrida inicial: **SÍ** (`npm audit`). Tras `npm audit fix` en `frontend/` el informe pasó a **0 vulnerabilidades** (mayo 2026). Mantener **`npm audit`** en CI ante cada cambio de dependencias.

---

## npm audit (`frontend/`)

| Dependencia transitiva | Severidad | Notas |
|------------------------|-----------|-------|
| `picomatch` 4.0.x | alta | Corregido tras `npm audit fix` (mayo 2026) |
| `postcss` &lt; 8.5.10 | moderada | Corregido tras `npm audit fix` |
| `vite` 7.0.0–7.3.x | alta | Corregido tras `npm audit fix` |

**Estado actual:** `npm audit` sin vulnerabilidades conocidas (volver a ejecutar tras cada cambio de `package-lock.json`).

**Mitigación de contexto:** varios avisos históricos de Vite afectaban el **servidor de desarrollo**; mantener Vite y lockfile al día.

---

## Backend (Maven) — recomendación

Este documento **no sustituye** un job de CI. Registrar como **[PENDIENTE DE VALIDACIÓN HUMANA]**:

- `dependency-check-maven` o Renovate/Dependabot en `backend/*/pom.xml`
- Opcional: SonarQube / SpotBugs security con reglas OWASP en PRs

---

## Próximo paso

Continuar en [FASE_2_ANALISIS_ESTRUCTURAL.md](./FASE_2_ANALISIS_ESTRUCTURAL.md).
