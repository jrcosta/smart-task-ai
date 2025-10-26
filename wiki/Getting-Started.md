# 🚀 Getting Started - Smart Task AI

Bem-vindo! Este guia vai te ajudar a configurar e rodar o Smart Task AI em menos de 5 minutos.

## 📋 Pré-requisitos

Escolha uma das opções abaixo:

### Opção 1: Docker (Recomendado - Mais Fácil)
- **Docker Desktop** instalado ([Download](https://www.docker.com/products/docker-desktop))
- **Docker Compose** (geralmente vem com Docker Desktop)

### Opção 2: Execução Local
- **Java 25+** ([Download](https://adoptium.net/))
- **Node.js 18+** ([Download](https://nodejs.org/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **PostgreSQL 15+** ([Download](https://www.postgresql.org/download/)) - Opcional, pode usar H2

## ⚡ Setup Rápido (Docker - 5 minutos)

### Passo 1: Clone o Repositório
```bash
git clone https://github.com/jrcosta/smart-task-ai.git
cd smart-task-ai
```

### Passo 2: Inicie com Docker Compose

**Windows:**
```bash
cd infrastructure
docker-compose-menu.bat
# Escolha opção [1] - Modo COMPLETO
```

**Linux/macOS:**
```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml up -d
```

### Passo 3: Aguarde a Inicialização
Aguarde aproximadamente 30-60 segundos para todos os serviços iniciarem.

### Passo 4: Acesse a Aplicação
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Swagger Docs**: http://localhost:8080/api/swagger-ui.html

### Passo 5: Faça Login
1. Acesse http://localhost:3000
2. Clique em "Criar Conta"
3. Preencha seus dados
4. Comece a usar!

## 🎯 Configuração de IA e WhatsApp (Opcional)

### Configurar OpenAI (Recursos de IA)

**Opção A: Via Interface Web** (Recomendado)
1. Faça login no aplicativo
2. Vá em **Configurações** → **API Keys**
3. Cole sua chave da OpenAI
4. Salve

**Opção B: Via Variável de Ambiente**
```bash
# Edite o arquivo .env no diretório infrastructure
OPENAI_API_KEY=sk-your-openai-key-here
```

**Como obter a chave OpenAI:**
1. Acesse https://platform.openai.com
2. Crie uma conta (se ainda não tiver)
3. Vá em https://platform.openai.com/api-keys
4. Clique em "Create new secret key"
5. Copie a chave gerada

### Configurar WhatsApp (Notificações)

**Opção A: Via Interface Web** (Recomendado)
1. Faça login no aplicativo
2. Vá em **Configurações** → **API Keys**
3. Preencha:
   - Account SID
   - Auth Token
   - WhatsApp Number
4. Salve

**Opção B: Via Variável de Ambiente**
```bash
# Edite o arquivo .env no diretório infrastructure
TWILIO_ACCOUNT_SID=seu-account-sid
TWILIO_AUTH_TOKEN=seu-auth-token
TWILIO_WHATSAPP_NUMBER=whatsapp:+14155238886
```

**Como obter credenciais Twilio:**
1. Crie conta gratuita em https://www.twilio.com/try-twilio
2. Configure WhatsApp Sandbox no console
3. Copie Account SID e Auth Token do dashboard

📖 **Guia Completo**: Veja [Configuration.md](Configuration.md) para instruções detalhadas.

## 🎨 Primeiro Uso

### Criar sua Primeira Tarefa

1. **Acesse o Dashboard**: http://localhost:3000
2. **Clique em "Nova Tarefa"**
3. **Preencha os dados**:
   - Título: Ex: "Implementar autenticação"
   - Descrição: Detalhe a tarefa
   - Prioridade: Escolha BAIXA, MEDIA ou ALTA
   - Prazo: Selecione uma data
4. **Use a IA** (se configurado):
   - Clique em "Analisar com IA"
   - A IA irá sugerir:
     - Prioridade adequada
     - Tags relevantes
     - Subtarefas
     - Estimativa de tempo
5. **Salve a tarefa**

### Explorar o Dashboard

- **Visão Geral**: Veja estatísticas de produtividade
- **Lista de Tarefas**: Filtre por status, prioridade, tags
- **Calendário**: Visualize prazos
- **Gráficos**: Analise seu progresso

## 🔍 Verificar se Tudo Está Funcionando

### 1. Verifique os Containers (Docker)
```bash
docker ps
```
Você deve ver:
- `smart-task-backend`
- `smart-task-frontend`
- `postgres`
- `prometheus` (se iniciou com observabilidade)
- `grafana` (se iniciou com observabilidade)
- `jaeger` (se iniciou com observabilidade)

### 2. Teste o Backend
```bash
curl http://localhost:8080/api/actuator/health
```
Resposta esperada: `{"status":"UP"}`

### 3. Teste o Frontend
Acesse http://localhost:3000 no navegador

### 4. Teste a API com Swagger
Acesse http://localhost:8080/api/swagger-ui.html

## 📊 Recursos de Observabilidade (Opcional)

Se você iniciou com o perfil de observabilidade completo:

### Prometheus (Métricas)
- **URL**: http://localhost:9090
- **Uso**: Consulte métricas da aplicação
- **Exemplo**: Digite `http_server_requests_seconds_count` na busca

### Grafana (Dashboards)
- **URL**: http://localhost:3001
- **Login**: admin / admin
- **Uso**: Visualize dashboards pré-configurados

### Jaeger (Traces Distribuídos)
- **URL**: http://localhost:16686
- **Uso**: Rastreie requisições através dos serviços
- **Procure por**: "smart-task-backend"

## 🛑 Parar a Aplicação

### Docker
```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml down
```

### Para remover também os dados (volumes)
```bash
docker-compose -f docker-compose-unified.yml down -v
```

## 🔄 Reiniciar a Aplicação

### Docker
```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml restart
```

## 📝 Próximos Passos

Agora que você já tem tudo rodando:

1. 📖 **Explore as [Features](Features.md)** - Conheça todas as funcionalidades
2. 🔧 **Configure [API Keys](Configuration.md)** - Habilite IA e WhatsApp
3. 📊 **Veja a [Architecture](Architecture.md)** - Entenda como funciona
4. 💻 **Configure [Development](Development-Guide.md)** - Se for desenvolver

## 🆘 Problemas Comuns

### Porta 3000 já está em uso
```bash
# Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti:3000 | xargs kill
```

### Porta 8080 já está em uso
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/macOS
lsof -ti:8080 | xargs kill
```

### Docker não inicia os containers
```bash
# Verifique os logs
docker-compose -f docker-compose-unified.yml logs

# Reconstrua as imagens
docker-compose -f docker-compose-unified.yml up --build
```

### Erro de conexão com banco de dados
```bash
# Reinicie apenas o banco
docker-compose -f docker-compose-unified.yml restart postgres

# Ou remova e recrie os volumes
docker-compose -f docker-compose-unified.yml down -v
docker-compose -f docker-compose-unified.yml up -d
```

## 📚 Mais Recursos

- 🐛 **[Troubleshooting](Troubleshooting.md)** - Solução completa de problemas
- ❓ **[FAQ](FAQ.md)** - Perguntas frequentes
- 🔐 **[Security](Security.md)** - Práticas de segurança
- 🚀 **[Deployment](Deployment.md)** - Deploy em produção

## 💡 Dicas Úteis

1. **Use o menu Docker no Windows**: `docker-compose-menu.bat` é mais fácil
2. **Configure IA primeiro**: Para aproveitar todos os recursos
3. **Explore o Swagger**: Entenda a API interativamente
4. **Veja os logs**: `docker-compose logs -f` para acompanhar em tempo real
5. **Backup regular**: Exporte seus dados periodicamente

---

🎉 **Parabéns! Você configurou o Smart Task AI com sucesso!**

*Precisa de ajuda? Abra uma [issue](https://github.com/jrcosta/smart-task-ai/issues) ou consulte a [documentação completa](Home.md).*

*Última atualização: Outubro 2025*
