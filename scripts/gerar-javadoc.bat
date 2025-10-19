@echo off
REM Script simples para gerar JavaDoc
REM Uso: gerar-javadoc.bat

echo.
echo ========================================
echo Smart Task Manager - JavaDoc Generator
echo ========================================
echo.

REM Definir caminho do javadoc diretamente
set JAVADOC_PATH=C:\Program Files\Java\jdk-25\bin\javadoc.exe

if not exist "%JAVADOC_PATH%" (
    echo [ERRO] javadoc nao encontrado em: %JAVADOC_PATH%
    exit /b 1
)

echo [INFO] Usando: %JAVADOC_PATH%
echo.

REM Definir diretorios
set SRC_DIR=%~dp0backend\src\main\java
set OUT_DIR=%~dp0backend\target\site\apidocs
set PROJECT_NAME=Smart Task Manager

echo [INFO] Fonte: %SRC_DIR%
echo [INFO] Saida: %OUT_DIR%
echo.

REM Criar diretorio se nao existir
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

echo [INFO] Gerando JavaDoc...
echo.

REM Gerar JavaDoc com todas as classes do com.smarttask
"%JAVADOC_PATH%" ^
  -sourcepath "%SRC_DIR%" ^
  -d "%OUT_DIR%" ^
  -author ^
  -version ^
  -use ^
  -encoding UTF-8 ^
  -charset UTF-8 ^
  -windowtitle "%PROJECT_NAME% - JavaDoc" ^
  -doctitle "%PROJECT_NAME% - Documentacao" ^
  -footer "Desenvolvido com Java 25 e Spring Boot 3.2" ^
  -private ^
  com.smarttask ^
  com.smarttask.config ^
  com.smarttask.controller ^
  com.smarttask.dto ^
  com.smarttask.exception ^
  com.smarttask.model ^
  com.smarttask.observability ^
  com.smarttask.repository ^
  com.smarttask.security ^
  com.smarttask.service

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo [OK] JavaDoc gerado com sucesso!
    echo ========================================
    echo.
    echo Local: %OUT_DIR%\index.html
    echo.
    start "" "%OUT_DIR%\index.html"
) else (
    echo.
    echo [ERRO] Falha ao gerar JavaDoc
    exit /b 1
)
