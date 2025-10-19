# 🐳 Docker Compose Unificado - Smart Task AI

Este documento descreve como usar o novo arquivo `docker-compose-unified.yml` que combina a aplicação completa com observabilidade.

## 📋 Visão Geral

O arquivo `docker-compose-unified.yml` contém **todos os serviços** do Smart Task AI com suporte a **profiles Docker Compose**, permitindo flexibilidade para diferentes cenários de desenvolvimento e estudo:

| Cenário | Profile | Serviços | Comando |
|---------|---------|----------|---------|
| **Estudo Completo** | (padrão) | Backend + Frontend + Database + Observabilidade | `docker-compose -f docker-compose-unified.yml up -d` |
| **Só Monitoramento** | `observability` | Jaeger + Prometheus + Grafana | `docker-compose -f docker-compose-unified.yml --profile observability up -d` |
| **Backend Local** | `observability` | Jaeger + Prometheus + Grafana *(Backend rodando localmente)* | `docker-compose -f docker-compose-unified.yml --profile observability up -d` |

## 🚀 Quick Start

### 1️⃣ **Modo Estudo Completo** (Recomendado para Iniciantes)

Suba a aplicação completa com banco de dados e observabilidade:

```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml up -d
```

**Serviços iniciados**:
- 🗄️ **PostgreSQL** (banco de dados): http://localhost:5432
- 🔙 **Backend** (Spring Boot): http://localhost:8080/api
- 🎨 **Frontend** (React): http://localhost:3000
- 📊 **Prometheus**: http://localhost:9090
- 📈 **Grafana**: http://localhost:3001 (user: `admin`, senha: `admin`)
- 🔍 **Jaeger**: http://localhost:16686

### 2️⃣ **Modo Observabilidade + Backend Local**

Use quando estiver desenvolvendo o backend localmente no IDE:

```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml --profile observability up -d
```

**Serviços iniciados**:
- 🗄️ **PostgreSQL**: http://localhost:5432
- 📊 **Prometheus**: http://localhost:9090
- 📈 **Grafana**: http://localhost:3001
- 🔍 **Jaeger**: http://localhost:16686

**Backend**: Rode localmente em `http://localhost:8080` 
```bash
cd backend
mvn spring-boot:run
```

### 3️⃣ **Parar Todos os Serviços**

```bash
docker-compose -f docker-compose-unified.yml down
```

**Remover dados persistidos** (limpar banco de dados e métricas):

```bash
docker-compose -f docker-compose-unified.yml down -v
```

## 🔧 Variáveis de Ambiente

Configure variáveis no seu sistema operacional ou em um arquivo `.env`:

```bash
# .env (criar na pasta infrastructure/)
JWT_SECRET=sua-chave-secreta-aqui
OPENAI_API_KEY=sua-chave-openai-aqui
TWILIO_ACCOUNT_SID=seu-twilio-sid
TWILIO_AUTH_TOKEN=seu-twilio-token
```

**No Windows PowerShell**:
```powershell
$env:JWT_SECRET = "sua-chave"
$env:OPENAI_API_KEY = "sua-chave"
docker-compose -f docker-compose-unified.yml up -d
```

**No Linux/macOS Bash**:
```bash
export JWT_SECRET="sua-chave"
export OPENAI_API_KEY="sua-chave"
docker-compose -f docker-compose-unified.yml up -d
```

## 📌 Acessar os Serviços

### Frontend
- **URL**: http://localhost:3000
- **Ação**: Acesse e faça login na aplicação

### Backend API (Documentação)
- **URL**: http://localhost:8080/api/swagger-ui.html
- **Descrição**: Swagger da API REST

### Prometheus (Métricas)
- **URL**: http://localhost:9090
- **Descrição**: Métricas brutas em tempo real

### Grafana (Dashboard)
- **URL**: http://localhost:3001
- **User**: `admin`
- **Senha**: `admin`
- **Descrição**: Dashboards visuais de métricas

### Jaeger (Distributed Tracing)
- **URL**: http://localhost:16686
- **Descrição**: Traces de requisições entre serviços

### PostgreSQL (Banco de Dados)
- **Host**: `localhost`
- **Porta**: `5432`
- **Database**: `smarttask`
- **User**: `postgres`
- **Senha**: `postgres`

**Conectar com psql** (requer PostgreSQL instalado):
```bash
psql -h localhost -U postgres -d smarttask
```

## 🔍 Verificar Status dos Serviços

```bash
docker-compose -f docker-compose-unified.yml ps
```

**Esperado**:
```
NAME                    STATUS              PORTS
smarttask-postgres      Up 2 minutes        0.0.0.0:5432->5432/tcp
smarttask-backend       Up 2 minutes        0.0.0.0:8080->8080/tcp
smarttask-frontend      Up 2 minutes        0.0.0.0:3000->80/tcp
smarttask-jaeger        Up 2 minutes        0.0.0.0:16686->16686/tcp
smarttask-prometheus    Up 2 minutes        0.0.0.0:9090->9090/tcp
smarttask-grafana       Up 2 minutes        0.0.0.0:3001->3000/tcp
```

## 📊 Exemplo: Visualizar Traces no Jaeger

1. Vá para http://localhost:16686
2. No dropdown de serviço, selecione `backend`
3. Clique em "Find Traces"
4. Veja as requisições e o tempo de cada operação

## 📈 Exemplo: Criar Dashboard no Grafana

1. Acesse http://localhost:3001
2. Login com `admin` / `admin`
3. **Add data source** → Selecione `Prometheus` → URL: `http://prometheus:9090`
4. **Create dashboard** → Selecione a métrica desejada

## 🐛 Troubleshooting

### Erro: "Port 3000 already in use"
```bash
# Liberar porta (Linux/macOS)
lsof -i :3000
kill -9 <PID>

# Windows PowerShell
netstat -ano | findstr :3000
taskkill /PID <PID> /F
```

### Backend não conecta ao PostgreSQL
```bash
# Verificar logs
docker-compose -f docker-compose-unified.yml logs backend

# Esperar saúde do banco
docker-compose -f docker-compose-unified.yml up -d postgres
sleep 10
docker-compose -f docker-compose-unified.yml up -d
```

### Grafana perdeu senhas
```bash
docker-compose -f docker-compose-unified.yml down -v
docker-compose -f docker-compose-unified.yml up -d
# Reseta Grafana (user/senha voltam a admin/admin)
```

## 🎓 Para Fins de Estudo

Este arquivo é **perfeito para aprender**:
- ✅ Docker Compose com múltiplos serviços
- ✅ Observabilidade (Prometheus, Grafana, Jaeger)
- ✅ Comunicação entre containers
- ✅ Volumes persistentes
- ✅ Health checks e dependências
- ✅ Docker profiles e variáveis de ambiente

## 📝 Notas

- **Profiles**: O arquivo usa `profiles:` para ativar/desativar serviços
- **Rede**: Todos os containers compartilham `smarttask-network` para comunicação
- **Volumes**: Dados persistem entre reinicializações (postgres_data, prometheus_data, grafana_data)
- **Saúde**: Backend aguarda PostgreSQL estar saudável antes de iniciar
- **Logs**: Use `docker-compose logs -f <service>` para ver logs em tempo real

## 🔗 Referências

- [Docker Compose Profiles](https://docs.docker.com/compose/profiles/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Prometheus](https://prometheus.io/)
- [Grafana](https://grafana.com/)
- [Jaeger Tracing](https://www.jaegertracing.io/)
