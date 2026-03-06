# API Gateway - Microservicios

Este API Gateway centraliza el acceso a todos los microservicios del sistema y maneja la autenticación JWT de forma unificada.

## 🚀 Funcionamiento

### Arquitectura
```
Frontend (Angular) → Gateway (Puerto 8080) → Microservicios
                        ↓
                   Validación JWT
```

### Rutas Configuradas

| Ruta | Microservicio | Puerto | Descripción |
|------|---------------|--------|-------------|
| `/api/usuarios/**` | usuarios | 5000 | Gestión de usuarios y autenticación |
| `/api/gestion-academica/**` | gestionacademica | 5001 | Gestión académica |
| `/api/competencias/**` | gestioncompetenciasyresultados | 5002 | Competencias |
| `/api/resultados/**` | gestioncompetenciasyresultados | 5002 | Resultados de aprendizaje |
| `/api/rubricas/**` | gestionrubricayevaluacion | 5003 | Rúbricas |
| `/api/evaluacion/**` | gestionrubricayevaluacion | 5003 | Evaluaciones |

## 🔐 Autenticación

### Flujo de Autenticación
1. **Login**: `POST http://localhost:8080/api/usuarios/auth/login`
2. **Recibir JWT**: El gateway pasa la petición al microservicio usuarios
3. **Usar JWT**: Incluir en header `Authorization: Bearer <token>` en todas las peticiones

### Rutas Públicas (sin JWT)
- `/api/usuarios/auth/**` - Autenticación
- `/api/usuarios/h2-console/**` - Consola H2 (desarrollo)

### Rutas Protegidas (requieren JWT)
- Todas las demás rutas requieren un JWT válido

## 🛠️ Configuración

### Prerrequisitos
1. **Java 17**
2. **Maven**
3. **Microservicios ejecutándose** en sus puertos respectivos

### Variables de Entorno
```properties
# JWT (deben coincidir con el microservicio usuarios)
bezkoder.app.jwtSecret===================claveSecreta======================
bezkoder.app.jwtExpirationMs=800000
```

## 🚦 Ejecución

```bash
# 1. Instalar dependencias
mvn clean install

# 2. Ejecutar el gateway
mvn spring-boot:run
```

El gateway estará disponible en: `http://localhost:8080`

## 📋 Orden de Inicio

1. **Microservicio usuarios** (puerto 5000) - OBLIGATORIO primero
2. **Otros microservicios** (puertos 5001, 5002, 5003)
3. **Gateway** (puerto 8080)
4. **Frontend Angular** (puerto 4200)

## 🔄 Ejemplo de Uso

### 1. Autenticación
```bash
curl -X POST http://localhost:8080/api/usuarios/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "123456"}'
```

### 2. Usar endpoints protegidos
```bash
curl -X GET http://localhost:8080/api/usuarios/findAll \
  -H "Authorization: Bearer <tu-jwt-token>"
```

## 🛡️ Características de Seguridad

- ✅ **Validación JWT** en cada petición
- ✅ **Headers de usuario** agregados automáticamente
- ✅ **CORS configurado** para Angular
- ✅ **Rutas públicas** para autenticación
- ✅ **Logging detallado** para debugging

## 🐛 Debugging

Logs importantes:
```bash
# Ver peticiones del gateway
logging.level.org.springframework.cloud.gateway=DEBUG

# Ver validación JWT
logging.level.co.edu.unicauca.gateway=DEBUG
```

## 📝 Notas Importantes

- El gateway **NO almacena estado** - todos los datos vienen del JWT
- **Actualizar puertos** en `GatewayConfig.java` si cambias puertos de microservicios
- **CORS** configurado para `http://localhost:4200` (Angular)
- **Timeout** configurado para 5 segundos 