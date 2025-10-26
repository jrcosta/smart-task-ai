#!/bin/bash
# Script para sincronizar a wiki local com o GitHub Wiki
# Este script copia os arquivos markdown da pasta wiki/ para o repositório wiki do GitHub

set -e

REPO_URL="https://github.com/jrcosta/smart-task-ai.wiki.git"
WIKI_DIR="wiki"
TEMP_DIR="/tmp/smart-task-ai-wiki-sync"

echo "🚀 Iniciando sincronização da wiki..."

# Verificar se a pasta wiki existe
if [ ! -d "$WIKI_DIR" ]; then
    echo "❌ Erro: Pasta wiki/ não encontrada"
    exit 1
fi

# Limpar diretório temporário se existir
if [ -d "$TEMP_DIR" ]; then
    echo "🧹 Limpando diretório temporário..."
    rm -rf "$TEMP_DIR"
fi

# Clonar o repositório wiki
echo "📥 Clonando repositório wiki..."
if ! git clone "$REPO_URL" "$TEMP_DIR" 2>/dev/null; then
    echo "⚠️  Repositório wiki não existe ou não tem permissão. Inicializando novo repositório..."
    mkdir -p "$TEMP_DIR"
    cd "$TEMP_DIR"
    git init
    git remote add origin "$REPO_URL"
else
    cd "$TEMP_DIR"
fi

# Copiar arquivos da wiki
echo "📋 Copiando arquivos da wiki..."
REPO_ROOT="$(git rev-parse --show-toplevel)"

# Verificar se há arquivos para copiar (mais eficiente)
if [ -z "$(find "$REPO_ROOT/$WIKI_DIR" -maxdepth 1 -type f -print -quit 2>/dev/null)" ]; then
    echo "❌ Erro: Pasta wiki/ está vazia ou não contém arquivos"
    cd - > /dev/null
    rm -rf "$TEMP_DIR"
    exit 1
fi

# Copiar todos os arquivos (incluindo ocultos)
cp -r "$REPO_ROOT/$WIKI_DIR"/. .

# Verificar se há mudanças
if [ -z "$(git status --porcelain)" ]; then
    echo "✅ Wiki já está sincronizada. Nenhuma mudança detectada."
    cd - > /dev/null
    rm -rf "$TEMP_DIR"
    exit 0
fi

# Adicionar e commitar mudanças
echo "💾 Commitando mudanças..."
git add .
git commit -m "Atualizar wiki do repositório (sincronizado em $(date '+%Y-%m-%d %H:%M:%S'))"

# Detectar branch padrão usando awk (mais robusto)
echo "📤 Detectando branch padrão..."
DEFAULT_BRANCH=$(git remote show origin 2>/dev/null | grep 'HEAD branch' | awk '{print $NF}')

# Validar se o branch detectado é válido
if [ -z "$DEFAULT_BRANCH" ] || ! git rev-parse --verify "origin/$DEFAULT_BRANCH" >/dev/null 2>&1; then
    DEFAULT_BRANCH="master"
fi

echo "📤 Enviando para branch: $DEFAULT_BRANCH"

# Push para o repositório wiki
if git push origin "$DEFAULT_BRANCH"; then
    echo "✅ Wiki sincronizada com sucesso!"
else
    echo "❌ Erro ao enviar para o repositório wiki."
    echo "💡 Certifique-se de que:"
    echo "   - Você tem permissões de escrita no repositório"
    echo "   - A aba Wiki está habilitada no GitHub"
    echo "   - Suas credenciais Git estão configuradas"
    cd - > /dev/null
    rm -rf "$TEMP_DIR"
    exit 1
fi

# Limpar
cd - > /dev/null
rm -rf "$TEMP_DIR"

echo "🎉 Sincronização concluída!"
echo "🌐 Acesse a wiki em: https://github.com/jrcosta/smart-task-ai/wiki"
