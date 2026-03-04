# SlotOne – Plataforma de agendamiento y reservas

Proyecto II – Ingeniería de Sistemas – Unicauca.

Plataforma web SaaS de agendamiento y reservas multi-negocio. Permite a distintos negocios (peluquerías, consultorios, gimnasios, etc.) ofrecer reservas en línea; los clientes consultan disponibilidad y reservan sin depender del teléfono.

- **Equipo:** SlotOne  
- **Integrantes:** Santiago Dorado, Felipe Pino, Brayan Benavides  
- **Product Owner:** Santiago Dorado  

---

## Stack técnico

- **Backend:** microservicios con **Spring Boot** (Java).
- **Frontend:** **React** con **Vite**.

---

## Estructura del proyecto

```
slotone/
├── backend/           # Microservicios Spring Boot
│   ├── auth-service/      # Autenticación, registro, login
│   ├── business-service/  # Negocios, servicios, horarios
│   └── ...                 # Otros microservicios según diseño
├── frontend/          # Aplicación React (Vite)
├── .gitignore
└── README.md
```

*(La estructura de microservicios dentro de `backend/` se define según el diseño del equipo; cada servicio puede tener su propio `pom.xml` y puerto.)*

---

## Requisitos

- **Backend:** JDK 17+, Maven o Gradle.
- **Frontend:** Node.js 18+, npm o yarn.
- Base de datos (PostgreSQL).

---

## Cómo ejecutar

### Backend (microservicios Spring Boot)

Cada microservicio se ejecuta por separado. Ejemplo con Maven:

```bash
cd backend/auth-service
mvn spring-boot:run
```

```bash
cd backend/business-service
mvn spring-boot:run
```

Configurar `application.yml` o `.env` en cada servicio (puerto, conexión BD, JWT, etc.). Ver README dentro de cada carpeta de servicio cuando exista.

### Frontend (React + Vite)

```bash
cd frontend
npm install
npm run dev
```

La aplicación se sirve por defecto en `http://localhost:5173/`. El proxy de desarrollo está configurado para reenviar `/api` al backend en `http://localhost:8080`.

---

## Variables de entorno

**Backend (Spring Boot):** en cada microservicio, `application.yml` o `.env`:

- Conexión a base de datos (URL, usuario, contraseña)
- Puerto del servicio (ej. 8080, 8081)
- JWT: secreto y tiempo de expiración
- Configuración de otros servicios si se llaman entre sí

**Frontend (React + Vite):** `.env` o `.env.local`:

- `VITE_API_URL` – URL base del API (si no se usa el proxy `/api` de Vite)

No subir `.env` ni credenciales al repositorio (están en `.gitignore`).

---

## Ramas y trabajo en equipo

- **main** – código estable e integrado  
- **frontend/santiago** – desarrollo frontend  
- **backend/brayan** – desarrollo backend  
- O ramas por feature: `feature/hu-s1-01-login`, etc.  

Hacer Pull Request a `main` cuando una funcionalidad esté lista.

---

## Documentación

- Documento Sprint 1, arquitectura, historias de usuario y backlog: ver wiki del proyecto (Google Sites) o documento en Classroom.
