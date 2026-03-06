@echo off
echo ====================================
echo    INICIANDO API GATEWAY
echo ====================================
echo.
echo Puerto: 8080
echo.
echo Compilando proyecto...
call mvn clean install -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: No se pudo compilar el proyecto
    pause
    exit /b 1
)

echo.
echo Iniciando Gateway...
echo.
echo IMPORTANTE: Asegurate de que los microservicios esten ejecutandose:
echo - usuarios (puerto 5000)
echo.
echo Gateway disponible en: http://localhost:8080
echo.
echo Presiona Ctrl+C para detener...
echo.

call mvn spring-boot:run 