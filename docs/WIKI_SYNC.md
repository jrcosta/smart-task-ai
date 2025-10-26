# 📚 Guia de Sincronização da Wiki

Este guia explica como fazer a wiki aparecer na aba Wiki do GitHub.

## 🎯 Objetivo

Transferir o conteúdo da pasta `wiki/` deste repositório para o GitHub Wiki oficial, que aparece na aba "Wiki" do GitHub.

## 📋 Pré-requisitos

1. **Habilitar a Wiki no GitHub**:
   - Acesse: https://github.com/jrcosta/smart-task-ai/settings
   - Na seção "Features", marque a opção "Wikis"
   - Clique em "Save changes"

2. **Ter permissões de escrita** no repositório

## 🚀 Método 1: Sincronização Automática via GitHub Actions (Recomendado)

A sincronização automática já está configurada! Sempre que houver mudanças na pasta `wiki/` e forem enviadas para o branch `main` ou `master`, a GitHub Action irá:

1. Detectar as mudanças
2. Clonar o repositório wiki
3. Copiar os arquivos atualizados
4. Fazer commit e push automaticamente

**Como funciona**:
- Workflow: `.github/workflows/sync-wiki.yml`
- Trigger: Push em `wiki/**` no branch principal
- Permissões: Usa `GITHUB_TOKEN` automaticamente

**Para executar manualmente**:
1. Vá para: https://github.com/jrcosta/smart-task-ai/actions/workflows/sync-wiki.yml
2. Clique em "Run workflow"
3. Selecione o branch e confirme

## 🛠️ Método 2: Sincronização Manual via Script

Use o script `sync-wiki.sh` para sincronizar localmente:

```bash
# Executar o script de sincronização
./scripts/sync-wiki.sh
```

**O script irá**:
1. Clonar o repositório wiki em `/tmp`
2. Copiar os arquivos de `wiki/` para lá
3. Fazer commit das mudanças
4. Enviar para o GitHub Wiki

**Requisitos**:
- Git configurado com credenciais
- Acesso de escrita ao repositório

## 🔧 Método 3: Sincronização Manual Completa

Se preferir fazer manualmente:

```bash
# 1. Clonar o repositório wiki
git clone https://github.com/jrcosta/smart-task-ai.wiki.git /tmp/wiki-repo

# 2. Copiar arquivos da wiki
cd /tmp/wiki-repo
cp -r /caminho/para/smart-task-ai/wiki/* .

# 3. Commit e push
git add .
git commit -m "Adicionar/atualizar conteúdo da wiki"
git push origin master
```

## 📁 Estrutura de Arquivos da Wiki

Os seguintes arquivos serão sincronizados:

```
wiki/
├── Home.md                  # Página inicial da wiki
├── Getting-Started.md       # Guia de início rápido
├── Features.md              # Funcionalidades
├── Usage-Guide.md           # Guia de uso completo
├── Architecture.md          # Arquitetura do sistema
├── Roadmap.md              # Roadmap de features
├── FAQ.md                  # Perguntas frequentes
├── Troubleshooting.md      # Resolução de problemas
├── README.md               # Índice da wiki
└── Visual-Index.md         # Índice visual
```

## ✅ Verificação

Após a sincronização, verifique se a wiki está visível:

1. Acesse: https://github.com/jrcosta/smart-task-ai/wiki
2. Confirme que a página "Home" está visível
3. Navegue pelas diferentes páginas para garantir que o conteúdo está correto

## 🔄 Fluxo de Trabalho Recomendado

1. **Editar arquivos** na pasta `wiki/` do repositório principal
2. **Commit e push** para o branch `main` ou `master`
3. **GitHub Actions** detecta as mudanças e sincroniza automaticamente
4. **Verificar** a wiki no GitHub após alguns minutos

## 🐛 Solução de Problemas

### Wiki não aparece no GitHub

**Problema**: Aba Wiki não está disponível

**Solução**:
1. Vá para Settings → Features
2. Ative "Wikis"
3. Aguarde alguns segundos e recarregue a página

### Erro ao fazer push no repositório wiki

**Problema**: `Permission denied` ou erro de autenticação

**Solução**:
1. Verifique suas permissões no repositório
2. Configure credenciais Git: `git config --global credential.helper store`
3. Ou use GitHub CLI: `gh auth login`

### GitHub Action falha ao sincronizar

**Problema**: Workflow `sync-wiki.yml` falha

**Solução**:
1. Verifique os logs em Actions
2. Confirme que a wiki está habilitada
3. Verifique se o `GITHUB_TOKEN` tem permissões corretas (deveria ter por padrão)

### Arquivos não aparecem na wiki

**Problema**: Arquivos copiados mas não visíveis na wiki

**Solução**:
1. Verifique se os arquivos têm extensão `.md`
2. Confirme que foram feitos commit e push
3. Aguarde alguns minutos (pode haver cache)
4. Limpe o cache do navegador

## 📚 Recursos Adicionais

- [Documentação oficial GitHub Wiki](https://docs.github.com/en/communities/documenting-your-project-with-wikis)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

## 💡 Dicas

- **Mantenha sincronizado**: Use a GitHub Action para manter a wiki sempre atualizada
- **Edite no repositório**: Faça mudanças em `wiki/` e deixe a Action sincronizar
- **Não edite direto na wiki**: Editar diretamente no GitHub Wiki pode causar conflitos
- **Use o script local**: Para testes rápidos, use `./scripts/sync-wiki.sh`

---

**Última atualização**: Outubro 2025  
**Versão**: 1.0.0
