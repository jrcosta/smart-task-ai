# 🚀 Quick Start - Observabilidade Local

Este guia mostra como executar a stack de observabilidade localmente em minutos.

## Passo 1: Pré-requisitos

- Docker e Docker Compose instalados
- Portas disponíveis: 8080, 5432, 16686, 9090, 3001

## Passo 2: Iniciar Stack Completa

```bash
# Clone o repositório
git clone https://github.com/jrcosta/smart-task-ai.git
cd smart-task-ai

# Inicie todos os serviços
docker-compose up -d

# Aguarde alguns segundos para inicialização
docker-compose ps
```

## Passo 3: Verificar Serviços

Abra seu navegador e acesse:

### 1. **Jaeger UI** (Traces)
- URL: http://localhost:16686
- O que ver: Traces distribuídos de todas as requisições

### 2. **Prometheus** (Métricas)
- URL: http://localhost:9090
- O que ver: Métricas brutas coletadas
- Tente: `tasks_created_total` na barra de busca

### 3. **Grafana** (Dashboard)
- URL: http://localhost:3001
- Login: `admin` / `admin`
- Dashboard: "Smart Task Manager - Dashboard de Observabilidade"
- O que ver: Visualizações consolidadas

### 4. **Backend API**
- URL: http://localhost:8080/api
- Health Check: http://localhost:8080/api/actuator/health
- Métricas: http://localhost:8080/api/actuator/prometheus

## Passo 4: Gerar Dados de Teste

### 4.1 Registrar Usuário

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test@1234",
    "fullName": "Test User"
  }'
```

Guarde o token JWT retornado!

### 4.2 Criar Tarefas

```bash
# Substitua YOUR_TOKEN pelo token recebido
TOKEN="YOUR_TOKEN"

# Criar tarefa simples
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Implementar feature X",
    "description": "Adicionar nova funcionalidade",
    "priority": "HIGH",
    "estimatedHours": 4
  }'

# Criar tarefa com IA
curl -X POST http://localhost:8080/api/tasks/ai \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Refatorar código legado do módulo de autenticação"
  }'
```

### 4.3 Listar Tarefas

```bash
curl http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN"
```

## Passo 5: Explorar a Observabilidade

### No Jaeger (http://localhost:16686)

1. Selecione o serviço: `smart-task-manager`
2. Clique em "Find Traces"
3. Clique em qualquer trace para ver detalhes
4. Explore:
   - Duração de cada operação
   - Spans aninhados (ex: TaskService → AIService)
   - Atributos customizados

### No Grafana (http://localhost:3001)

1. Vá para "Dashboards" → "Browse"
2. Abra "Smart Task Manager - Dashboard de Observabilidade"
3. Observe:
   - Total de tarefas criadas
   - Taxa de criação por minuto
   - Duração das operações
   - Distribuição por prioridade

### No Prometheus (http://localhost:9090)

Tente estas queries:

```promql
# Total de tarefas criadas
sum(tasks_created_total)

# Taxa de criação por minuto
rate(tasks_created_total[5m]) * 60

# Tarefas por prioridade
sum by (priority) (tasks_created_total)

# Duração média das operações
rate(tasks_duration_milliseconds_sum[5m]) / rate(tasks_duration_milliseconds_count[5m])

# Taxa de autenticações bem-sucedidas
rate(auth_success_total[5m]) * 60
```

## Passo 6: Entender os Traces

### Exemplo de Trace: Criar Tarefa com IA

Quando você cria uma tarefa com IA, verá este fluxo no Jaeger:

```
POST /api/tasks/ai
  └─ TaskService.createTaskWithAI
      ├─ AIService.analyzeTask
      │   └─ OpenAI API Call
      └─ Database Save
```

Cada span mostra:
- **Duração**: Quanto tempo levou
- **Atributos**: Parâmetros, prioridade, etc.
- **Status**: Sucesso ou erro
- **Exceções**: Se houver erro

## Troubleshooting

### Serviço não está respondendo

```bash
# Verificar logs
docker-compose logs -f backend

# Reiniciar serviço específico
docker-compose restart backend
```

### Jaeger não mostra traces

1. Verifique se o backend está conectado:
```bash
curl http://localhost:8080/api/actuator/health
```

2. Verifique logs do Jaeger:
```bash
docker-compose logs -f jaeger
```

3. Faça requisições para gerar traces

### Prometheus não coleta métricas

1. Verifique se o endpoint está acessível:
```bash
curl http://localhost:8080/api/actuator/prometheus
```

2. Verifique config do Prometheus:
```bash
docker-compose exec prometheus cat /etc/prometheus/prometheus.yml
```

### Grafana não mostra dados

1. Verifique se os data sources estão configurados:
   - Vá para Configuration → Data Sources
   - Teste conexão com Prometheus e Jaeger

2. Aguarde alguns minutos para coleta de dados

## Modo Desenvolvimento Local

Se você quer rodar o backend localmente (fora do Docker) e apenas os serviços de observabilidade no Docker:

```bash
# Iniciar apenas observabilidade
docker-compose -f docker-compose-observability.yml up -d

# Rodar backend localmente
cd backend
mvn spring-boot:run

# Ou com env específico
./mvnw spring-boot:run -Dspring-boot.run.arguments="--otel.exporter.jaeger.endpoint=http://localhost:14250"
```

## Limpeza

```bash
# Parar todos os serviços
docker-compose down

# Remover volumes (dados serão perdidos)
docker-compose down -v
```

## Próximos Passos

- Explore outros endpoints da API
- Crie alertas customizados no Grafana
- Adicione mais métricas customizadas
- Configure sampling em produção

## Recursos

- [Documentação completa](../OBSERVABILITY.md)
- [Jaeger UI Tutorial](https://www.jaegertracing.io/docs/1.x/getting-started/)
- [Grafana Dashboards](https://grafana.com/docs/grafana/latest/dashboards/)
- [PromQL Queries](https://prometheus.io/docs/prometheus/latest/querying/basics/)
