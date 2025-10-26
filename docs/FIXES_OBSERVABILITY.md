# 🔧 Correções de Observabilidade e Erro 502

## 📋 Resumo do Problema

O frontend estava apresentando erro 502 ao tentar cadastrar usuário, e as ferramentas de observabilidade (Grafana, Prometheus, Jaeger) não estavam recebendo nenhuma métrica ou trace do backend.

## 🔍 Problemas Identificados

### 1. Docker Compose de Observabilidade com Paths Incorretos
**Problema:** O arquivo `infrastructure/docker-compose-observability.yml` estava tentando montar volumes usando paths relativos `./observability/`, mas o diretório `observability` está na raiz do projeto, não dentro de `infrastructure/`.

**Sintoma:**
```
Error: cannot create subdirectories in "/var/lib/docker/overlay2/.../etc/prometheus/prometheus.yml": not a directory
```

**Solução:** Corrigir os paths nos volumes do Docker Compose:
- `./observability/prometheus-local.yml` → `../observability/prometheus-local.yml`
- `./observability/grafana/dashboards` → `../observability/grafana/dashboards`
- `./observability/grafana/datasources` → `../observability/grafana/datasources`

### 2. Conflito de Configuração OpenTelemetry
**Problema:** O backend tinha uma configuração manual do OpenTelemetry SDK que estava conflitando com a auto-configuração nativa do Spring Boot 3.5.

**Sintoma:**
```
Correct the classpath of your application so that it contains a single, compatible version of com.smarttask.config.OpenTelemetryConfig
```

**Causa Raiz:** Spring Boot 3.5 possui suporte nativo para OpenTelemetry através do Micrometer Tracing. A classe `OpenTelemetryConfig` estava tentando inicializar manualmente o SDK usando `AutoConfiguredOpenTelemetrySdk.initialize()`, o que criava uma segunda instância conflitante.

**Solução:** 
- Migrar para Micrometer Tracing Bridge for OpenTelemetry
- Remover inicialização manual do SDK
- Manter apenas o bean `Tracer` que consome o `OpenTelemetry` auto-configurado pelo Spring Boot

### 3. Dependências OpenTelemetry Incompatíveis
**Problema:** O projeto especificava OpenTelemetry 1.32.0, mas Spring Boot 3.5.2 requer OpenTelemetry 1.49.0+.

**Sintoma:**
```
NoSuchMethodError: 'io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporterBuilder.setConnectTimeout(java.time.Duration)'
```

**Solução:**
- Remover versões fixas das propriedades (`opentelemetry.version`)
- Deixar Spring Boot gerenciar as versões através do BOM (Bill of Materials)
- Adicionar `micrometer-tracing-bridge-otel` como dependência principal

### 4. Classes Configuration com Modificador `final`
**Problema:** Spring Boot 3.x/Spring Framework 6.x não permite classes `@Configuration` com modificador `final`, pois precisa criar proxies CGLIB para AOP.

**Sintoma:**
```
Configuration problem: @Configuration class 'OpenApiConfig' may not be final. Remove the final modifier to continue.
```

**Solução:** Remover `final` de todas as classes:
- `OpenTelemetryConfig`
- `OpenApiConfig`
- `SecurityConfig`
- `CustomUserDetailsService`
- `JwtAuthenticationFilter`
- `JwtTokenProvider`

## 🛠️ Alterações Realizadas

### 1. Docker Compose (`infrastructure/docker-compose-observability.yml`)
```yaml
# ANTES
volumes:
  - ./observability/prometheus-local.yml:/etc/prometheus/prometheus.yml

# DEPOIS
volumes:
  - ../observability/prometheus-local.yml:/etc/prometheus/prometheus.yml
```

### 2. Dependências Maven (`backend/pom.xml`)
```xml
<!-- REMOVIDO: Dependências manuais OpenTelemetry -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
    <version>${opentelemetry.version}</version>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-jaeger</artifactId>
    <version>${opentelemetry.version}</version>
</dependency>

<!-- ADICIONADO: Micrometer Tracing Bridge -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```

### 3. OpenTelemetryConfig (`backend/src/main/java/com/smarttask/config/OpenTelemetryConfig.java`)
```java
// ANTES: Inicialização manual
@Bean
public OpenTelemetry openTelemetry() {
    System.setProperty(OTEL_SERVICE_NAME, applicationName);
    System.setProperty(OTEL_TRACES_EXPORTER, "jaeger,otlp");
    return AutoConfiguredOpenTelemetrySdk.initialize().getOpenTelemetrySdk();
}

// DEPOIS: Apenas configuração do Tracer
@Bean
public Tracer tracer(final OpenTelemetry openTelemetry) {
    log.info("Configurando Tracer OpenTelemetry para o servico: {}", applicationName);
    return openTelemetry.getTracer(applicationName, TRACER_VERSION);
}
```

### 4. Application.yml (`backend/src/main/resources/application.yml`)
```yaml
# ANTES
otel:
  traces:
    enabled: true
  exporter:
    jaeger:
      endpoint: ${OTEL_EXPORTER_JAEGER_ENDPOINT:http://localhost:14250}
    otlp:
      endpoint: ${OTEL_EXPORTER_OTLP_ENDPOINT:http://localhost:4317}

# DEPOIS
management:
  otlp:
    tracing:
      endpoint: ${OTEL_EXPORTER_OTLP_ENDPOINT:http://localhost:4318/v1/traces}
  tracing:
    sampling:
      probability: 1.0
```

## ✅ Validação da Solução

### Teste 1: User Registration (Erro 502 Corrigido)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"Test123!","fullName":"Test User"}'
```
**Resultado:** ✅ HTTP 200 - Token JWT retornado com sucesso

### Teste 2: Jaeger Traces
```bash
curl -s "http://localhost:16686/api/traces?service=smart-task-manager&limit=20" | jq '.data | length'
```
**Resultado:** ✅ 10 traces capturados

### Teste 3: Prometheus Metrics
```bash
curl -s http://localhost:9090/api/v1/targets | jq '.data.activeTargets[].health'
```
**Resultado:** ✅ "up" - Scraping bem-sucedido

### Teste 4: Métricas HTTP
```bash
curl -s http://localhost:8080/api/actuator/prometheus | grep "http_server_requests_seconds_count.*register"
```
**Resultado:** ✅ Métricas de requisição capturadas

## 🚀 Como Executar

### 1. Iniciar Stack de Observabilidade
```bash
cd infrastructure
docker compose -f docker-compose-observability.yml up -d
```

### 2. Iniciar Backend
```bash
cd backend
export JAVA_HOME=/usr/lib/jvm/temurin-25-jdk-amd64
mvn spring-boot:run
```

### 3. Acessar Ferramentas
- **Jaeger UI:** http://localhost:16686
- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3001 (admin/admin)
- **API Swagger:** http://localhost:8080/api/swagger-ui.html
- **Actuator:** http://localhost:8080/api/actuator

## 📊 Métricas e Traces Disponíveis

### Métricas Customizadas (via MetricsService)
- `tasks_created_total{priority}` - Total de tarefas criadas
- `tasks_completed_total` - Total de tarefas completadas
- `tasks_deleted_total` - Total de tarefas deletadas
- `auth_success_total{method}` - Autenticações bem-sucedidas
- `auth_failures_total{reason}` - Falhas de autenticação
- `whatsapp_messages_sent_total{type}` - Mensagens WhatsApp enviadas
- `ai_analysis_requests_total{success}` - Requisições de análise IA

### Métricas Automáticas (Spring Boot Actuator)
- `http_server_requests_seconds_*` - Métricas HTTP por endpoint
- `jvm_memory_*` - Uso de memória JVM
- `jvm_gc_*` - Garbage Collection
- `system_cpu_usage` - Uso de CPU

### Traces (via OpenTelemetry + Micrometer)
- Spans automáticos para todas as requisições HTTP
- Spans customizados via anotação `@Traced`
- Propagação de contexto entre serviços
- Instrumentação de chamadas JPA/Hibernate

## 🔑 Pontos Importantes

1. **Compatibilidade de Versões:** Spring Boot 3.5.2 requer OpenTelemetry 1.49.0+. Não especificar versões manualmente permite que o BOM do Spring Boot gerencie isso automaticamente.

2. **Micrometer Tracing Bridge:** É a forma recomendada de integrar OpenTelemetry com Spring Boot 3.x, substituindo a configuração manual do SDK.

3. **OTLP HTTP Endpoint:** Spring Boot 3.5 usa por padrão o endpoint HTTP do OTLP na porta 4318 (`/v1/traces`), diferente do endpoint gRPC na porta 4317.

4. **Classes Non-Final:** Spring 6.x requer que classes com proxies AOP (como `@Configuration`, `@Service`, `@Component`) não sejam `final`.

5. **Observabilidade Out-of-the-Box:** Com as configurações corretas, Spring Boot automaticamente:
   - Instrumenta todas as requisições HTTP
   - Exporta métricas via Actuator
   - Propaga trace context entre componentes
   - Integra com Prometheus e Jaeger

## 📚 Referências

- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html)
- [Micrometer Tracing](https://micrometer.io/docs/tracing)
- [OpenTelemetry Java](https://opentelemetry.io/docs/languages/java/)
- [Spring Boot 3.5 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.5-Release-Notes)

---

**Data:** Outubro 2025  
**Autor:** GitHub Copilot  
**Versão:** Smart Task AI v1.0.0
