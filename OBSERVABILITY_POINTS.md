# 📊 Pontos de Observabilidade - Smart Task Manager

Este documento detalha todos os pontos instrumentados no código para observabilidade.

## 🔍 Varredura do Código

### 1. TaskService (Backend: src/main/java/com/smarttask/service/TaskService.java)

**Operações Rastreadas:**
- ✅ `createTask()` - Criação de tarefas simples
- ✅ `createTaskWithAI()` - Criação de tarefas com análise de IA
- ✅ `getAllTasks()` - Listagem de todas as tarefas
- ✅ `getTasksByStatus()` - Listagem filtrada por status
- ✅ `getTaskById()` - Busca de tarefa específica
- ✅ `updateTask()` - Atualização de tarefas
- ✅ `deleteTask()` - Exclusão de tarefas
- ✅ `getOverdueTasks()` - Listagem de tarefas atrasadas

**Métricas Registradas:**
```java
metricsService.recordTaskCreated(priority)        // Contador de tarefas criadas
metricsService.recordTaskCompleted()              // Contador de tarefas completadas
metricsService.recordTaskDeleted()                // Contador de tarefas deletadas
metricsService.recordTaskDuration(ms, operation)  // Duração das operações
```

**Traces Capturados:**
- Tempo de execução de cada operação
- Parâmetros da requisição (quando `captureParameters=true`)
- Status de sucesso/erro
- Stack trace em caso de exceção

---

### 2. AIService (Backend: src/main/java/com/smarttask/service/AIService.java)

**Operações Rastreadas:**
- ✅ `analyzeTask()` - Análise de tarefa com OpenAI

**Métricas Registradas:**
```java
metricsService.recordAIAnalysis(success)              // Taxa de sucesso/falha
metricsService.recordAIAnalysisDuration(milliseconds) // Duração da análise
```

**Traces Capturados:**
- Tempo de resposta da API OpenAI
- Parâmetros da análise
- Resultado da análise (prioridade, tags, subtarefas)
- Fallback para mock quando API não disponível

**Alertas Importantes:**
- Duração anormal (> 5 segundos)
- Taxa de falhas alta (> 10%)
- Uso de mock (indica API não configurada)

---

### 3. AuthService (Backend: src/main/java/com/smarttask/service/AuthService.java)

**Operações Rastreadas:**
- ✅ `register()` - Registro de novo usuário
- ✅ `login()` - Autenticação de usuário

**Métricas Registradas:**
```java
metricsService.recordAuthenticationSuccess(method)  // Login/registro bem-sucedido
metricsService.recordAuthenticationFailure(reason)  // Falhas de autenticação
```

**Traces Capturados:**
- Tempo de geração de token JWT
- Validação de credenciais
- Erro de autenticação (tipo de exceção)

**Alertas Importantes:**
- Taxa de falhas anormal (possível ataque)
- Tempo de autenticação alto (> 500ms)

---

### 4. WhatsAppService (Backend: src/main/java/com/smarttask/service/WhatsAppService.java)

**Operações Rastreadas:**
- ✅ `sendDailyTaskReminder()` - Lembrete diário
- ✅ `sendOverdueAlert()` - Alerta de tarefas atrasadas
- ✅ `sendCompletionSummary()` - Resumo de conclusão
- ✅ `sendTestMessage()` - Mensagem de teste

**Métricas Registradas:**
```java
metricsService.recordWhatsAppMessage(messageType)  // Por tipo de mensagem
```

**Traces Capturados:**
- Tempo de envio via Twilio
- Número de tarefas no lembrete
- Status de entrega (sucesso/falha)
- Modo de operação (real/simulação)

**Tipos de Mensagem Monitorados:**
- `daily_reminder` - Lembretes diários
- `overdue_alert` - Alertas de atraso
- `completion_summary` - Resumos
- `test_message` - Testes
- `*_failed` - Falhas de envio

---

### 5. Controllers (Instrumentação Automática via Spring Boot)

**Endpoints Monitorados Automaticamente:**
- `POST /api/auth/register` - Registro
- `POST /api/auth/login` - Login
- `GET /api/tasks` - Listar tarefas
- `POST /api/tasks` - Criar tarefa
- `POST /api/tasks/ai` - Criar tarefa com IA
- `GET /api/tasks/{id}` - Obter tarefa
- `PUT /api/tasks/{id}` - Atualizar tarefa
- `DELETE /api/tasks/{id}` - Deletar tarefa
- `GET /api/tasks/status/{status}` - Filtrar por status
- `GET /api/tasks/overdue` - Tarefas atrasadas

**Métricas Automáticas do Spring Boot:**
```
http_server_requests_seconds_count  - Número de requisições
http_server_requests_seconds_sum    - Tempo total de requisições
http_server_requests_seconds_max    - Tempo máximo de requisição
```

**Atributos Capturados:**
- `method` - HTTP method (GET, POST, PUT, DELETE)
- `uri` - Endpoint acessado
- `status` - HTTP status code
- `exception` - Tipo de exceção (se houver)
- `outcome` - Resultado (SUCCESS, CLIENT_ERROR, SERVER_ERROR)

---

## 📈 Métricas Disponíveis

### Tarefas
| Métrica | Tipo | Descrição | Labels |
|---------|------|-----------|--------|
| `tasks_created_total` | Counter | Total de tarefas criadas | `priority` |
| `tasks_completed_total` | Counter | Total de tarefas completadas | - |
| `tasks_deleted_total` | Counter | Total de tarefas deletadas | - |
| `tasks_duration_milliseconds` | Histogram | Duração de operações | `operation` |

### IA
| Métrica | Tipo | Descrição | Labels |
|---------|------|-----------|--------|
| `ai_analysis_requests_total` | Counter | Requisições de análise | `success` |
| `ai_analysis_duration_milliseconds` | Histogram | Duração da análise | - |

### WhatsApp
| Métrica | Tipo | Descrição | Labels |
|---------|------|-----------|--------|
| `whatsapp_messages_sent_total` | Counter | Mensagens enviadas | `type` |

### Autenticação
| Métrica | Tipo | Descrição | Labels |
|---------|------|-----------|--------|
| `auth_success_total` | Counter | Autenticações bem-sucedidas | `method` |
| `auth_failures_total` | Counter | Falhas de autenticação | `reason` |

### JVM (Automáticas)
- `jvm_memory_used_bytes` - Uso de memória
- `jvm_memory_max_bytes` - Memória máxima
- `jvm_gc_pause_seconds` - Pausas de GC
- `jvm_threads_live` - Threads ativas
- `system_cpu_usage` - Uso de CPU
- `process_uptime_seconds` - Tempo de atividade

---

## 🔍 Traces Disponíveis

### Hierarquia de Traces

```
HTTP Request (Spring)
├── TaskService.createTaskWithAI
│   ├── AIService.analyzeTask
│   │   └── OpenAI API Call
│   └── TaskRepository.save (JPA)
└── Response
```

### Atributos em Spans

**Span Customizados:**
- `component` - Classe do serviço
- `method` - Nome do método
- `param.*` - Parâmetros (quando habilitado)
- `result` - Resultado (quando habilitado)
- `error` - Mensagem de erro
- `exception.type` - Tipo de exceção
- `exception.message` - Mensagem da exceção
- `exception.stacktrace` - Stack trace

**Spans HTTP (Automáticos):**
- `http.method` - GET, POST, PUT, DELETE
- `http.url` - URL completa
- `http.status_code` - Status HTTP
- `http.route` - Rota pattern
- `http.user_agent` - User agent

---

## 🎯 Queries Úteis no Prometheus

### Taxa de Criação de Tarefas
```promql
rate(tasks_created_total[5m]) * 60
```

### Tarefas por Prioridade
```promql
sum by (priority) (tasks_created_total)
```

### Duração Média de Operações
```promql
rate(tasks_duration_milliseconds_sum[5m]) / rate(tasks_duration_milliseconds_count[5m])
```

### Taxa de Sucesso de Análises IA
```promql
sum(rate(ai_analysis_requests_total{success="true"}[5m])) / sum(rate(ai_analysis_requests_total[5m]))
```

### Top 5 Endpoints Mais Lentos
```promql
topk(5, histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])))
```

### Taxa de Erros HTTP
```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m]))
```

---

## 🚨 Alertas Recomendados

### Alta Taxa de Falhas de Autenticação
```yaml
alert: HighAuthFailureRate
expr: rate(auth_failures_total[5m]) > 10
```

### Análise IA Muito Lenta
```yaml
alert: SlowAIAnalysis
expr: rate(ai_analysis_duration_milliseconds_sum[5m]) / rate(ai_analysis_duration_milliseconds_count[5m]) > 5000
```

### Taxa de Erro HTTP Alta
```yaml
alert: HighHTTPErrorRate
expr: sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m])) > 0.05
```

---

## 📊 Visualizações no Grafana

### Dashboard Principal
1. **Visão Geral** - Métricas totais
2. **Performance** - Duração de operações
3. **Tráfego** - Requisições por endpoint
4. **Erros** - Taxa de erros e falhas
5. **IA** - Análises e performance
6. **WhatsApp** - Mensagens enviadas
7. **JVM** - Memória, GC, Threads

### Painéis Customizados
- Heatmap de requisições por hora
- Distribuição de prioridades de tarefas
- Taxa de conversão (registro → login)
- Tempo médio de análise IA
- Mensagens WhatsApp por tipo

---

## 🔧 Como Adicionar Novos Pontos de Observabilidade

### 1. Adicionar Trace a um Método

```java
@Traced(value = "MeuServico.meuMetodo", captureParameters = true)
public void meuMetodo(String param) {
    // Seu código
}
```

### 2. Adicionar Métrica Customizada

```java
@Service
@RequiredArgsConstructor
public class MeuServico {
    private final MetricsService metricsService;
    
    public void minhaOperacao() {
        long start = System.currentTimeMillis();
        // Operação
        metricsService.recordTaskDuration(
            System.currentTimeMillis() - start, 
            "minha_operacao"
        );
    }
}
```

### 3. Criar Span Manual

```java
@Service
@RequiredArgsConstructor
public class MeuServico {
    private final Tracer tracer;
    
    public void operacaoComplexa() {
        Span span = tracer.spanBuilder("operacao.complexa").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("custom.attribute", "valor");
            // Operação
            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

---

## 📝 Checklist de Implementação

- [x] Instrumentar TaskService
- [x] Instrumentar AIService
- [x] Instrumentar AuthService
- [x] Instrumentar WhatsAppService
- [x] Configurar Jaeger
- [x] Configurar Prometheus
- [x] Configurar Grafana
- [x] Criar dashboard customizado
- [x] Adicionar métricas customizadas
- [x] Documentar uso
- [x] Criar guia de quickstart
- [x] Adicionar script de validação

---

## 🎓 Próximos Passos

1. **Alertas**: Configurar alertas no Grafana para métricas críticas
2. **Logs**: Correlacionar logs com traces usando trace ID
3. **Sampling**: Configurar sampling em produção (ex: 10%)
4. **Dashboards**: Criar dashboards específicos por equipe
5. **SLI/SLO**: Definir Service Level Indicators e Objectives
6. **Distributed Tracing**: Se adicionar microserviços, propagar contexto
7. **Custom Exporters**: Se necessário, criar exporters customizados

---

**Última atualização**: Outubro de 2024
**Versão**: 1.0.0
