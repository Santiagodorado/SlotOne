# SlotOne · Arranque

Terminal abierta en la carpeta **`slotone`**.

| Orden | Qué hacer | Detalle |
|------:|-----------|---------|
| 0 | **PostgreSQL** | `docker compose up -d` — Postgres del proyecto escucha en el host **`localhost:5433`** (no `:5432`, para no pisar PostgreSQL instalado en Windows). Crea las tres BD; usuario/clave en `docker-compose.yml`. |
| 1 | usuarios | `cd backend\usuarios; mvn spring-boot:run` — puerto **5000**. Flyway aplica migraciones (`db/migration`) en la primera subida. |
| 2 | negocios | `cd backend\negocios; mvn spring-boot:run` — **5004** |
| 3 | agenda | `cd backend\agenda`: **`.\run-local.ps1`** (carga `.env` o **`mail.env`**) · o sin correo SMTP: `mvn spring-boot:run` — **5005** |
| 4 | gateway | `cd backend\gateway; mvn spring-boot:run` — **8080** |
| 5 | frontend | `cd frontend; npm run dev` — **5173** |

Luego abre **http://localhost:5173**.

Variables JDBC por defecto: `DATABASE_URL`, `DB_USERNAME`, `DB_PASSWORD` (ver README). Sin Postgres en ejecución, los microservicios no arrancan.

**Error `FATAL: password authentication failed for user "slotone"` (28P01):** suele ser **conectar al Postgres equivocado**: en Windows suele haber PostgreSQL instalado en `:5432` (otro usuario/clave); SlotOne usa **Docker en `localhost:5433`** (`slotone` / `slotone_dev`). Si aun así falla la clave con el puerto correcto, `docker compose down -v` y `docker compose up -d` (borra datos) o iguala contraseña y `DATABASE_URL`.