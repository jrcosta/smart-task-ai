#!/bin/bash

echo "🚀 Iniciando Frontend do Smart Task Manager..."
echo ""

# Verificar versão do Node
echo "📦 Verificando Node.js..."
node -v
npm -v
echo ""

# Ir para o diretório do frontend
cd "$(dirname "$0")/frontend"

# Verificar se node_modules existe
if [ ! -d "node_modules" ]; then
    echo "📥 Instalando dependências..."
    npm install
    echo ""
fi

# Iniciar o servidor de desenvolvimento
echo "✨ Iniciando servidor de desenvolvimento..."
echo "Frontend estará disponível em: http://localhost:3000"
echo ""
npm run dev
