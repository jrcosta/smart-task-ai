# Script para gerar documentacao JavaDoc do Smart Task Manager
# Requer: Maven 3.8+ instalado
# Uso: .\generate-javadoc.ps1

Write-Host "========================================" -ForegroundColor Green
Write-Host "Smart Task Manager - JavaDoc Generator" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Verificar se Maven esta instalado
$mvnPath = Get-Command mvn -ErrorAction SilentlyContinue

if (-not $mvnPath) {
    Write-Host "[ERRO] Maven nao encontrado no PATH" -ForegroundColor Red
    Write-Host "Por favor, instale Maven 3.8+ e adicione ao PATH" -ForegroundColor Yellow
    Write-Host "Opcoes:" -ForegroundColor Yellow
    Write-Host "  1. Instalar via Chocolatey: choco install maven" -ForegroundColor Yellow
    Write-Host "  2. Usar Docker:" -ForegroundColor Yellow
    Write-Host "     docker run -v `$(pwd):/workspace -w /workspace/backend maven:3.9-eclipse-temurin-25" -ForegroundColor Yellow
    exit 1
}

Write-Host "[INFO] Maven encontrado: $($mvnPath.Source)" -ForegroundColor Green
Write-Host "[INFO] Iniciando geracao..." -ForegroundColor Green
Write-Host ""

# Navegar para pasta backend
$backendPath = Join-Path $PSScriptRoot "backend"
if (-not (Test-Path $backendPath)) {
    Write-Host "[ERRO] Nao foi possivel encontrar a pasta backend" -ForegroundColor Red
    exit 1
}

Set-Location $backendPath

Write-Host "[INFO] Removendo builds anteriores..." -ForegroundColor Cyan
mvn clean -q

Write-Host "[INFO] Gerando documentacao JavaDoc..." -ForegroundColor Cyan
Write-Host ""

mvn javadoc:aggregate -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "[SUCESSO] Documentacao gerada com sucesso!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Documentacao localizada em:" -ForegroundColor Green
    Write-Host "  target/site/apidocs/index.html" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Abrindo documentacao no navegador..." -ForegroundColor Green
    Write-Host ""
    
    $docPath = Join-Path $backendPath "target\site\apidocs\index.html"
    if (Test-Path $docPath) {
        Start-Process $docPath
    } else {
        Write-Host "[AVISO] Arquivo index.html nao encontrado" -ForegroundColor Yellow
        Write-Host "Verifique o caminho: $docPath" -ForegroundColor Yellow
    }
} else {
    Write-Host ""
    Write-Host "[ERRO] Falha ao gerar documentacao JavaDoc" -ForegroundColor Red
    exit 1
}
