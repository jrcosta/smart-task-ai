#!/bin/bash

echo "🚀 Iniciando Backend do Smart Task Manager..."
echo ""

# Verificar versão do Java
echo "☕ Verificando Java..."
java -version
echo ""

# Ir para o diretório do backend
cd "$(dirname "$0")/backend"

# Compilar e executar
echo "🔨 Compilando e iniciando o backend..."
echo "Backend estará disponível em: http://localhost:8080"
echo ""
mvn spring-boot:run
