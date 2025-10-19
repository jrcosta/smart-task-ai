# Script para gerar JavaDoc diretamente com javadoc (sem Maven)
# Uso: .\generate-javadoc-direct.ps1

Write-Host "========================================" -ForegroundColor Green
Write-Host "Smart Task Manager - JavaDoc Generator" -ForegroundColor Green
Write-Host "Usando javadoc diretamente (sem Maven)" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Encontrar caminho do Java
$javaHome = (Get-Command java).Source | Split-Path -Parent
$javadoc = "$javaHome\javadoc.exe"

if (-not (Test-Path $javadoc)) {
    Write-Host "[ERRO] javadoc não encontrado em: $javadoc" -ForegroundColor Red
    exit 1
}

Write-Host "[INFO] Usando: $javadoc" -ForegroundColor Cyan

# Diretórios
$projectRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$srcDir = "$projectRoot\backend\src\main\java"
$outputDir = "$projectRoot\backend\target\site\apidocs"
$classpathDir = "$projectRoot\backend\target\classes"

Write-Host "[INFO] Diretório de código-fonte: $srcDir" -ForegroundColor Cyan
Write-Host "[INFO] Diretório de saída: $outputDir" -ForegroundColor Cyan

# Criar diretório de saída se não existir
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    Write-Host "[INFO] Diretório criado: $outputDir" -ForegroundColor Green
}

# Gerar JavaDoc
Write-Host ""
Write-Host "[INFO] Gerando documentação JavaDoc..." -ForegroundColor Cyan
Write-Host ""

$javaFiles = Get-ChildItem -Path $srcDir -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

if ($javaFiles.Count -eq 0) {
    Write-Host "[ERRO] Nenhum arquivo .java encontrado em $srcDir" -ForegroundColor Red
    exit 1
}

Write-Host "[INFO] Encontrados $($javaFiles.Count) arquivos Java" -ForegroundColor Green

& $javadoc `
    -sourcepath "$srcDir" `
    -d "$outputDir" `
    -author `
    -version `
    -use `
    -encoding UTF-8 `
    -charset UTF-8 `
    -docencoding UTF-8 `
    -windowtitle "Smart Task Manager API" `
    -doctitle "<h1>Smart Task Manager - Documentação JavaDoc</h1>" `
    -bottom "<b>Smart Task Manager v1.0.0 - Desenvolvido com Java 25 + Spring Boot 3.2</b>" `
    -failonerror `
    -private `
    -verbose `
    com.smarttask `
    com.smarttask.config `
    com.smarttask.controller `
    com.smarttask.dto `
    com.smarttask.exception `
    com.smarttask.model `
    com.smarttask.observability `
    com.smarttask.repository `
    com.smarttask.security `
    com.smarttask.service

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "[SUCESSO] Documentação gerada!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Documentação localizada em:" -ForegroundColor Green
    Write-Host "  $outputDir\index.html" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Abrindo documentação..." -ForegroundColor Green
    Start-Process "$outputDir\index.html"
} else {
    Write-Host ""
    Write-Host "[ERRO] Falha ao gerar documentação" -ForegroundColor Red
    exit 1
}
