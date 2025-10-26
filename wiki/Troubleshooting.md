# 🔧 Troubleshooting - Smart Task AI

Soluções para problemas comuns e guia de diagnóstico.

## 📋 Índice

1. [Problemas de Instalação](#problemas-de-instalação)
2. [Problemas com Docker](#problemas-com-docker)
3. [Problemas com Backend](#problemas-com-backend)
4. [Problemas com Frontend](#problemas-com-frontend)
5. [Problemas com Banco de Dados](#problemas-com-banco-de-dados)
6. [Problemas com IA/OpenAI](#problemas-com-iaopenai)
7. [Problemas com WhatsApp/Twilio](#problemas-com-whatsapptwilio)
8. [Problemas de Autenticação](#problemas-de-autenticação)
9. [Problemas de Performance](#problemas-de-performance)
10. [Diagnóstico Geral](#diagnóstico-geral)

---

## 🛠️ Problemas de Instalação

### Git clone falha ou é muito lento

**Sintoma:**
```bash
$ git clone https://github.com/jrcosta/smart-task-ai.git
fatal: unable to access 'https://github.com/...': Failed to connect
```

**Soluções:**
```bash
# 1. Verifique conexão com internet
ping github.com

# 2. Use SSH em vez de HTTPS
git clone git@github.com:jrcosta/smart-task-ai.git

# 3. Aumente o timeout do git
git config --global http.postBuffer 524288000
git config --global http.lowSpeedLimit 0
git config --global http.lowSpeedTime 999999

# 4. Clone com profundidade limitada (mais rápido)
git clone --depth 1 https://github.com/jrcosta/smart-task-ai.git
```

### Versão do Java incorreta

**Sintoma:**
```
Error: Java version 17 is not supported. Requires Java 25+
```

**Solução:**
```bash
# Verificar versão atual
java -version

# Instalar Java 25
# Windows: https://adoptium.net/
# Linux (Ubuntu):
sudo apt install openjdk-25-jdk

# macOS:
brew install openjdk@25

# Definir JAVA_HOME
export JAVA_HOME=/path/to/java25
export PATH=$JAVA_HOME/bin:$PATH
```

### Node.js versão incompatível

**Sintoma:**
```
npm ERR! engine Unsupported engine
npm ERR! Required: {"node":">=18.0.0"}
```

**Solução:**
```bash
# Verificar versão
node -version

# Instalar versão correta
# Windows/Mac: https://nodejs.org
# Linux com nvm:
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 18
nvm use 18
```

---

## 🐳 Problemas com Docker

### Docker não está instalado ou não inicia

**Sintoma:**
```bash
$ docker ps
Cannot connect to the Docker daemon. Is the docker daemon running?
```

**Soluções:**
```bash
# Windows/Mac: Inicie Docker Desktop
# Procure o ícone do Docker na bandeja do sistema

# Linux: Inicie o serviço
sudo systemctl start docker
sudo systemctl enable docker

# Verifique status
sudo systemctl status docker

# Adicione seu usuário ao grupo docker (Linux)
sudo usermod -aG docker $USER
# Faça logout e login novamente
```

### Porta já está em uso

**Sintoma:**
```
Error: bind: address already in use
Port 3000 is already allocated
```

**Solução 1: Parar processo na porta**
```bash
# Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:3000 | xargs kill -9
# Ou para múltiplas portas
lsof -ti:3000,8080,5432 | xargs kill -9
```

**Solução 2: Usar portas diferentes**
```yaml
# Edite docker-compose-unified.yml
services:
  frontend:
    ports:
      - "3001:3000"  # Acesse em localhost:3001
  backend:
    ports:
      - "8081:8080"  # Acesse em localhost:8081
```

### Containers não iniciam

**Sintoma:**
```bash
$ docker-compose ps
All containers show "Exit 1" or "Restarting"
```

**Diagnóstico:**
```bash
# 1. Veja logs detalhados
docker-compose -f docker-compose-unified.yml logs

# 2. Veja logs de um serviço específico
docker-compose -f docker-compose-unified.yml logs backend
docker-compose -f docker-compose-unified.yml logs frontend

# 3. Veja logs em tempo real
docker-compose -f docker-compose-unified.yml logs -f
```

**Soluções:**
```bash
# 1. Reconstruir sem cache
docker-compose -f docker-compose-unified.yml build --no-cache

# 2. Limpar tudo e recomeçar
docker-compose -f docker-compose-unified.yml down -v
docker system prune -a --volumes
docker-compose -f docker-compose-unified.yml up -d

# 3. Verificar variáveis de ambiente
cat .env
# Certifique-se que não tem erros de sintaxe
```

### Erro de permissão (Linux)

**Sintoma:**
```
Got permission denied while trying to connect to the Docker daemon socket
```

**Solução:**
```bash
# Adicione usuário ao grupo docker
sudo usermod -aG docker $USER

# Reinicie a sessão
logout
# Faça login novamente

# Ou use sudo (não recomendado)
sudo docker-compose up -d
```

### Falta de espaço em disco

**Sintoma:**
```
Error: no space left on device
```

**Solução:**
```bash
# Verificar uso de espaço
docker system df

# Limpar imagens não usadas
docker image prune -a

# Limpar volumes não usados
docker volume prune

# Limpar tudo (cuidado!)
docker system prune -a --volumes

# Verificar espaço disponível
df -h
```

---

## 🔙 Problemas com Backend

### Backend não inicia

**Sintoma:**
```
Backend container exits immediately or keeps restarting
```

**Diagnóstico:**
```bash
# Veja os logs
docker-compose logs backend

# Ou rode localmente para ver erro completo
cd backend
mvn spring-boot:run
```

**Problemas Comuns:**

#### Erro de conexão com banco
```
Error: Could not open connection to database
```

**Solução:**
```bash
# Verifique se PostgreSQL está rodando
docker-compose ps postgres

# Reinicie o banco
docker-compose restart postgres

# Verifique variáveis de ambiente
# Edite docker-compose-unified.yml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/smarttask
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: postgres
```

#### Porta 8080 em uso
```
Error: Port 8080 is already in use
```

**Solução:**
```bash
# Parar processo na porta 8080
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

#### Erro de compilação Maven
```
Failed to execute goal on project: Could not resolve dependencies
```

**Solução:**
```bash
# Limpe cache do Maven
cd backend
mvn clean install -U

# Ou force download de dependências
mvn dependency:purge-local-repository
mvn clean install
```

### Health check falha

**Sintoma:**
```bash
$ curl http://localhost:8080/api/actuator/health
Connection refused
```

**Soluções:**
```bash
# 1. Aguarde mais tempo (pode levar 30-60s para iniciar)

# 2. Verifique se backend está rodando
docker-compose ps backend

# 3. Verifique logs
docker-compose logs backend

# 4. Teste endpoint alternativo
curl http://localhost:8080/api/swagger-ui.html
```

### Erro 500 Internal Server Error

**Diagnóstico:**
```bash
# Veja logs do backend
docker-compose logs backend | grep ERROR

# Ou rode com debug
docker-compose -f docker-compose-unified.yml up backend
```

**Causas Comuns:**
- NullPointerException no código
- Erro de validação de dados
- Conexão com IA/Twilio falhou
- Erro de consulta ao banco

**Solução:**
```bash
# Consulte stack trace nos logs
# Corrija o problema específico mostrado
# Reinicie o backend
docker-compose restart backend
```

---

## 🎨 Problemas com Frontend

### Tela branca ou erro de carregamento

**Sintoma:**
Navegador mostra tela branca ou erro 404.

**Soluções:**
```bash
# 1. Veja console do navegador (F12 → Console)
# Procure por erros JavaScript

# 2. Limpe cache do navegador
Ctrl+Shift+Delete → Limpar tudo

# 3. Force rebuild
docker-compose build --no-cache frontend
docker-compose up -d frontend

# 4. Acesse logs
docker-compose logs frontend
```

### Erro CORS

**Sintoma:**
```
Access to fetch at 'http://localhost:8080/api/tasks' from origin 
'http://localhost:3000' has been blocked by CORS policy
```

**Soluções:**
```bash
# 1. Verifique se backend está rodando
curl http://localhost:8080/api/actuator/health

# 2. Reinicie ambos os serviços
docker-compose restart backend frontend

# 3. Verifique configuração CORS no backend
# backend/src/main/java/com/smarttask/config/SecurityConfig.java
# Deve permitir origem http://localhost:3000
```

### npm install falha

**Sintoma:**
```
npm ERR! code ENOENT
npm ERR! syscall open
```

**Soluções:**
```bash
# 1. Limpe cache do npm
cd frontend
npm cache clean --force

# 2. Delete node_modules e package-lock.json
rm -rf node_modules package-lock.json

# 3. Reinstale
npm install

# 4. Se persistir, use yarn
npm install -g yarn
yarn install
```

### Build do Vite falha

**Sintoma:**
```
vite build
Error: Cannot find module '@vitejs/plugin-react'
```

**Solução:**
```bash
cd frontend

# Reinstale dependências
rm -rf node_modules
npm install

# Ou instale a dependência faltante
npm install -D @vitejs/plugin-react

# Tente build novamente
npm run build
```

---

## 🗄️ Problemas com Banco de Dados

### PostgreSQL não inicia

**Sintoma:**
```bash
$ docker-compose ps
postgres    Exit 1
```

**Soluções:**
```bash
# 1. Veja logs
docker-compose logs postgres

# 2. Remova e recrie o volume
docker-compose down -v
docker-compose up -d postgres

# 3. Verifique permissões (Linux)
sudo chown -R 999:999 ./postgres-data
```

### Dados foram perdidos

**Sintoma:**
Todas as tarefas desapareceram após reiniciar.

**Causa:**
Volume do banco foi removido com `docker-compose down -v`.

**Prevenção:**
```bash
# NUNCA use -v se quiser manter dados
docker-compose down     # OK - mantém dados
docker-compose down -v  # REMOVE dados!

# Para backup
docker exec postgres pg_dump -U postgres smarttask > backup.sql

# Para restaurar
docker exec -i postgres psql -U postgres smarttask < backup.sql
```

### Erro de migração de schema

**Sintoma:**
```
Flyway migration failed: Schema validation error
```

**Solução:**
```bash
# Opção 1: Reset completo (perde dados)
docker-compose down -v
docker-compose up -d

# Opção 2: Acesse banco e corrija manualmente
docker exec -it postgres psql -U postgres smarttask
# Execute ALTER TABLE conforme necessário

# Opção 3: Desabilite validação (desenvolvimento apenas)
# Em application.yml:
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

### Conexão lenta com banco

**Sintoma:**
Consultas demoram muito (>2s).

**Soluções:**
```bash
# 1. Adicione índices
# Acesse o banco
docker exec -it postgres psql -U postgres smarttask

# Crie índices
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);

# 2. Analise query plans
EXPLAIN ANALYZE SELECT * FROM tasks WHERE user_id = 1;

# 3. Vacuum e analyze
VACUUM ANALYZE;
```

---

## 🤖 Problemas com IA/OpenAI

### IA não está funcionando

**Sintoma:**
Análise de IA retorna erro ou análise mock.

**Diagnóstico:**
```bash
# 1. Verifique se chave está configurada
# Login → Configurações → API Keys
# Ou veja logs:
docker-compose logs backend | grep OpenAI
```

**Soluções:**

#### Chave não configurada
```
Configure em: Configurações → API Keys → OpenAI API Key
Formato: sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

#### Chave inválida
```
Error: Invalid API key

Solução:
1. Gere nova chave em https://platform.openai.com/api-keys
2. Atualize em Configurações
3. Teste novamente
```

#### Sem crédito
```
Error: You exceeded your current quota

Solução:
1. Acesse https://platform.openai.com/account/billing
2. Adicione método de pagamento
3. Aguarde processamento (pode levar minutos)
```

#### Rate limit atingido
```
Error: Rate limit exceeded

Solução:
1. Aguarde 1 minuto
2. Tente novamente
3. Ou considere upgrade do plano OpenAI
```

### Análise demora muito

**Sintoma:**
Análise de IA leva >10 segundos.

**Causas:**
- API da OpenAI está lenta
- Descrição da tarefa muito grande
- Problemas de rede

**Soluções:**
```bash
# 1. Verifique latência com OpenAI
curl -w "@curl-format.txt" -o /dev/null -s https://api.openai.com/v1/models

# 2. Reduza tamanho da descrição
# Limite: ~2000 caracteres para respostas rápidas

# 3. Configure timeout maior
# Em backend application.yml:
openai:
  timeout: 30s  # Padrão é 10s
```

### Respostas da IA são inadequadas

**Sintoma:**
IA sugere prioridades ou tags erradas.

**Soluções:**
```
1. Seja mais específico na descrição
   Ruim: "Fazer tela"
   Bom: "Criar componente React para tela de login com formulário e validação"

2. Use termos técnicos
   Exemplo: "API REST com autenticação JWT"

3. Inclua contexto
   Exemplo: "URGENTE: Bug crítico em produção afetando 1000 usuários"

4. Revise e ajuste sugestões
   Você sempre pode modificar o que a IA sugere
```

---

## 📱 Problemas com WhatsApp/Twilio

### Notificações não chegam

**Diagnóstico:**
```bash
# 1. Veja logs do backend
docker-compose logs backend | grep Twilio

# 2. Teste credenciais
# Configurações → API Keys → Twilio → Testar Envio
```

**Causas Comuns:**

#### Credenciais incorretas
```
Error: Authentication failed

Solução:
1. Verifique Account SID e Auth Token
2. Acesse https://console.twilio.com
3. Copie credenciais corretas
4. Atualize em Configurações → API Keys
```

#### Não conectado ao Sandbox
```
Error: Unverified number

Solução:
1. Envie mensagem para +1-415-523-8886
2. Conteúdo: "join <seu-codigo>"
3. Aguarde confirmação
4. Tente enviar notificação novamente
```

#### Número formatado incorretamente
```
Error: Invalid phone number

Formato correto: +5511999999999
Incorreto: 11999999999 ou (11) 9999-9999

Solução:
Inclua código do país (+55 para Brasil)
```

#### Sem crédito Twilio
```
Error: Insufficient funds

Solução:
1. Acesse https://console.twilio.com/billing
2. Adicione crédito
3. Aguarde processamento
```

### Sandbox expira constantemente

**Sintoma:**
A cada 3 dias precisa reativar o Sandbox.

**Explicação:**
Sandbox expira após 3 dias de inatividade.

**Soluções:**
```
Opção 1: Reative frequentemente
- Envie "join <codigo>" novamente

Opção 2: WhatsApp Business (pago)
- Solicite número próprio no Twilio
- Sem expiração
- Custo: ~$1-5/mês + $0.005/msg
```

---

## 🔐 Problemas de Autenticação

### Não consigo fazer login

**Sintoma:**
```
Error: Invalid credentials
```

**Soluções:**
```
1. Verifique senha
   - Senhas são case-sensitive
   - Sem espaços extras

2. Reset de senha (manualmente)
   docker exec -it postgres psql -U postgres smarttask
   UPDATE users SET password='$2a$10$...' WHERE email='seu@email.com';
   
3. Crie nova conta
   Se esqueceu senha, crie nova conta com email diferente
```

### Token JWT expirou

**Sintoma:**
```
Error: 401 Unauthorized
Token expired
```

**Solução:**
```
1. Faça logout
2. Faça login novamente
3. Novo token será gerado

Ou limpe localStorage:
F12 → Application → Local Storage → Clear
```

### Session perdida ao recarregar página

**Causa:**
localStorage foi limpo ou token expirou.

**Prevenção:**
```javascript
// Token dura 7 dias por padrão
// Para aumentar, edite backend:
// JwtTokenProvider.java
private static final long JWT_EXPIRATION = 604800000L; // 7 dias
```

---

## ⚡ Problemas de Performance

### Aplicação está lenta

**Diagnóstico:**
```bash
# 1. Verifique uso de recursos
docker stats

# 2. Veja latência de endpoints
# No Grafana: http://localhost:3001
# Dashboard → HTTP Request Duration

# 3. Analise traces
# No Jaeger: http://localhost:16686
```

**Soluções:**

#### Alto uso de CPU
```bash
# Identifique container problemático
docker stats

# Verifique logs para loops infinitos
docker-compose logs <service>

# Aumente limites
# Em docker-compose-unified.yml:
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 2G
```

#### Alto uso de memória
```bash
# Verifique heap da JVM
docker exec backend jcmd 1 VM.native_memory summary

# Ajuste heap size
# Em docker-compose-unified.yml:
environment:
  JAVA_OPTS: -Xmx1024m -Xms512m
```

#### Queries lentas
```bash
# Ative log de slow queries
# Em application.yml:
logging:
  level:
    org.hibernate.SQL: DEBUG
    
# Adicione índices (veja seção de banco de dados)
```

### Frontend demora para carregar

**Soluções:**
```bash
# 1. Build para produção
cd frontend
npm run build

# 2. Use compression
# Em vite.config.ts:
import compression from 'vite-plugin-compression'

export default defineConfig({
  plugins: [compression()]
})

# 3. Lazy load components
// Em React:
const TaskList = lazy(() => import('./components/TaskList'))
```

---

## 🔍 Diagnóstico Geral

### Checklist de Diagnóstico

```bash
# 1. Todos containers estão rodando?
docker-compose ps
# Esperado: todos "Up"

# 2. Health check do backend OK?
curl http://localhost:8080/api/actuator/health
# Esperado: {"status":"UP"}

# 3. Frontend acessível?
curl http://localhost:3000
# Esperado: HTML da aplicação

# 4. Banco de dados conectado?
docker exec postgres psql -U postgres -c "SELECT 1"
# Esperado: "1"

# 5. Logs sem erros críticos?
docker-compose logs | grep ERROR
# Esperado: poucos ou nenhum erro

# 6. Portas corretas?
netstat -an | grep LISTEN | grep -E "3000|8080|5432"
# Esperado: todas as portas escutando
```

### Comandos Úteis de Debug

```bash
# Ver todos logs
docker-compose logs

# Logs de serviço específico
docker-compose logs backend

# Seguir logs em tempo real
docker-compose logs -f backend

# Últimas 100 linhas
docker-compose logs --tail=100 backend

# Logs com timestamps
docker-compose logs -t backend

# Inspecionar container
docker inspect <container_id>

# Acessar shell do container
docker exec -it backend /bin/bash

# Ver processos rodando
docker exec backend ps aux

# Ver variáveis de ambiente
docker exec backend env

# Testar conectividade entre containers
docker exec frontend ping backend

# Ver uso de recursos
docker stats

# Ver rede
docker network inspect smart-task-ai_default
```

### Logs Importantes

```bash
# Backend Spring Boot
docker-compose logs backend | grep -E "ERROR|WARN|Exception"

# PostgreSQL
docker-compose logs postgres | grep -E "ERROR|FATAL"

# Frontend Vite
docker-compose logs frontend | grep -E "ERROR|Failed"

# Prometheus
docker-compose logs prometheus | grep -E "ERROR|WARN"
```

### Reset Completo

**Quando tudo mais falhar:**
```bash
# 1. Pare tudo
docker-compose down -v

# 2. Limpe Docker
docker system prune -a --volumes

# 3. Limpe código
cd frontend && rm -rf node_modules package-lock.json
cd ../backend && mvn clean

# 4. Reconstrua
docker-compose -f docker-compose-unified.yml build --no-cache

# 5. Inicie
docker-compose -f docker-compose-unified.yml up -d

# 6. Aguarde 60 segundos

# 7. Teste
curl http://localhost:8080/api/actuator/health
curl http://localhost:3000
```

---

## 🆘 Ainda com Problemas?

### 1. Procure em Issues Existentes
https://github.com/jrcosta/smart-task-ai/issues

### 2. Abra Nova Issue
https://github.com/jrcosta/smart-task-ai/issues/new

**Inclua:**
- Descrição do problema
- Passos para reproduzir
- Mensagens de erro completas
- Logs relevantes
- Sistema operacional
- Versões (Docker, Java, Node)

### 3. Pergunte na Comunidade
https://github.com/jrcosta/smart-task-ai/discussions

### 4. Consulte Outras Páginas da Wiki
- [FAQ](FAQ.md) - Perguntas frequentes
- [Getting Started](Getting-Started.md) - Setup básico
- [Usage Guide](Usage-Guide.md) - Como usar

---

**🔧 Esta página será atualizada com novos problemas e soluções conforme reportados pela comunidade.**

*Última atualização: Outubro 2025 | Versão: 1.0.0*
