# 🎯 Como Fazer a Wiki Aparecer na Aba Wiki do GitHub - Passo a Passo

Este é um guia completo para fazer a wiki do repositório aparecer na aba "Wiki" do GitHub.

## 📝 Resumo Executivo

A wiki do projeto já está criada na pasta `wiki/` com 10 páginas completas. Para fazê-la aparecer na aba Wiki do GitHub, você precisa:

1. ✅ **Habilitar a Wiki no repositório** (configuração do GitHub)
2. ✅ **Fazer merge deste PR** (para ter o workflow de sincronização)
3. ✅ **Executar a sincronização** (manual ou automática)

---

## 🚀 Passo 1: Habilitar a Wiki no GitHub

A aba Wiki precisa ser habilitada nas configurações do repositório:

### Instruções Detalhadas:

1. **Acesse as configurações do repositório**:
   - Vá para: https://github.com/jrcosta/smart-task-ai/settings
   - Ou clique em "Settings" na página do repositório

2. **Navegue até a seção Features**:
   - Na barra lateral esquerda, procure a seção "General"
   - Role a página até encontrar a seção "Features"

3. **Habilite a Wiki**:
   - Marque a checkbox "Wikis"
   - A mudança é salva automaticamente

4. **Verifique se funcionou**:
   - Recarregue a página do repositório
   - Você deve ver uma nova aba "Wiki" no menu superior
   - Clicando nela, você verá uma página vazia pedindo para criar a primeira página

**✅ Pronto!** A aba Wiki agora está visível.

---

## 📦 Passo 2: Fazer Merge deste Pull Request

Este PR adiciona os seguintes recursos:

### Arquivos Criados:

1. **`.github/workflows/sync-wiki.yml`**
   - GitHub Actions workflow que sincroniza automaticamente
   - Dispara quando há mudanças em `wiki/**`
   - Usa o token GITHUB_TOKEN automaticamente

2. **`scripts/sync-wiki.sh`**
   - Script bash para sincronização manual
   - Útil para desenvolvimento local
   - Não requer configuração adicional

3. **`docs/WIKI_SYNC.md`**
   - Documentação completa sobre sincronização
   - Inclui troubleshooting
   - 3 métodos diferentes explicados

4. **`README.md` (atualizado)**
   - Links para a Wiki do GitHub
   - Referências aos novos recursos

### Como Fazer o Merge:

1. **Revise o Pull Request**:
   - Verifique os arquivos modificados
   - Confira que tudo está correto

2. **Aprove e faça merge**:
   - Clique em "Merge pull request"
   - Escolha "Squash and merge" ou "Create a merge commit"
   - Confirme o merge

3. **Aguarde o merge**:
   - O código será integrado ao branch principal

**✅ Pronto!** Os recursos de sincronização agora estão disponíveis.

---

## 🔄 Passo 3: Sincronizar a Wiki

Agora você precisa fazer a sincronização inicial. Escolha um dos métodos abaixo:

### Método A: GitHub Actions (Mais Fácil - Recomendado)

Após fazer o merge do PR:

1. **O workflow será executado automaticamente** porque:
   - O merge modifica arquivos em `wiki/**` (através do README que aponta para eles)
   - O workflow tem trigger em push para o branch principal

2. **Ou execute manualmente**:
   - Vá para: https://github.com/jrcosta/smart-task-ai/actions/workflows/sync-wiki.yml
   - Clique em "Run workflow"
   - Selecione o branch `main` ou `master`
   - Clique em "Run workflow" novamente
   - Aguarde a execução (1-2 minutos)

3. **Verifique o resultado**:
   - Vá para: https://github.com/jrcosta/smart-task-ai/wiki
   - Você deve ver a página Home e todas as outras páginas

**✅ Pronto!** A wiki está sincronizada e visível.

### Método B: Script Local (Avançado)

Se preferir executar localmente:

```bash
# Clone o repositório (se ainda não tiver)
git clone https://github.com/jrcosta/smart-task-ai.git
cd smart-task-ai

# Certifique-se de estar no branch com as mudanças
git checkout main  # ou master
git pull

# Execute o script
./scripts/sync-wiki.sh
```

**Requisitos**:
- Git configurado com credenciais do GitHub
- Permissão de escrita no repositório
- Wiki habilitada (Passo 1)

### Método C: Manual Completo (Para Entender o Processo)

Se quiser fazer completamente manual:

```bash
# 1. Clone o repositório wiki (separado do repo principal)
git clone https://github.com/jrcosta/smart-task-ai.wiki.git wiki-repo
cd wiki-repo

# 2. Copie os arquivos da pasta wiki/ do repositório principal
cp /caminho/para/smart-task-ai/wiki/* .

# 3. Adicione, commit e push
git add .
git commit -m "Adicionar conteúdo inicial da wiki"
git push origin master

# 4. Acesse a wiki
# https://github.com/jrcosta/smart-task-ai/wiki
```

---

## 🎉 Passo 4: Verificar o Resultado

Após a sincronização:

1. **Acesse a wiki**:
   - URL: https://github.com/jrcosta/smart-task-ai/wiki

2. **Verifique as páginas**:
   - ✅ Home - Página inicial
   - ✅ Getting-Started - Guia de início
   - ✅ Features - Funcionalidades
   - ✅ Usage-Guide - Guia de uso
   - ✅ Architecture - Arquitetura
   - ✅ FAQ - Perguntas frequentes
   - ✅ Troubleshooting - Resolução de problemas
   - ✅ Roadmap - Roadmap futuro
   - ✅ README - Índice da wiki
   - ✅ Visual-Index - Índice visual

3. **Teste a navegação**:
   - Clique nos links entre páginas
   - Verifique se as imagens (badges) aparecem
   - Confirme que a formatação está correta

---

## 🔧 Troubleshooting

### Problema: Aba Wiki não aparece

**Solução**:
1. Verifique se habilitou em Settings → Features → Wikis
2. Aguarde alguns segundos e recarregue a página
3. Limpe o cache do navegador (Ctrl+Shift+R)

### Problema: Wiki está vazia após sincronização

**Solução**:
1. Verifique os logs do GitHub Actions:
   - Vá para Actions → Sync Wiki
   - Veja se há erros na execução
2. Tente executar o workflow manualmente novamente
3. Se persistir, use o Método C (manual) para debug

### Problema: Erro de permissão ao executar script

**Solução**:
```bash
# Dê permissão de execução ao script
chmod +x scripts/sync-wiki.sh

# Configure suas credenciais Git
git config --global user.name "Seu Nome"
git config --global user.email "seu@email.com"

# Ou use GitHub CLI
gh auth login
```

### Problema: GitHub Actions falha com erro de autenticação

**Solução**:
1. Verifique se a wiki está habilitada (Passo 1)
2. O GITHUB_TOKEN deve ter permissões automaticamente
3. Se necessário, vá em Settings → Actions → General
4. Em "Workflow permissions", selecione "Read and write permissions"

---

## 📊 Estrutura Criada na Wiki

Após a sincronização, a wiki terá esta estrutura:

```
GitHub Wiki (https://github.com/jrcosta/smart-task-ai/wiki)
├── Home                    # Página inicial com navegação
├── Getting-Started         # Setup rápido em 5 minutos
├── Features                # Todas as funcionalidades
├── Usage-Guide             # Guia completo de uso
├── Architecture            # Arquitetura técnica do sistema
├── FAQ                     # 50+ perguntas respondidas
├── Troubleshooting         # Resolução de problemas
├── Roadmap                 # Planejamento até 2026
├── README                  # Índice da wiki
└── Visual-Index            # Índice visual com estatísticas
```

---

## 🔄 Manutenção Futura

### Atualizando a Wiki:

1. **Edite os arquivos em `wiki/`** no repositório principal
2. **Faça commit e push** para o branch principal
3. **A GitHub Action sincroniza automaticamente**
4. **Aguarde 1-2 minutos** para ver as mudanças na wiki

### Desabilitando Sincronização Automática:

Se quiser desabilitar temporariamente:

```bash
# Renomeie o arquivo do workflow
mv .github/workflows/sync-wiki.yml .github/workflows/sync-wiki.yml.disabled
```

### Habilitando Novamente:

```bash
# Renomeie de volta
mv .github/workflows/sync-wiki.yml.disabled .github/workflows/sync-wiki.yml
```

---

## 📚 Recursos Adicionais

- **Documentação Detalhada**: [docs/WIKI_SYNC.md](docs/WIKI_SYNC.md)
- **GitHub Wiki Docs**: https://docs.github.com/en/communities/documenting-your-project-with-wikis
- **GitHub Actions Docs**: https://docs.github.com/en/actions

---

## ✅ Checklist Final

Marque conforme completa:

- [ ] **Passo 1**: Wiki habilitada no GitHub Settings
- [ ] **Passo 2**: Pull Request merged
- [ ] **Passo 3**: Sincronização executada (manual ou automática)
- [ ] **Passo 4**: Wiki verificada e funcionando
- [ ] **Bonus**: Testou editar um arquivo em `wiki/` e viu sincronizar automaticamente

---

## 🎊 Conclusão

Após seguir estes passos, sua wiki estará:

✅ **Visível** na aba Wiki do GitHub  
✅ **Completa** com 10 páginas de documentação  
✅ **Sincronizada** automaticamente via GitHub Actions  
✅ **Mantida** facilmente editando arquivos em `wiki/`  

**Próximos passos sugeridos**:
1. Adicione screenshots nas páginas da wiki
2. Personalize o conteúdo conforme necessário
3. Compartilhe o link da wiki com sua equipe
4. Continue expandindo a documentação

---

**Data**: Outubro 2025  
**Versão**: 1.0.0  
**Status**: ✅ Pronto para uso
