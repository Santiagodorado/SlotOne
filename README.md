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
- **Base de datos:** H2 (desarrollo) / PostgreSQL (producción).

---

## Estructura del proyecto

slotone/
├── backend/           # Microservicios Spring Boot
│   ├── usuarios/      # Autenticación y registro (puerto 5000)
│   ├── gateway/       # API Gateway para enrutamiento (puerto 8080)
│   └── ...            # Otros microservicios según diseño
├── frontend/          # Aplicación React (Vite) (puerto 5173)
├── .gitignore
└── README.md

(La estructura de microservicios dentro de `backend/` se define según el diseño del equipo; cada servicio puede tener su propio `pom.xml` y puerto.)

---

## Requisitos

- **Backend:** JDK 17+, Maven o Gradle.
- **Frontend:** Node.js 18+, npm o yarn.
- Base de datos: H2 para desarrollo, PostgreSQL para producción.

---

## Cómo ejecutar

**Orden de ejecución:** `usuarios` → `gateway` → `frontend`

### Backend (microservicios Spring Boot)

Cada microservicio se ejecuta por separado. Ejemplo con Maven:

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

La aplicación se sirve por defecto en `http://localhost:5173/`. El proxy de desarrollo está configurado para reenviar `/api` al backend en `http://localhost:8080`.

---

## Variables de entorno

**Backend (Spring Boot):** en cada microservicio, `application.yml`:

- Conexión a base de datos (URL, usuario, contraseña)
- Puerto del servicio (ej. 5000, 8080)
- JWT: secreto y tiempo de expiración
- URLs de otros servicios para comunicación interna

**Frontend (React + Vite):** `.env` o `.env.local`:

- `VITE_API_URL` – URL base del API (si no se usa el proxy `/api` de Vite)

No subir `.env` ni credenciales al repositorio (están en `.gitignore`).

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

- Documento Sprint 1, arquitectura, historias de usuario y backlog: ver wiki del proyecto (Google Sites) o documento en Classroom.
