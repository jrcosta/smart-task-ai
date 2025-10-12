#!/bin/bash

echo "🚀 Iniciando Smart Task Manager Backend com variáveis de ambiente..."
echo ""

# Ir para o diretório do backend
cd "$(dirname "$0")/backend" || exit 1

# Carregar variáveis do .env
if [ -f .env ]; then
    echo "📋 Carregando variáveis do .env..."
    export $(cat .env | grep -v '^#' | xargs)
    echo "✅ Variáveis carregadas!"
    echo ""
else
    echo "⚠️  Arquivo .env não encontrado!"
fi

# Verificar se as variáveis foram carregadas
echo "🔍 Verificando configurações do Twilio:"
echo "   TWILIO_ACCOUNT_SID: ${TWILIO_ACCOUNT_SID:0:10}..."
echo "   TWILIO_AUTH_TOKEN: ${TWILIO_AUTH_TOKEN:0:10}..."
echo "   TWILIO_WHATSAPP_NUMBER: $TWILIO_WHATSAPP_NUMBER"
echo ""

# Matar processos anteriores
echo "📋 Parando processos anteriores..."
pkill -9 -f "spring-boot:run" 2>/dev/null || true
sleep 2

# Carregar SDKMAN
source ~/.sdkman/bin/sdkman-init.sh

# Iniciar o backend com as variáveis de ambiente
echo "🚀 Iniciando backend..."
echo ""

mvn spring-boot:run \
    -Dspring-boot.run.arguments="--twilio.account-sid=$TWILIO_ACCOUNT_SID --twilio.auth-token=$TWILIO_AUTH_TOKEN --twilio.whatsapp-number=$TWILIO_WHATSAPP_NUMBER"
