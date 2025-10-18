# 🚀 Quick Start - Gerando JavaDoc

## ⏱️ 2 Minutos de Setup

### Windows PowerShell (Recomendado)

```powershell
# 1. Navegue até a raiz do projeto
cd C:\Users\Seu-Usuario\smart-task-ai

# 2. Execute o script
.\generate-javadoc.ps1

# 3. Aguarde a geração (1-2 minutos)
# O navegador abrirá automaticamente com a documentação

# ✅ Pronto!
```

### Windows Batch

```bash
# 1. Abra o prompt de comando
# 2. Navegue até a raiz do projeto
cd C:\Users\Seu-Usuario\smart-task-ai

# 3. Execute o script
generate-javadoc.bat

# 4. Aguarde a geração
# O navegador abrirá automaticamente

# ✅ Pronto!
```

### Linux / macOS

```bash
# 1. Navegue até a raiz do projeto
cd ~/smart-task-ai

# 2. Acesse a pasta backend
cd backend

# 3. Gere a documentação
mvn clean javadoc:aggregate

# 4. Abra no navegador
open target/site/apidocs/index.html    # macOS
xdg-open target/site/apidocs/index.html # Linux

# ✅ Pronto!
```

### Docker

```bash
# 1. Navegue até a raiz do projeto
cd ~/smart-task-ai

# 2. Build da imagem
docker build -f docs/javadoc/Dockerfile -t smart-task-javadoc .

# 3. Execute o container
docker run -v $(pwd)/docs/javadoc:/app/docs smart-task-javadoc

# 4. Abra a documentação gerada
open docs/javadoc/apidocs/index.html

# ✅ Pronto!
```

## 📍 Onde Encontrar a Documentação

Após gerar, a documentação estará em:

```
backend/target/site/apidocs/
├── index.html                    ← Abra este arquivo
├── allclasses.html
├── com/smarttask/
│   ├── model/Task.html
│   ├── model/User.html
│   ├── controller/TaskController.html
│   ├── service/TaskService.html
│   └── ...
└── stylesheet.css
```

## 🔍 Como Navegar

1. **Página Inicial** - Lista de pacotes e classes
2. **Class Index** - Clique em "All Classes" para ver todas
3. **Busca** - Use Ctrl+F para procurar
4. **Links Cruzados** - Clique em nomes de classes para ver detalhes

## 📚 Arquivos de Referência Rápida

Se não conseguir gerar, consulte:

| Arquivo | Conteúdo |
|---------|----------|
| `docs/javadoc/README.md` | Guia completo de geração |
| `docs/javadoc/INDEX.md` | Índice de classes |
| `docs/javadoc/CLASSES.md` | Referência de todas as classes |
| `docs/javadoc/SUMMARY.md` | Resumo da implementação |

## ⚙️ Requisitos

| Ferramenta | Versão | Instalação |
|-----------|--------|-----------|
| Maven | 3.8+ | `choco install maven` (Windows) |
| Java | 25+ | Já deve estar instalado |
| Docker | Latest | `choco install docker` (Windows) |

## 🆘 Resolvendo Problemas

### "Maven não encontrado"
```bash
# Windows (Chocolatey)
choco install maven

# Depois, feche e reabra o terminal
```

### "Permissão negada" (PowerShell)
```powershell
# Execute como administrador e rode:
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### "Docker erro"
```bash
# Use Maven direto
cd backend
mvn clean javadoc:aggregate
```

### "Porta 8080 em uso"
```bash
# Isso não afeta a geração de JavaDoc
# JavaDoc é uma ferramenta estática, não usa servidor
```

## ✨ Dicas Úteis

### 1. Adicionar ao PATH (Windows)
Para usar `mvn` de qualquer lugar:
```bash
# Windows
setx PATH "%PATH%;C:\Program Files\Maven\bin"

# Reinicie o terminal depois
```

### 2. Criar Shortcut (Windows)
Crie um arquivo `generate-docs.lnk` apontando para `generate-javadoc.ps1`

### 3. Automação (Git Hook)
Adicione ao `.git/hooks/post-commit` (Linux/macOS):
```bash
#!/bin/bash
cd backend
mvn clean javadoc:aggregate -DskipTests
```

### 4. CI/CD (GitHub Actions)
```yaml
name: Generate JavaDoc
on: [push]
jobs:
  javadoc:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '25'
      - run: cd backend && mvn clean javadoc:aggregate
      - uses: actions/upload-artifact@v3
        with:
          name: javadoc
          path: backend/target/site/apidocs/
```

## 📊 Estrutura de Classes Documentadas

```
✅ TOTALMENTE DOCUMENTADAS:
  • Task.java - Entidade principal
  • User.java - Usuário do sistema
  • TaskController.java - REST API
  • SmartTaskManagerApplication.java - Entry point
  • TaskService.java - Lógica de negócio
  • TaskRequest.java - DTO de requisição

⏳ PARCIALMENTE DOCUMENTADAS:
  • Outros Controllers
  • Outros Services
  • Outros DTOs

🔄 REFERENCIADAS:
  • AuthService
  • AIService
  • WhatsAppService
  • Repositórios
  • Configurações
```

## 🎓 O que Você Aprenderá

Ao explorar a documentação, você entenderá:

1. **Estrutura de Classes** - Como as classes se relacionam
2. **Fluxo de Dados** - Como dados fluem pela aplicação
3. **Segurança** - Autenticação JWT e Spring Security
4. **Observabilidade** - Como o tracing e métricas funcionam
5. **IA Integration** - Como OpenAI é integrado
6. **WhatsApp Notifications** - Como Twilio é usado

## 💡 Próximos Passos

1. ✅ Gerar documentação (este arquivo)
2. 📖 Explorar `docs/javadoc/CLASSES.md`
3. 🔍 Abrir `backend/target/site/apidocs/index.html`
4. 🎯 Entender a arquitetura do projeto
5. 💻 Começar a desenvolver!

## 📞 Suporte

- Documentação completa: `docs/javadoc/README.md`
- Referência de classes: `docs/javadoc/CLASSES.md`
- Issues no GitHub: https://github.com/jrcosta/smart-task-ai/issues

---

**Versão:** Smart Task AI 1.0.0  
**Última atualização:** Outubro 2025  
**Status:** ✅ Pronto para usar  

🚀 Boa sorte explorando a documentação!
