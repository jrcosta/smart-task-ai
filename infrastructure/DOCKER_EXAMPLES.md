# 🎓 Docker Compose - Exemplos Práticos

Este documento contém exemplos práticos de como usar o `docker-compose-unified.yml` para diferentes cenários.

## 1️⃣ Cenário: Estou Começando - Quero ver tudo funcionando

**Objetivo**: Rodar a aplicação completa com todas as ferramentas, como um "all-in-one"

### Windows
```bash
cd infrastructure
docker-compose-menu.bat
# Escolha opção 1
```

### Linux/macOS
```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml up -d
```

### O que acontecerá
```
✅ PostgreSQL iniciará na porta 5432
✅ Backend iniciará na porta 8080
✅ Frontend iniciará na porta 3000
✅ Jaeger iniciará na porta 16686
✅ Prometheus iniciará na porta 9090
✅ Grafana iniciará na porta 3001
```

### Acesse:
- Frontend: http://localhost:3000
- Backend Docs: http://localhost:8080/api/swagger-ui.html
- Grafana: http://localhost:3001 (admin/admin)
- Jaeger: http://localhost:16686

---

## 2️⃣ Cenário: Estou Desenvolvendo o Backend

**Objetivo**: Rodar Backend localmente no IDE com observabilidade em Docker

### Passo 1: Inicie só a observabilidade
```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml --profile observability up -d
```

### Passo 2: Configure variáveis de ambiente
```bash
# Windows PowerShell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/smarttask"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "postgres"

# Linux/macOS Bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/smarttask"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="postgres"
```

### Passo 3: Rode Backend localmente
```bash
cd backend
mvn spring-boot:run
```

### Passo 4: Desenvolva normalmente
```bash
# Abra outro terminal para Frontend
cd frontend
npm run dev
```

### Passo 5: Veja os Traces
- Jaeger: http://localhost:16686
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001

---

## 3️⃣ Cenário: Testar Integração Entre Frontend e Backend

**Objetivo**: Ambos rodando em Docker, mas sem observabilidade (mais leve)

### Remova os serviços de observabilidade do compose
```bash
cd infrastructure

# Versão simplificada (temporária)
docker-compose -f docker-compose-unified.yml up -d

# Sem observabilidade (só backend, frontend, banco)
# Parar Jaeger, Prometheus, Grafana manualmente:
docker stop smarttask-jaeger smarttask-prometheus smarttask-grafana
```

Ou crie um arquivo `.env`:
```env
# infrastructure/.env
OMIT_OBSERVABILITY=true
```

---

## 4️⃣ Cenário: Validar que as Métricas Estão Sendo Coletadas

**Objetivo**: Confirmar que aplicação está enviando métricas para o Prometheus

### Acesse Prometheus
```
http://localhost:9090
```

### Procure por métricas da aplicação
No campo de busca, digite:
```
http_requests_total
jvm_memory_used_bytes
process_cpu_usage
```

### Se aparecer dados, ✅ está funcionando!

---

## 5️⃣ Cenário: Criar um Dashboard no Grafana

**Objetivo**: Visualizar métricas em um dashboard bonito

### Passo 1: Acesse Grafana
```
http://localhost:3001
Login: admin / admin
```

### Passo 2: Adicione Prometheus como Data Source
1. Clique em **Configuration** (gear icon)
2. **Data Sources** → **Add data source**
3. Escolha **Prometheus**
4. URL: `http://prometheus:9090`
5. Clique em **Save & Test**

### Passo 3: Crie um Dashboard
1. **+** → **Dashboard**
2. **Add new panel**
3. No campo de métrica, digite: `process_uptime_seconds`
4. Visualize o gráfico

### Passo 4: Salve o Dashboard
Clique em **Save** e dê um nome

---

## 6️⃣ Cenário: Ver Traces de uma Requisição (Jaeger)

**Objetivo**: Entender como uma requisição flui pela aplicação

### Passo 1: Faça uma requisição
```bash
curl -X GET http://localhost:8080/api/tasks
```

Ou abra Frontend e clique em um botão

### Passo 2: Acesse Jaeger
```
http://localhost:16686
```

### Passo 3: Busque por traces
1. Service: `backend`
2. Operation: `TaskController.getTasks` (por exemplo)
3. Clique em **Find Traces**

### Passo 4: Veja o detalhamento
- Qual operação levou mais tempo?
- Quantas chamadas foram feitas?
- Houve erros?

---

## 7️⃣ Cenário: Limpar Tudo e Recomeçar

**Objetivo**: Remover todos os containers, volumes e dados

```bash
cd infrastructure

# Remove tudo (containers + volumes)
docker-compose -f docker-compose-unified.yml down -v

# Remover imagens também (opcional)
docker-compose -f docker-compose-unified.yml down -v --rmi all
```

Depois:
```bash
# Recomeçar do zero
docker-compose -f docker-compose-unified.yml up -d
```

---

## 8️⃣ Cenário: Usar Variáveis de Ambiente (JWT Secret, OpenAI Key)

**Objetivo**: Configurar chaves secretas para produção

### Crie arquivo `.env`
```bash
# infrastructure/.env
JWT_SECRET=sua-chave-super-secreta-aqui-com-pelo-menos-32-caracteres
OPENAI_API_KEY=sk-proj-sua-chave-aqui
TWILIO_ACCOUNT_SID=seu-twilio-sid
TWILIO_AUTH_TOKEN=seu-twilio-token
```

### Execute com o arquivo .env
```bash
docker-compose -f docker-compose-unified.yml up -d
```

Docker automaticamente lerá o `.env`

### Verifique
```bash
docker-compose -f docker-compose-unified.yml config | grep JWT_SECRET
```

---

## 9️⃣ Cenário: Debug - Conectar ao PostgreSQL com CLI

**Objetivo**: Inspecionar dados no banco manualmente

### Instale PostgreSQL Client (se não tiver)
```bash
# macOS
brew install postgresql

# Windows (choco)
choco install postgresql

# Ubuntu/Debian
sudo apt-get install postgresql-client
```

### Conecte
```bash
psql -h localhost -U postgres -d smarttask
```

Quando pedir senha, digite: `postgres`

### Comandos úteis
```sql
-- Listar tabelas
\dt

-- Ver estrutura de uma tabela
\d users

-- Listar tudo de users
SELECT * FROM users;

-- Contar registros
SELECT COUNT(*) FROM tasks;

-- Sair
\q
```

---

## 🔟 Cenário: Troubleshooting - Backend não inicia

**Problema**: Backend está com erro ao iniciar

### Verifique logs
```bash
docker-compose -f docker-compose-unified.yml logs backend
```

### Causas comuns
1. **PostgreSQL não iniciou**
   ```bash
   docker-compose -f docker-compose-unified.yml logs postgres
   ```

2. **Porta 8080 já em uso**
   ```bash
   # Windows
   netstat -ano | findstr :8080
   taskkill /PID <PID> /F
   
   # Linux/macOS
   lsof -i :8080
   kill -9 <PID>
   ```

3. **Arquivo de configuração corrompido**
   ```bash
   docker-compose -f docker-compose-unified.yml down -v
   docker-compose -f docker-compose-unified.yml up -d
   ```

---

## 1️⃣1️⃣ Scenario: Update Imagens Docker

**Objetivo**: Atualizar imagens para versões mais novas

```bash
cd infrastructure

# Baixar imagens mais recentes
docker-compose -f docker-compose-unified.yml pull

# Parar e remover containers antigos
docker-compose -f docker-compose-unified.yml down

# Iniciar com novas imagens
docker-compose -f docker-compose-unified.yml up -d
```

---

## 1️⃣2️⃣ Scenario: Monitorar Recursos (CPU, Memória)

**Objetivo**: Ver quanto de CPU/Memória cada container está usando

```bash
# Ver uso em tempo real
docker stats

# Ver histórico
docker stats --no-stream
```

Se um container estiver usando muita memória:
```bash
# Aumentar limite no docker-compose
# No serviço, adicione:
# deploy:
#   resources:
#     limits:
#       memory: 512M
```

---

## 📚 Resumo Rápido

| Ação | Comando |
|------|---------|
| Iniciar tudo | `docker-compose -f docker-compose-unified.yml up -d` |
| Parar tudo | `docker-compose -f docker-compose-unified.yml down` |
| Ver status | `docker-compose -f docker-compose-unified.yml ps` |
| Ver logs | `docker-compose -f docker-compose-unified.yml logs -f backend` |
| Limpar tudo | `docker-compose -f docker-compose-unified.yml down -v` |
| Observabilidade só | `docker-compose -f docker-compose-unified.yml --profile observability up -d` |
| Menu (Windows) | `docker-compose-menu.bat` |
| Menu (PowerShell) | `./docker-compose-menu.ps1` |

---

## 🎯 Próximos Passos

1. **Customize os dashboards**: Crie métricas específicas do seu projeto no Grafana
2. **Configure alertas**: Use Prometheus para alertas quando métricas ultrapassam limites
3. **Integre CI/CD**: Use essas imagens no seu pipeline de entrega
4. **Produza**: Publique suas imagens no Docker Hub e use em produção

---

## 💡 Dicas Finais

- **Logs são seus amigos**: Sempre veja `docker logs` quando algo não funciona
- **Redes Docker**: Containers podem se comunicar pelo nome (ex: `http://backend:8080`)
- **Volumes persistem dados**: Seus dados PostgreSQL não sumem ao parar o container
- **Profiles = flexibilidade**: Use-os para diferentes ambientes
