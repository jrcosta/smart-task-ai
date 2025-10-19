# Script para gerar JavaDoc sem Maven instalado
# Usa javadoc.exe do JDK diretamente
# Funciona com Java 25+

Write-Host "========================================" -ForegroundColor Green
Write-Host "JavaDoc Generator - Versão Sem Maven" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Encontrar Java JDK
Write-Host "[INFO] Procurando Java JDK..." -ForegroundColor Cyan

$javadocPaths = @(
    "C:\Program Files\Java\jdk-25\bin\javadoc.exe",
    "C:\Program Files\Java\jdk-21\bin\javadoc.exe",
    "C:\Program Files\Java\jdk-17\bin\javadoc.exe",
    "$env:JAVA_HOME\bin\javadoc.exe"
)

$javadocPath = $null
foreach ($path in $javadocPaths) {
    if (Test-Path $path) {
        $javadocPath = $path
        Write-Host "[SUCESSO] javadoc encontrado: $path" -ForegroundColor Green
        break
    }
}

if (-not $javadocPath) {
    Write-Host "[ERRO] javadoc.exe não encontrado!" -ForegroundColor Red
    Write-Host "Procurado em:" -ForegroundColor Yellow
    $javadocPaths | ForEach-Object { Write-Host "  - $_" }
    Write-Host ""
    Write-Host "Soluções:" -ForegroundColor Yellow
    Write-Host "1. Instale Java JDK (não apenas JRE)" -ForegroundColor Yellow
    Write-Host "2. Configure variável JAVA_HOME" -ForegroundColor Yellow
    Write-Host "3. Use Docker: docker run -v `$(pwd):/workspace maven:3.9-eclipse-temurin-25" -ForegroundColor Yellow
    exit 1
}

# Navegar para backend
$backendPath = Join-Path $PSScriptRoot "backend"
if (-not (Test-Path $backendPath)) {
    Write-Host "[ERRO] Pasta backend não encontrada: $backendPath" -ForegroundColor Red
    exit 1
}

Set-Location $backendPath
Write-Host "[INFO] Navegado para: $backendPath" -ForegroundColor Cyan
Write-Host ""

# Criar pasta de saída
$docsPath = "docs"
if (-not (Test-Path $docsPath)) {
    New-Item -ItemType Directory -Path $docsPath -Force | Out-Null
    Write-Host "[INFO] Pasta $docsPath criada" -ForegroundColor Cyan
}

# Executar javadoc
Write-Host "[INFO] Gerando documentação JavaDoc..." -ForegroundColor Cyan
Write-Host "[INFO] Fonte: src/main/java" -ForegroundColor Cyan
Write-Host "[INFO] Destino: $docsPath" -ForegroundColor Cyan
Write-Host ""

& $javadocPath `
    -d $docsPath `
    -sourcepath "src/main/java" `
    -subpackages "com.smarttask" `
    -author `
    -version `
    -use `
    -Xdoclint:none `
    -windowtitle "Smart Task Manager API Documentation" `
    -doctitle "Smart Task Manager - JavaDoc 1.0.0" `
    -header "<b>Smart Task Manager</b><br>Gerenciador de Tarefas com IA"

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "[SUCESSO] Documentação gerada!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Localização: $docsPath\index.html" -ForegroundColor Green
    Write-Host ""
    Write-Host "Abrindo documentação..." -ForegroundColor Cyan
    Write-Host ""
    
    $indexPath = Join-Path $docsPath "index.html"
    if (Test-Path $indexPath) {
        Start-Process $indexPath
        Write-Host "[SUCESSO] Navegador aberto!" -ForegroundColor Green
    } else {
        Write-Host "[AVISO] index.html não encontrado" -ForegroundColor Yellow
        Write-Host "Procure em: $(Get-Location)\$docsPath" -ForegroundColor Yellow
    }
} else {
    Write-Host ""
    Write-Host "[ERRO] Falha ao gerar JavaDoc" -ForegroundColor Red
    Write-Host "Código de erro: $LASTEXITCODE" -ForegroundColor Red
    exit 1
}
