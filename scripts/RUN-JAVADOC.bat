@echo off
REM ============================================
REM Script SIMPLES para Gerar JavaDoc
REM Duplo-clique para executar
REM ============================================

setlocal enabledelayedexpansion

echo.
echo ========================================
echo SMART TASK MANAGER - JavaDoc Generator
echo ========================================
echo.

REM Adicionar Maven ao PATH
set MAVEN_HOME=C:\Users\Liliane Sebirino\.maven\apache-maven-3.9.11
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Verificar Maven
echo [1] Verificando Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Maven nao encontrado em: %MAVEN_HOME%
    pause
    exit /b 1
)
echo [OK] Maven 3.9.11 disponivel
echo.

REM Ir para backend
echo [2] Localizando diretorio backend...
cd /d "%~dp0..\backend" || (
    echo [ERRO] Diretorio backend nao encontrado
    pause
    exit /b 1
)
echo [OK] Backend encontrado em: %cd%
echo.

REM Limpar compilacoes anteriores
echo [3] Limpando compilacoes anteriores...
if exist target rmdir /s /q target >nul 2>&1
echo [OK] Limpeza concluida
echo.

REM Compilar com Maven
echo [4] Compilando projeto...
mvn clean compile -q
if errorlevel 1 (
    echo [ERRO] Falha na compilacao
    echo.
    pause
    exit /b 1
)
echo [OK] Compilacao concluida
echo.

REM Gerar JavaDoc
echo [5] Gerando JavaDoc...
mvn javadoc:aggregate -q -DskipTests

if errorlevel 1 (
    echo [ERRO] Falha na geracao de JavaDoc
    echo.
    pause
    exit /b 1
)
echo [OK] JavaDoc gerado
echo.

REM Verificar resultado
echo [6] Verificando resultado...
if exist "target\site\apidocs\index.html" (
    echo [OK] index.html encontrado
    echo.
    echo ========================================
    echo [SUCESSO] JavaDoc gerado com sucesso!
    echo ========================================
    echo.
    echo Abrindo no navegador...
    start target\site\apidocs\index.html
) else (
    echo [AVISO] index.html nao encontrado
    echo Verifique: target\site\apidocs\
    pause
)
