#!/bin/bash

echo "🚀 Iniciando Smart Task Manager Backend..."
echo ""

# Cores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Ir para o diretório do backend
cd "$(dirname "$0")/backend" || exit 1

# 1. Matar processos anteriores
echo -e "${YELLOW}📋 Parando processos anteriores...${NC}"
pkill -9 -f "spring-boot:run" 2>/dev/null || true
sleep 2

# 2. Verificar se a porta está livre
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Porta 8080 em uso. Liberando...${NC}"
    lsof -ti:8080 | xargs kill -9 2>/dev/null || true
    sleep 2
fi

# 3. Carregar SDKMAN e verificar Java
echo -e "${YELLOW}☕ Verificando Java...${NC}"
source ~/.sdkman/bin/sdkman-init.sh

java -version
echo ""

# 4. Limpar e compilar
echo -e "${YELLOW}🔨 Compilando projeto...${NC}"
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Erro na compilação!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Compilação concluída!${NC}"
echo ""

# 5. Iniciar o backend
echo -e "${GREEN}🚀 Iniciando backend...${NC}"
echo -e "${YELLOW}Aguarde até ver: 'Started SmartTaskManagerApplication'${NC}"
echo ""

mvn spring-boot:run
