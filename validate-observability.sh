#!/bin/bash
# Script para validar a stack de observabilidade
# Não usa 'set -e' pois queremos capturar falhas individuais sem parar o script

echo "🔍 Validando Stack de Observabilidade do Smart Task Manager"
echo "=========================================================="
echo ""

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para testar endpoint
test_endpoint() {
    local name=$1
    local url=$2
    local expected=$3
    
    echo -n "Testando $name... "
    
    # Adicionar timeout para evitar travamento
    response=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 --connect-timeout 5 "$url" 2>/dev/null || echo "000")
    
    if [ "$response" == "$expected" ]; then
        echo -e "${GREEN}✓ OK${NC} (HTTP $response)"
        return 0
    else
        echo -e "${RED}✗ FALHOU${NC} (HTTP $response, esperado $expected)"
        return 1
    fi
}

# Contador de sucessos
success=0
total=0
failed_tests=()

# Testar Backend
echo "📦 Backend API"
((total++))
if test_endpoint "  Health Check" "http://localhost:8080/api/actuator/health" "200"; then
    ((success++))
else
    failed_tests+=("Backend Health Check")
fi
((total++))
if test_endpoint "  Prometheus Metrics" "http://localhost:8080/api/actuator/prometheus" "200"; then
    ((success++))
else
    failed_tests+=("Backend Prometheus Metrics")
fi
echo ""

# Testar Jaeger
echo "📊 Jaeger (Traces)"
((total++))
if test_endpoint "  Jaeger UI" "http://localhost:16686/" "200"; then
    ((success++))
else
    failed_tests+=("Jaeger UI")
fi
((total++))
if test_endpoint "  Jaeger API" "http://localhost:16686/api/services" "200"; then
    ((success++))
else
    failed_tests+=("Jaeger API")
fi
echo ""

# Testar Prometheus
echo "📈 Prometheus (Metrics)"
((total++))
if test_endpoint "  Prometheus UI" "http://localhost:9090/" "200"; then
    ((success++))
else
    failed_tests+=("Prometheus UI")
fi
((total++))
if test_endpoint "  Prometheus API" "http://localhost:9090/api/v1/status/config" "200"; then
    ((success++))
else
    failed_tests+=("Prometheus API")
fi
echo ""

# Testar Grafana
echo "📉 Grafana (Dashboard)"
((total++))
if test_endpoint "  Grafana UI" "http://localhost:3001/" "302"; then
    ((success++))
else
    failed_tests+=("Grafana UI")
fi
((total++))
if test_endpoint "  Grafana API" "http://localhost:3001/api/health" "200"; then
    ((success++))
else
    failed_tests+=("Grafana API")
fi
echo ""

# Resultado final
echo "=========================================================="
echo ""
if [ $success -eq $total ]; then
    echo -e "${GREEN}✅ Todos os serviços estão funcionando! ($success/$total)${NC}"
    echo ""
    echo "🎉 Stack de observabilidade pronta para uso!"
    echo ""
    echo "Acesse:"
    echo "  • Jaeger:     http://localhost:16686"
    echo "  • Prometheus: http://localhost:9090"
    echo "  • Grafana:    http://localhost:3001 (admin/admin)"
    echo "  • Backend:    http://localhost:8080/api"
    exit 0
else
    echo -e "${RED}❌ Alguns serviços falharam! ($success/$total)${NC}"
    echo ""
    if [ ${#failed_tests[@]} -gt 0 ]; then
        echo "Testes que falharam:"
        for test in "${failed_tests[@]}"; do
            echo "  - $test"
        done
        echo ""
    fi
    echo "Verifique os logs com:"
    echo "  docker-compose logs -f"
    echo ""
    echo "Ou reinicie os serviços:"
    echo "  docker-compose restart"
    echo ""
    echo "Ou inicie os serviços se ainda não estiverem rodando:"
    echo "  docker-compose up -d"
    exit 1
fi
