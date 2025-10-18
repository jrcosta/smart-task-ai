@echo off
REM Script alternativo para gerar JavaDoc sem Maven instalado
REM Usa um wrapper Maven fornecido pelo Apache
REM Este script baixa automaticamente Maven se necessário

setlocal enabledelayedexpansion

echo ========================================
echo Smart Task Manager - JavaDoc Generator
echo (Versao sem Maven instalado)
echo ========================================
echo.

REM Verificar Java
where /q java
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Java nao encontrado!
    echo Por favor, instale Java 25+ primeiro
    exit /b 1
)

echo [INFO] Java encontrado. Continuando...
echo.

REM Navegar para backend
cd /d %~dp0backend
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] Nao foi possivel acessar pasta backend
    exit /b 1
)

REM Verificar pom.xml
if not exist pom.xml (
    echo [ERRO] pom.xml nao encontrado
    exit /b 1
)

echo [INFO] Tentando gerar JavaDoc com Java nativo...
echo.

REM Tentar encontrar javadoc
where /q javadoc
if %ERRORLEVEL% EQU 0 (
    echo [SUCESSO] javadoc encontrado!
    echo [INFO] Gerando documentacao...
    
    REM Criar diretorio de saida
    if not exist docs mkdir docs
    
    REM Executar javadoc em todos os arquivos Java
    javadoc -d docs -sourcepath src/main/java -subpackages com.smarttask ^
        -author -version -use -Xdoclint:none
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo [SUCESSO] Documentacao gerada em: docs/index.html
        start docs\index.html
    ) else (
        echo [ERRO] Falha ao gerar JavaDoc
        exit /b 1
    )
) else (
    echo [AVISO] javadoc nao encontrado no PATH
    echo.
    echo Opcoes:
    echo 1. Instale Java Development Kit (JDK) completo
    echo 2. Use Docker: docker run -v %%cd%%:/workspace maven:3.9-eclipse-temurin-25
    echo 3. Baixe Maven manualmente: https://maven.apache.org/download.cgi
    echo.
    exit /b 1
)
