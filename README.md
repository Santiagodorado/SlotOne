# SlotOne – Plataforma de agendamiento y reservas

Proyecto II – Ingeniería de Sistemas – Unicauca.

Plataforma web SaaS de agendamiento y reservas multi-negocio. Permite a distintos negocios (peluquerías, consultorios, gimnasios, etc.) ofrecer reservas en línea; los clientes consultan disponibilidad y reservan sin depender del teléfono.

- **Equipo:** SlotOne  
- **Integrantes:** Santiago Dorado, Felipe Pino, Brayan Benavides  
- **Product Owner:** Santiago Dorado  

---

## Stack técnico

- **Backend:** microservicios con **Spring Boot** (Java 17+).
- **Frontend:** **React** con **Vite**.
- **Base de datos:** **PostgreSQL** (local con `docker-compose.yml` o servidor gestionado). Tres BD lógicas: `slotone_usuarios`, `slotone_negocios`, `slotone_agenda` (véase `deploy/postgres/init-databases.sql`).

---

## Estructura del proyecto

slotone/
├── backend/           # Microservicios Spring Boot
│   ├── usuarios/      # Autenticación y registro (puerto 5000)
│   ├── gateway/       # API Gateway para enrutamiento (puerto 8080)
│   └── ...            # Otros microservicios según diseño
├── deploy/postgres/   # Script de creación de bases (Docker)
├── docker-compose.yml # Postgres local opcional
├── frontend/          # Aplicación React (Vite) (puerto 5173)
├── .gitignore
└── README.md

(La estructura de microservicios dentro de `backend/` se define según el diseño del equipo; cada servicio puede tener su propio `pom.xml` y puerto.)

---

## Requisitos

- **Backend:** JDK 17+, Maven o Gradle.
- **Frontend:** Node.js 18+, npm o yarn.
- **PostgreSQL** (Docker o servicio gestionado) y **Docker** si usas `docker-compose.yml` en la raíz de `slotone`.

---

## Cómo ejecutar

**Orden recomendado (local):** `usuarios` → `negocios` → `agenda` → `gateway` → `frontend`. Ver tabla en `ARRANQUE.md`.

### Backend (microservicios Spring Boot)

Cada microservicio se ejecuta por separado. Para un arranque mínimo vía gateway y front suele ejecutarse **`usuarios`**, **`gateway`** y **`frontend`**; **`negocios`** y **`agenda`** también son necesarios para catálogo y reservas. Ejemplo con Maven (`negocios` / `agenda` análogo):

# 1. Servicio de usuarios (puerto 5000)
cd backend/usuarios
mvn spring-boot:run

# 2. API Gateway (puerto 8080)
cd backend/gateway
mvn spring-boot:run

Configurar `application.yml` en cada servicio según el entorno. No incluir archivos de configuración con credenciales en el repositorio.

### Frontend (React + Vite)

cd frontend
npm install
npm run dev

# Tests: `npm run test` · `npm run test:watch` — Vitest sobre `*.test.ts(x)` (App, `api/client`, Navbar, Login).

La aplicación se sirve por defecto en `http://localhost:5173/`. El proxy de desarrollo está configurado para reenviar `/api` al backend en `http://localhost:8080`.

---

## Variables de entorno

Seguridad relevante del gateway y del front:

- **`JWT_SECRET` / `JWT_EXPIRATION_MS`:** idénticos en **`usuarios`**, **`gateway`**, **`negocios`** y **`agenda`** (todos validan el mismo HMAC). Ejemplo de generación: `openssl rand -base64 32`.
- **Validación estricta del secreto:** si `SLOTONE_STRICT_JWT_SECRET=true`, el perfil Spring es `prod`/`production`, **o** existe `RENDER=true` (típico en Render), los servicios **no arrancarán** con el `JWT_SECRET` por defecto del repositorio ni con un Base64 demasiado corto; define `JWT_SECRET` en el panel de variables.
- **Rutas sin JWT (públicas):** login/registro en `usuarios`; `GET /api/negocios` sin `duenioId`; `GET /api/negocios/{id}`; en `agenda`, las lecturas de catálogo (`servicios`, `horarios`, `trabajadores`, `disponibilidad`, `horarios/cubre`) y **`POST /api/agenda/reservas`**. El resto (incl. `GET /api/negocios?duenioId=` y **listar reservas**) exige `Authorization: Bearer <token>`.
- **`negocios`** y **`agenda`** aplican Spring Security OAuth2 Resource Server con el mismo modelo de rutas públicas; igualmente conviene que solo el **gateway** sea el punto público conocido por el navegador.

**Correo (solo micro `agenda`)**

- **No usa `JWT_SECRET`.** El “secreto” del correo es la credencial SMTP: sobre todo **`SMTP_PASSWORD`** (contraseña de aplicación de Gmail, API key de Resend/SendGrid/Brevo, etc.). `JWT_SECRET` sigue siendo solo para firmar tokens entre microservicios.
- Con **`SLOTONE_MAIL_ENABLED=false`** (por defecto en local), al crear una reserva el servicio **no envía** correo y deja trazas **`[EMAIL DESHABILITADO]`** en el log con el cuerpo que habría mandado.
- Con **`SLOTONE_MAIL_ENABLED=true`**, Spring Mail usa `spring.mail.*` mapeadas desde variables de entorno (`application.properties` en `agenda`):

| Variable | Uso |
|---------|-----|
| `SLOTONE_MAIL_ENABLED` | `true` para enviar |
| `SLOTONE_MAIL_FROM` | Remitente visible (debe estar autorizado por el proveedor SMTP) |
| `SLOTONE_MAIL_BUSINESS_TO` | Opcional; destino fijo del negocio si en base de datos no hay `correo` en el negocio (si hay, se usa ese). |
| `SMTP_HOST` | Ej. `smtp.gmail.com`, `smtp.resend.com` |
| `SMTP_PORT` | Ej. `587` (STARTTLS) |
| `SMTP_USERNAME` / `SMTP_PASSWORD` | Usuario y clave/API key del proveedor |
| `SMTP_AUTH` | `true` en casi todos los proveedores |
| `SMTP_STARTTLS` | `true` típico en puerto 587 |

Plantilla de variables: `backend/agenda/mail-env.example`. **Gmail:** activa verificación en dos pasos y genera una **contraseña de aplicación**; no uses tu clave normal de cuenta.

**Dónde ponerlas en local (para que arranquen siempre)**

- **`backend/agenda/.env`** o **`mail.env`**: copia desde `mail-env.example`; formato `CLAVE=valor`. Si existen ambos, se usa solo **`.env`**. Estos archivos van en `.gitignore`. Arranca **`agenda`** con `.\run-local.ps1` (PowerShell) para cargar variables en ese proceso antes de Maven.
- **Variables de usuario de Windows**: Configuración → Sistema → Acerca de → Configuración avanzada del sistema → Variables de entorno → *Usuario*. Ahí cargan todas las nuevas sesiones/consolas; puede seguir usando `mvn spring-boot:run` sin script.

**Cómo proteger el secreto**

- No pongas **`SMTP_PASSWORD`** en Markdown, Discord, Classroom ni commits. Si ya filtró, **revoca** la contraseña de aplicación/API key en el proveedor y genera una nueva.
- Producción (**Render**, etc.): solo en las **environment variables / secrets** del servicio; igual que `DATABASE_URL`.
- Opcionalmente **contraseña de aplicación solo para SlotOne**, no tu clave Gmail principal si usas ese proveedor.

**Base de datos (PostgreSQL)**

- **`DATABASE_URL`** (JDBC): por defecto en cada micro apunta a `localhost:5433` (Docker Compose; evita el PostgreSQL típico de Windows en `:5432`) con una BD distinta: `jdbc:postgresql://localhost:5433/slotone_usuarios`, `slotone_negocios`, `slotone_agenda`. En Render u otro PaaS, sustituye por la cadena JDBC que te dé el proveedor (con esquema `jdbc:postgresql:...`).
- **`DB_USERNAME`** / **`DB_PASSWORD`:** por defecto `slotone` / `slotone_dev` (`docker-compose.yml` usa esos valores).

**Backend (Spring Boot):** en cada microservicio, configuración habitual:

- JDBC (`DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD`), puerto, JWT (`JWT_SECRET`, etc.) y URLs de otros servicios.

**Frontend (React + Vite):** `.env` o `.env.local`:

- `VITE_API_URL` – URL base del API (si no se usa el proxy `/api` de Vite)

En **Vercel**, los encabezados de endurecimiento (incl. una CSP inicial) están en `frontend/vercel.json`; ajústalos si el API vive en un dominio fijo aparte del front.

No subir `.env` ni credenciales al repositorio (están en `.gitignore`).

---

## Despliegue (Vercel + Render)

**Frontend (Vercel):** define `VITE_API_URL` con la URL pública del API Gateway y el prefijo `/api`, por ejemplo `https://tu-gateway.onrender.com/api`.

**Backend (Render):** Render no incluye JVM en runtimes nativos; cada micro tiene un **`Dockerfile`** junto al `pom.xml`. Opcionalmente sincroniza el blueprint **`render.yaml`** (Dashboard → Blueprint) para crear los cuatro Web Services.

Después del primer deploy, anota las URLs públicas **`https://...onrender.com`** (sin barra final) de `usuarios`, `negocios` y `agenda`:

- En **`slotone-gateway`** configura `SLOTONE_USUARIOS_URL`, `SLOTONE_NEGOCIOS_URL`, `SLOTONE_AGENDA_URL`; `JWT_SECRET` y opcionalmente `JWT_EXPIRATION_MS` **iguales** que en **`usuarios`**; `SLOTONE_ALLOWED_ORIGINS` con el origen exacto del front en Vercel (varios valores separados por coma si hay staging y prod).
- En **`agenda`** configura `SLOTONE_NEGOCIOS_BASE_URL` con la URL pública del servicio negocios.
- En **`agenda`**, correo (opcional): en el panel del servicio define `SLOTONE_MAIL_ENABLED=true`, `SLOTONE_MAIL_FROM`, `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_AUTH=true`, `SMTP_STARTTLS=true` (ver tabla arriba y `backend/agenda/mail-env.example`). Sin eso no se envían correos; solo se registra simulación en logs.

Redespliega o reinicia el **gateway** al cambiar esas variables. En Render se usa la variable **`PORT`** (ya referenciada en `application.properties`); en local siguen valiendo los puertos de la tabla anterior. Define **`DATABASE_URL`**, **`DB_USERNAME`** y **`DB_PASSWORD`** por cada micro; conviene crear **tres bases de datos** dentro de la misma instancia Postgres (como en el `deploy/postgres/init-databases.sql` local) y apuntar cada servicio a la suya.

---

## Credenciales de prueba

| Rol | Email | Contraseña |
|-----|-------|-----------|
| PLATFORM_ADMIN | juan.lopez@unicauca.edu.co | password202 |
| BUSINESS | pedro.gomez@unicauca.edu.co | password123 |
| CLIENT | laura.rodriguez@unicauca.edu.co | password111 |

>  Estas credenciales son únicamente para entorno de desarrollo.

---

## Verificación rápida

Para verificar que el servicio de autenticación está funcionando correctamente:

curl http://localhost:8080/api/usuarios/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan.lopez@unicauca.edu.co","password":"password202"}'

---

## Ramas y trabajo en equipo

- **main** – código estable e integrado  
- **frontend/santiago** – desarrollo frontend  
- **backend/brayan** – desarrollo backend  
- **backend/felipe** – desarrollo backend  
- O ramas por feature: `feature/hu-s1-01-login`, etc.  

Hacer Pull Request a `main` cuando una funcionalidad esté lista.

---

## Documentación

- Opcionalmente, documentación interna (p. ej. plan vuln Sprint) puede vivir solo en disco en la carpeta **`docs/`**; no forma parte del repo (véase `.gitignore`).
- Documento Sprint 1, arquitectura, historias de usuario y backlog: ver wiki del proyecto (Google Sites) o documento en Classroom.
