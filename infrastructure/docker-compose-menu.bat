@echo off
REM Smart Task AI - Docker Compose Helper Script
REM Facilita o uso do docker-compose-unified.yml com opções de menu

setlocal enabledelayedexpansion

cd /d "%~dp0"

:menu
cls
echo.
echo ===================================================
echo   Smart Task AI - Docker Compose Menu
echo ===================================================
echo.
echo Opcoes:
echo   1 - Iniciar modo COMPLETO (Backend + Frontend + BD + Observabilidade)
echo   2 - Iniciar modo OBSERVABILIDADE (Jaeger + Prometheus + Grafana)
echo   3 - Ver status dos containers
echo   4 - Ver logs em tempo real
echo   5 - Parar todos os containers
echo   6 - Remover tudo (containers + volumes)
echo   7 - Acessar URLs importantes
echo   0 - Sair
echo.
set /p choice="Escolha uma opcao [0-7]: "

if "%choice%"=="1" goto modo_completo
if "%choice%"=="2" goto modo_observabilidade
if "%choice%"=="3" goto status
if "%choice%"=="4" goto logs
if "%choice%"=="5" goto stop
if "%choice%"=="6" goto remove
if "%choice%"=="7" goto urls
if "%choice%"=="0" exit /b
goto menu

:modo_completo
cls
echo.
echo [*] Iniciando modo COMPLETO...
echo [*] Esto incluir: Backend, Frontend, PostgreSQL, Jaeger, Prometheus, Grafana
echo.
docker-compose -f docker-compose-unified.yml up -d
timeout /t 5
cls
echo.
echo [+] Containers iniciados com sucesso!
echo.
echo Acesse:
echo   Frontend:     http://localhost:3000
echo   Backend API:  http://localhost:8080/api/swagger-ui.html
echo   Grafana:      http://localhost:3001 (admin/admin)
echo   Jaeger:       http://localhost:16686
echo   Prometheus:   http://localhost:9090
echo.
pause
goto menu

:modo_observabilidade
cls
echo.
echo [*] Iniciando modo OBSERVABILIDADE...
echo [*] Esto incluir: Jaeger, Prometheus, Grafana (sem Backend nem Frontend)
echo [*] Use para rodar Backend localmente no IDE
echo.
docker-compose -f docker-compose-unified.yml --profile observability up -d
timeout /t 5
cls
echo.
echo [+] Servicos de observabilidade iniciados!
echo.
echo Acesse:
echo   Grafana:      http://localhost:3001 (admin/admin)
echo   Jaeger:       http://localhost:16686
echo   Prometheus:   http://localhost:9090
echo.
echo [!] Para rodar Backend localmente:
echo     cd backend
echo     mvn spring-boot:run
echo.
pause
goto menu

:status
cls
echo.
echo [*] Status dos containers:
echo.
docker-compose -f docker-compose-unified.yml ps
echo.
pause
goto menu

:logs
cls
echo.
echo [*] Ver logs (Press Ctrl+C para sair)
echo.
set /p service="Qual servico deseja ver logs? (backend/frontend/postgres/all): "
if "%service%"=="all" (
    docker-compose -f docker-compose-unified.yml logs -f
) else (
    docker-compose -f docker-compose-unified.yml logs -f %service%
)
goto menu

:stop
cls
echo.
echo [*] Parando todos os containers...
echo.
docker-compose -f docker-compose-unified.yml down
echo.
echo [+] Containers parados com sucesso!
echo.
pause
goto menu

:remove
cls
echo.
echo [WARNING] Isto vai remover TODOS os containers e volumes (dados do banco serao perdidos)
echo.
set /p confirm="Tem certeza? (s/n): "
if /i "%confirm%"=="s" (
    echo.
    echo [*] Removendo containers e volumes...
    docker-compose -f docker-compose-unified.yml down -v
    echo.
    echo [+] Removido com sucesso!
) else (
    echo [!] Cancelado
)
echo.
pause
goto menu

:urls
cls
echo.
echo ===================================================
echo   URLs Importantes
echo ===================================================
echo.
echo   Frontend:              http://localhost:3000
echo   Backend API Swagger:   http://localhost:8080/api/swagger-ui.html
echo   Backend Health:        http://localhost:8080/actuator/health
echo.
echo   --- Observabilidade ---
echo   Grafana:               http://localhost:3001 (admin/admin)
echo   Jaeger UI:             http://localhost:16686
echo   Prometheus:            http://localhost:9090
echo.
echo   --- Database ---
echo   PostgreSQL:            localhost:5432
echo   Database:              smarttask
echo   User:                  postgres
echo   Password:              postgres
echo.
pause
goto menu
