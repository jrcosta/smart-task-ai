@echo off
REM Script para gerar documentacao JavaDoc do Smart Task Manager
REM Requer: Maven 3.8+ instalado
REM Uso: generate-javadoc.bat

echo ========================================
echo Smart Task Manager - JavaDoc Generator
echo ========================================
echo.

REM Verificar se Maven esta instalado
where /q mvn
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Maven nao encontrado no PATH
    echo Por favor, instale Maven 3.8+ e adicione ao PATH
    echo Ou execute: choco install maven (se usar Chocolatey)
    echo Ou use Docker: docker run -v %%cd%%:/workspace -w /workspace/backend maven:3.9-eclipse-temurin-25
    exit /b 1
)

echo [INFO] Maven encontrado. Iniciando geracao...
echo.

REM Navegar para pasta backend
cd /d %~dp0backend
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Nao foi possivel acessar a pasta backend
    exit /b 1
)

echo [INFO] Removendo builds anteriores...
call mvn clean -q

echo [INFO] Gerando documentacao JavaDoc...
call mvn javadoc:aggregate -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo [SUCESSO] Documentacao gerada com sucesso!
    echo ========================================
    echo.
    echo Documentacao localizada em:
    echo   target/site/apidocs/index.html
    echo.
    echo Abrindo documentacao no navegador...
    start target\site\apidocs\index.html
) else (
    echo.
    echo [ERRO] Falha ao gerar documentacao JavaDoc
    exit /b 1
)
