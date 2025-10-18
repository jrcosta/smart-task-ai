# Observabilidade com OpenTelemetry - Smart Task Manager

## 📊 Visão Geral

Este projeto foi instrumentado com **OpenTelemetry** para fornecer observabilidade completa da aplicação, incluindo:

- **Traces (Rastreamento Distribuído)**: Visualização do fluxo de requisições através dos serviços
- **Metrics (Métricas)**: Monitoramento de performance e comportamento da aplicação
- **Logs**: Correlação de logs com traces para debug eficiente

## 🏗️ Arquitetura de Observabilidade

```
┌─────────────────┐
│   Spring Boot   │
│   Application   │
└────────┬────────┘
         │
         ├─────► OpenTelemetry SDK
         │
         ├─────► Jaeger (Traces)
         │       └─► UI: http://localhost:16686
         │
         ├─────► Prometheus (Metrics)
         │       └─► UI: http://localhost:9090
         │
         └─────► Grafana (Dashboard)
                 └─► UI: http://localhost:3001
```

## 🚀 Como Executar Localmente

### Pré-requisitos
- Docker e Docker Compose instalados
- Java 25 (ou 17+)
- Maven

### 1. Iniciar Stack Completo

```bash
# Iniciar todos os serviços (backend, banco, observabilidade)
docker-compose up -d

# Verificar se todos os containers estão rodando
docker-compose ps
```

### 2. Acessar Interfaces

| Serviço | URL | Credenciais | Descrição |
|---------|-----|-------------|-----------|
| **Backend API** | http://localhost:8080/api | - | API REST principal |
| **Jaeger UI** | http://localhost:16686 | - | Visualização de traces |
| **Prometheus** | http://localhost:9090 | - | Métricas brutas |
| **Grafana** | http://localhost:3001 | admin/admin | Dashboard unificado |
| **Actuator** | http://localhost:8080/api/actuator | - | Endpoints de monitoramento |

### 3. Executar Apenas Observabilidade Localmente

Se você já tem o backend rodando localmente fora do Docker:

```bash
# Criar docker-compose-observability.yml apenas com os serviços de observabilidade
docker-compose -f docker-compose-observability.yml up -d
```

## 📈 Métricas Disponíveis

### Métricas Customizadas

#### Tarefas
- `tasks_created_total` - Total de tarefas criadas (por prioridade)
- `tasks_completed_total` - Total de tarefas completadas
- `tasks_deleted_total` - Total de tarefas deletadas
- `tasks_duration_milliseconds` - Duração das operações com tarefas

#### IA
- `ai_analysis_requests_total` - Requisições de análise de IA (sucesso/falha)
- `ai_analysis_duration_milliseconds` - Tempo de processamento da IA

#### WhatsApp
- `whatsapp_messages_sent_total` - Mensagens enviadas (por tipo)

#### Autenticação
- `auth_success_total` - Autenticações bem-sucedidas (por método)
- `auth_failures_total` - Falhas de autenticação (por motivo)

### Métricas do Spring Boot (via Actuator)

- `http_server_requests_seconds` - Requisições HTTP
- `jvm_memory_used_bytes` - Uso de memória JVM
- `jvm_gc_pause_seconds` - Pausas de Garbage Collection
- `system_cpu_usage` - Uso de CPU

## 🔍 Visualizando Traces

### Através do Jaeger

1. Acesse http://localhost:16686
2. Selecione o serviço `smart-task-manager`
3. Clique em "Find Traces"
4. Explore os traces clicando neles para ver detalhes

### Principais Operações Rastreadas

- `TaskService.createTask` - Criação de tarefas
- `TaskService.createTaskWithAI` - Criação com análise de IA
- `TaskService.getAllTasks` - Listagem de tarefas
- `TaskService.updateTask` - Atualização de tarefas
- `TaskService.deleteTask` - Exclusão de tarefas
- `AIService.analyzeTask` - Análise de IA
- `AuthService.login` - Login
- `AuthService.register` - Registro
- `WhatsAppService.*` - Operações WhatsApp

## 📊 Dashboard Grafana

### Configuração Inicial

1. Acesse http://localhost:3001
2. Login: `admin` / `admin`
3. O dashboard "Smart Task Manager - Dashboard de Observabilidade" já estará configurado
4. Navegue até "Dashboards" → "Smart Task Manager"

### Painéis Disponíveis

1. **Visão Geral de Métricas**
   - Total de tarefas criadas
   - Total de tarefas completadas
   - Total de análises de IA
   - Total de mensagens WhatsApp

2. **Performance**
   - Taxa de criação de tarefas
   - Duração média das operações
   - Duração das análises de IA

3. **Autenticação**
   - Sucessos vs Falhas
   - Taxa por método

4. **Distribuição**
   - Tarefas por prioridade (gráfico de pizza)
   - Mensagens WhatsApp por tipo

## 🔧 Configuração

### Variáveis de Ambiente

```bash
# OpenTelemetry
OTEL_TRACES_ENABLED=true
OTEL_METRICS_ENABLED=true
OTEL_EXPORTER_JAEGER_ENDPOINT=http://localhost:14250
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
```

### Desabilitar Observabilidade

Para desabilitar temporariamente:

```yaml
# application.yml
otel:
  traces:
    enabled: false
  metrics:
    enabled: false
```

## 🐛 Debug e Troubleshooting

### Verificar Conexão com Jaeger

```bash
curl http://localhost:16686/api/services
```

### Verificar Métricas do Prometheus

```bash
curl http://localhost:8080/api/actuator/prometheus
```

### Verificar Health do Backend

```bash
curl http://localhost:8080/api/actuator/health
```

### Logs dos Containers

```bash
# Ver logs de todos os containers
docker-compose logs -f

# Ver logs de um container específico
docker-compose logs -f backend
docker-compose logs -f jaeger
docker-compose logs -f prometheus
docker-compose logs -f grafana
```

## 📝 Adicionando Instrumentação Customizada

### Usando a Anotação @Traced

```java
@Service
public class MeuServico {
    
    @Traced(value = "MeuServico.meuMetodo", captureParameters = true)
    public String meuMetodo(String param) {
        // Automaticamente criará um span
        return "resultado";
    }
}
```

### Registrando Métricas Customizadas

```java
@Service
@RequiredArgsConstructor
public class MeuServico {
    
    private final MetricsService metricsService;
    
    public void minhaOperacao() {
        long startTime = System.currentTimeMillis();
        
        // Sua lógica aqui
        
        // Registrar métrica
        metricsService.recordTaskDuration(
            System.currentTimeMillis() - startTime, 
            "minha_operacao"
        );
    }
}
```

### Criando Spans Manualmente

```java
@Service
@RequiredArgsConstructor
public class MeuServico {
    
    private final Tracer tracer;
    
    public void minhaOperacao() {
        Span span = tracer.spanBuilder("MeuServico.minhaOperacao").startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("custom.attribute", "valor");
            
            // Sua lógica aqui
            
            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

## 🔒 Segurança

- **Actuator endpoints** estão expostos publicamente apenas para desenvolvimento
- Em produção, proteja os endpoints `/actuator/**` com autenticação
- Configure firewall para limitar acesso às portas de observabilidade

## 📚 Recursos Adicionais

- [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

## 🎯 Boas Práticas

1. **Não rastreie tudo** - Seja seletivo com spans para evitar overhead
2. **Use atributos significativos** - Adicione contexto útil aos spans
3. **Monitore o overhead** - Observabilidade tem custo, monitore o impacto
4. **Correlacione logs** - Use trace IDs nos logs para debug
5. **Configure alertas** - Use Grafana para criar alertas em métricas críticas

## 🚦 Próximos Passos

- [ ] Adicionar alertas no Grafana para métricas críticas
- [ ] Implementar distributed tracing entre microserviços
- [ ] Adicionar logs estruturados com correlação de trace ID
- [ ] Configurar sampling de traces em produção
- [ ] Implementar custom exporters se necessário
