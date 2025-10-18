#!/bin/bash

# Script para validar a stack de observabilidade

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
    
    response=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null)
    
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

# Testar Backend
echo "📦 Backend API"
((total++))
test_endpoint "  Health Check" "http://localhost:8080/api/actuator/health" "200" && ((success++))
((total++))
test_endpoint "  Prometheus Metrics" "http://localhost:8080/api/actuator/prometheus" "200" && ((success++))
echo ""

# Testar Jaeger
echo "📊 Jaeger (Traces)"
((total++))
test_endpoint "  Jaeger UI" "http://localhost:16686/" "200" && ((success++))
((total++))
test_endpoint "  Jaeger API" "http://localhost:16686/api/services" "200" && ((success++))
echo ""

# Testar Prometheus
echo "📈 Prometheus (Metrics)"
((total++))
test_endpoint "  Prometheus UI" "http://localhost:9090/" "200" && ((success++))
((total++))
test_endpoint "  Prometheus API" "http://localhost:9090/api/v1/status/config" "200" && ((success++))
echo ""

# Testar Grafana
echo "📉 Grafana (Dashboard)"
((total++))
test_endpoint "  Grafana UI" "http://localhost:3001/" "302" && ((success++))
((total++))
test_endpoint "  Grafana API" "http://localhost:3001/api/health" "200" && ((success++))
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
    echo "Verifique os logs com:"
    echo "  docker-compose logs -f"
    echo ""
    echo "Ou reinicie os serviços:"
    echo "  docker-compose restart"
    exit 1
fi
