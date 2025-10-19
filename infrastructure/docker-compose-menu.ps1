#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Smart Task AI - Docker Compose Helper (PowerShell)
.DESCRIPTION
    Menu interativo para gerenciar docker-compose-unified.yml
.EXAMPLE
    .\docker-compose-menu.ps1
#>

Set-StrictMode -Version Latest
$ErrorActionPreference = "Continue"

function Show-Menu {
    Clear-Host
    Write-Host ""
    Write-Host "===================================================" -ForegroundColor Cyan
    Write-Host "  Smart Task AI - Docker Compose Menu" -ForegroundColor Cyan
    Write-Host "===================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Opcoes:" -ForegroundColor Yellow
    Write-Host "  1 - Iniciar modo COMPLETO (Backend + Frontend + BD + Observabilidade)" -ForegroundColor White
    Write-Host "  2 - Iniciar modo OBSERVABILIDADE (Jaeger + Prometheus + Grafana)" -ForegroundColor White
    Write-Host "  3 - Ver status dos containers" -ForegroundColor White
    Write-Host "  4 - Ver logs em tempo real" -ForegroundColor White
    Write-Host "  5 - Parar todos os containers" -ForegroundColor White
    Write-Host "  6 - Remover tudo (containers + volumes)" -ForegroundColor White
    Write-Host "  7 - Acessar URLs importantes" -ForegroundColor White
    Write-Host "  0 - Sair" -ForegroundColor White
    Write-Host ""
    $choice = Read-Host "Escolha uma opcao [0-7]"
    return $choice
}

function Start-FullMode {
    Clear-Host
    Write-Host ""
    Write-Host "[*] Iniciando modo COMPLETO..." -ForegroundColor Yellow
    Write-Host "[*] Esto incluir: Backend, Frontend, PostgreSQL, Jaeger, Prometheus, Grafana" -ForegroundColor Yellow
    Write-Host ""
    
    & docker-compose -f docker-compose-unified.yml up -d
    
    Start-Sleep -Seconds 5
    Clear-Host
    Write-Host ""
    Write-Host "[+] Containers iniciados com sucesso!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Acesse:" -ForegroundColor Cyan
    Write-Host "  Frontend:     http://localhost:3000" -ForegroundColor White
    Write-Host "  Backend API:  http://localhost:8080/api/swagger-ui.html" -ForegroundColor White
    Write-Host "  Grafana:      http://localhost:3001 (admin/admin)" -ForegroundColor White
    Write-Host "  Jaeger:       http://localhost:16686" -ForegroundColor White
    Write-Host "  Prometheus:   http://localhost:9090" -ForegroundColor White
    Write-Host ""
    Read-Host "Pressione Enter para voltar ao menu"
}

function Start-ObservabilityMode {
    Clear-Host
    Write-Host ""
    Write-Host "[*] Iniciando modo OBSERVABILIDADE..." -ForegroundColor Yellow
    Write-Host "[*] Esto incluir: Jaeger, Prometheus, Grafana (sem Backend nem Frontend)" -ForegroundColor Yellow
    Write-Host "[*] Use para rodar Backend localmente no IDE" -ForegroundColor Yellow
    Write-Host ""
    
    & docker-compose -f docker-compose-unified.yml --profile observability up -d
    
    Start-Sleep -Seconds 5
    Clear-Host
    Write-Host ""
    Write-Host "[+] Servicos de observabilidade iniciados!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Acesse:" -ForegroundColor Cyan
    Write-Host "  Grafana:      http://localhost:3001 (admin/admin)" -ForegroundColor White
    Write-Host "  Jaeger:       http://localhost:16686" -ForegroundColor White
    Write-Host "  Prometheus:   http://localhost:9090" -ForegroundColor White
    Write-Host ""
    Write-Host "[!] Para rodar Backend localmente:" -ForegroundColor Yellow
    Write-Host "    cd backend" -ForegroundColor White
    Write-Host "    mvn spring-boot:run" -ForegroundColor White
    Write-Host ""
    Read-Host "Pressione Enter para voltar ao menu"
}

function Show-Status {
    Clear-Host
    Write-Host ""
    Write-Host "[*] Status dos containers:" -ForegroundColor Yellow
    Write-Host ""
    
    & docker-compose -f docker-compose-unified.yml ps
    
    Write-Host ""
    Read-Host "Pressione Enter para voltar ao menu"
}

function Show-Logs {
    Clear-Host
    Write-Host ""
    $service = Read-Host "Qual servico deseja ver logs? (backend/frontend/postgres/all)"
    Write-Host ""
    Write-Host "[*] Ver logs (Press Ctrl+C para sair)" -ForegroundColor Yellow
    Write-Host ""
    
    if ($service -eq "all") {
        & docker-compose -f docker-compose-unified.yml logs -f
    } else {
        & docker-compose -f docker-compose-unified.yml logs -f $service
    }
}

function Stop-Containers {
    Clear-Host
    Write-Host ""
    Write-Host "[*] Parando todos os containers..." -ForegroundColor Yellow
    Write-Host ""
    
    & docker-compose -f docker-compose-unified.yml down
    
    Write-Host ""
    Write-Host "[+] Containers parados com sucesso!" -ForegroundColor Green
    Write-Host ""
    Read-Host "Pressione Enter para voltar ao menu"
}

function Remove-Everything {
    Clear-Host
    Write-Host ""
    Write-Host "[WARNING] Isto vai remover TODOS os containers e volumes (dados do banco serao perdidos)" -ForegroundColor Red
    Write-Host ""
    $confirm = Read-Host "Tem certeza? (s/n)"
    
    if ($confirm -eq "s" -or $confirm -eq "S") {
        Write-Host ""
        Write-Host "[*] Removendo containers e volumes..." -ForegroundColor Yellow
        
        & docker-compose -f docker-compose-unified.yml down -v
        
        Write-Host ""
        Write-Host "[+] Removido com sucesso!" -ForegroundColor Green
    } else {
        Write-Host "[!] Cancelado" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Read-Host "Pressione Enter para voltar ao menu"
}

function Show-URLs {
    Clear-Host
    Write-Host ""
    Write-Host "===================================================" -ForegroundColor Cyan
    Write-Host "  URLs Importantes" -ForegroundColor Cyan
    Write-Host "===================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  Frontend:              http://localhost:3000" -ForegroundColor Green
    Write-Host "  Backend API Swagger:   http://localhost:8080/api/swagger-ui.html" -ForegroundColor Green
    Write-Host "  Backend Health:        http://localhost:8080/actuator/health" -ForegroundColor Green
    Write-Host ""
    Write-Host "  --- Observabilidade ---" -ForegroundColor Yellow
    Write-Host "  Grafana:               http://localhost:3001 (admin/admin)" -ForegroundColor White
    Write-Host "  Jaeger UI:             http://localhost:16686" -ForegroundColor White
    Write-Host "  Prometheus:            http://localhost:9090" -ForegroundColor White
    Write-Host ""
    Write-Host "  --- Database ---" -ForegroundColor Yellow
    Write-Host "  PostgreSQL:            localhost:5432" -ForegroundColor White
    Write-Host "  Database:              smarttask" -ForegroundColor White
    Write-Host "  User:                  postgres" -ForegroundColor White
    Write-Host "  Password:              postgres" -ForegroundColor White
    Write-Host ""
    Read-Host "Pressione Enter para voltar ao menu"
}

# Main loop
Set-Location (Split-Path -Parent $MyInvocation.MyCommand.Path)

do {
    $choice = Show-Menu
    
    switch ($choice) {
        "1" { Start-FullMode }
        "2" { Start-ObservabilityMode }
        "3" { Show-Status }
        "4" { Show-Logs }
        "5" { Stop-Containers }
        "6" { Remove-Everything }
        "7" { Show-URLs }
        "0" { exit }
        default { Write-Host "Opcao invalida" -ForegroundColor Red; Start-Sleep -Seconds 2 }
    }
} while ($true)
