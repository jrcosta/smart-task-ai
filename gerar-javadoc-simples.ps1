# Script PowerShell para gerar JavaDoc SIMPLES
# Adiciona Maven ao PATH e executa o comando

Write-Host "========================================" -ForegroundColor Green
Write-Host "Smart Task Manager - JavaDoc Generator" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Adicionar Maven ao PATH
$mavenBin = "C:\Users\Liliane Sebirino\.maven\apache-maven-3.9.11\bin"
if (Test-Path $mavenBin) {
    $env:PATH = "$mavenBin;$env:PATH"
    Write-Host "[OK] Maven adicionado ao PATH" -ForegroundColor Green
} else {
    Write-Host "[ERRO] Maven não encontrado" -ForegroundColor Red
    exit 1
}

# Verificar Maven
$mvnCheck = mvn -version 2>&1 | Select-Object -First 1
if ($mvnCheck -match "Apache Maven") {
    Write-Host "[OK] Maven 3.9.11 disponível" -ForegroundColor Green
} else {
    Write-Host "[ERRO] Maven não respondeu corretamente" -ForegroundColor Red
    exit 1
}

Write-Host ""

# Diretório do projeto
$projectRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$backendDir = "$projectRoot\backend"

Write-Host "[INFO] Projeto: $backendDir" -ForegroundColor Cyan
Write-Host ""
Write-Host "[INFO] Gerando JavaDoc..." -ForegroundColor Cyan
Write-Host ""

# Executar Maven
Set-Location $backendDir
mvn clean compile javadoc:aggregate -DskipTests

if ($LASTEXITCODE -eq 0) {
    $docPath = "$backendDir\target\site\apidocs\index.html"
    if (Test-Path $docPath) {
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "[SUCESSO] JavaDoc gerado!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Localização: $docPath" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "Abrindo no navegador..." -ForegroundColor Green
        Start-Process $docPath
    } else {
        Write-Host "[AVISO] index.html não encontrado em $docPath" -ForegroundColor Yellow
    }
} else {
    Write-Host ""
    Write-Host "[ERRO] Falha na geração" -ForegroundColor Red
    exit 1
}
