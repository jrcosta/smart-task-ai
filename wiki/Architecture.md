# 🏗️ Architecture - Smart Task AI

Visão completa da arquitetura do sistema, componentes e fluxos de dados.

## 📊 Visão Geral

Smart Task AI é uma aplicação full-stack moderna com arquitetura em camadas, separação clara de responsabilidades e observabilidade completa.

```
┌─────────────────────────────────────────────────────────────┐
│                        Cliente (Usuário)                     │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (React 19)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Components  │  │    Pages     │  │    Stores    │      │
│  │  (UI/Views)  │  │   (Routes)   │  │  (Zustand)   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                    ┌──────────────┐                         │
│                    │   API Client │                         │
│                    │   (Axios)    │                         │
│                    └──────┬───────┘                         │
└───────────────────────────┼─────────────────────────────────┘
                            │ HTTP/REST (Port 3000)
                            │ JWT Authentication
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Backend (Spring Boot 3.2 + Java 25)            │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              Controllers (REST Endpoints)             │  │
│  │         @RestController + @Traced Annotation          │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                     │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │                   Services                            │  │
│  │   TaskService │ AIService │ WhatsAppService │ ...     │  │
│  │   Business Logic + @Traced + OpenTelemetry            │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                     │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │             Repositories (JPA)                        │  │
│  │   TaskRepository │ UserRepository │ NotificationRepo  │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                     │
│  ┌────────────────────▼─────────────┬───────────────────┐  │
│  │     Security (JWT)     │  Observability              │  │
│  │   JwtTokenProvider     │  OpenTelemetry              │  │
│  │   SecurityConfig       │  Traces + Metrics           │  │
│  └────────────────────────┴─────────────────────────────┘  │
└───────┬──────────────────┬──────────────────┬──────────────┘
        │                  │                  │
        ↓                  ↓                  ↓
┌───────────────┐  ┌──────────────┐  ┌──────────────────┐
│  PostgreSQL   │  │  OpenAI API  │  │   Twilio API     │
│  (Database)   │  │  (GPT-4)     │  │  (WhatsApp)      │
│  Port 5432    │  │  External    │  │  External        │
└───────────────┘  └──────────────┘  └──────────────────┘
        ↓
┌─────────────────────────────────────────────────────────────┐
│                   Observability Stack                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Prometheus  │  │   Grafana    │  │    Jaeger    │      │
│  │  (Metrics)   │  │ (Dashboards) │  │   (Traces)   │      │
│  │  Port 9090   │  │  Port 3001   │  │  Port 16686  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎨 Frontend Architecture

### Tecnologias
- **React 19**: Framework UI com componentes funcionais
- **TypeScript**: Type safety e melhor DX
- **Vite**: Build tool moderno e rápido
- **TailwindCSS**: Utility-first CSS framework
- **TanStack Query**: Server state management
- **Zustand**: Client state management
- **Axios**: HTTP client com interceptors
- **React Router**: Navegação SPA

### Estrutura de Diretórios

```
frontend/src/
├── components/          # Componentes reutilizáveis
│   ├── TaskCard.tsx
│   ├── TaskModal.tsx
│   ├── Layout.tsx
│   └── ...
│
├── pages/               # Páginas/Rotas
│   ├── Dashboard.tsx
│   ├── Tasks.tsx
│   ├── Login.tsx
│   ├── Register.tsx
│   ├── Notifications.tsx
│   └── Settings.tsx
│
├── store/               # Estado global (Zustand)
│   ├── authStore.ts     # Autenticação
│   ├── taskStore.ts     # Tarefas
│   └── notificationStore.ts
│
├── lib/                 # Utilitários
│   ├── api.ts           # Cliente HTTP + JWT interceptor
│   └── utils.ts         # Helpers gerais
│
├── types/               # TypeScript types
│   └── index.ts         # Task, User, Notification, etc.
│
├── App.tsx              # Componente raiz
├── main.tsx             # Entry point
└── index.css            # Estilos globais
```

### Fluxo de Dados (Frontend)

```
Usuário Interage
     ↓
Componente React dispara ação
     ↓
TanStack Query useMutation/useQuery
     ↓
API Client (Axios)
     ↓
Interceptor adiciona JWT token
     ↓
HTTP Request → Backend
     ↓
Response retorna
     ↓
TanStack Query atualiza cache
     ↓
Componente re-renderiza
     ↓
UI atualizada + Toast notification
```

### Autenticação no Frontend

```typescript
// 1. Login
const login = async (email, password) => {
  const response = await authAPI.login(email, password);
  const { token, user } = response.data;
  
  // Armazena token
  localStorage.setItem('token', token);
  
  // Atualiza state
  useAuthStore.setState({ user, token });
};

// 2. Interceptor adiciona token automaticamente
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 3. Logout
const logout = () => {
  localStorage.removeItem('token');
  useAuthStore.setState({ user: null, token: null });
};
```

---

## ⚙️ Backend Architecture

### Tecnologias
- **Java 25**: Linguagem moderna com features recentes
- **Spring Boot 3.2**: Framework enterprise
- **Spring Security**: Autenticação e autorização
- **Spring Data JPA**: Acesso a dados
- **PostgreSQL**: Banco de dados relacional
- **OpenTelemetry**: Observabilidade
- **OpenAI Java SDK**: Integração com GPT-4
- **Twilio SDK**: WhatsApp messaging
- **Maven**: Build e gerenciamento de dependências

### Estrutura de Diretórios

```
backend/src/main/java/com/smarttask/
├── SmartTaskManagerApplication.java  # Main class
│
├── config/                   # Configurações
│   ├── SecurityConfig.java          # Spring Security + JWT
│   ├── OpenApiConfig.java           # Swagger/OpenAPI
│   ├── OpenTelemetryConfig.java     # Observabilidade
│   └── WebConfig.java               # CORS, etc.
│
├── controller/               # REST Controllers
│   ├── AuthController.java          # /auth/*
│   ├── TaskController.java          # /tasks/*
│   ├── AIController.java            # /ai/*
│   └── NotificationController.java  # /notifications/*
│
├── service/                  # Business Logic
│   ├── TaskService.java
│   ├── AIService.java               # OpenAI integration
│   ├── AuthService.java
│   ├── WhatsAppService.java         # Twilio integration
│   └── NotificationService.java
│
├── model/                    # JPA Entities
│   ├── Task.java
│   ├── User.java
│   ├── Notification.java
│   └── SubTask.java
│
├── repository/               # Data Access
│   ├── TaskRepository.java
│   ├── UserRepository.java
│   └── NotificationRepository.java
│
├── dto/                      # Data Transfer Objects
│   ├── request/
│   │   ├── CreateTaskRequest.java
│   │   ├── LoginRequest.java
│   │   └── ...
│   └── response/
│       ├── TaskResponse.java
│       ├── AuthResponse.java
│       └── ...
│
├── security/                 # Segurança
│   ├── JwtTokenProvider.java       # Geração e validação JWT
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
│
├── observability/            # OpenTelemetry
│   ├── TracingAspect.java          # AOP para @Traced
│   ├── MetricsService.java         # Métricas customizadas
│   └── Traced.java                 # Annotation
│
└── exception/                # Exception Handling
    ├── GlobalExceptionHandler.java
    ├── ResourceNotFoundException.java
    └── ...
```

### Padrão de Camadas

```
Controller Layer (HTTP)
    ↓ recebe DTOs
Service Layer (Business Logic)
    ↓ usa Models
Repository Layer (Data Access)
    ↓ persiste em
Database
```

### Exemplo de Fluxo Completo

```java
// 1. Controller recebe request
@PostMapping("/tasks")
public ResponseEntity<TaskResponse> createTask(
    @RequestBody CreateTaskRequest request,
    @AuthenticationPrincipal User user
) {
    Task task = taskService.createTask(request, user);
    return ResponseEntity.ok(TaskResponse.from(task));
}

// 2. Service processa lógica
@Service
public class TaskService {
    @Traced("TaskService.createTask")  // ← OpenTelemetry span
    public Task createTask(CreateTaskRequest req, User user) {
        // Cria entity
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setUser(user);
        
        // Analisa com IA (se configurado)
        if (aiService.isConfigured()) {
            AIAnalysis analysis = aiService.analyzeTask(task);
            task.setPriority(analysis.getPriority());
            task.setTags(analysis.getTags());
        }
        
        // Salva no banco
        Task saved = taskRepository.save(task);
        
        // Registra métrica
        metricsService.recordTaskCreated(user.getId());
        
        return saved;
    }
}

// 3. Repository persiste
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByStatus(TaskStatus status);
}
```

---

## 🔐 Security Architecture

### JWT Flow

```
1. User Login
   POST /auth/login
   { email, password }
        ↓
2. Backend valida credenciais
   BCrypt compare password
        ↓
3. Gera JWT token
   JwtTokenProvider.generateToken(user)
   Expiration: 7 dias
        ↓
4. Retorna token
   { token: "eyJhbGciOiJIUzI1Ni...", user: {...} }
        ↓
5. Frontend armazena
   localStorage.setItem('token', token)
        ↓
6. Próximas requests incluem token
   Authorization: Bearer eyJhbGciOiJIUzI1Ni...
        ↓
7. Backend valida token
   JwtAuthenticationFilter
   JwtTokenProvider.validateToken()
        ↓
8. Extrai usuário
   SecurityContext.setAuthentication()
        ↓
9. Processa request
   Controller executa com @AuthenticationPrincipal User
```

### Endpoints Protegidos

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api/docs/**").permitAll()
                
                // Protegidos (requer autenticação)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}
```

---

## 🗄️ Database Architecture

### Schema Principal

```sql
-- Users (autenticação)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,  -- BCrypt hash
    telefone VARCHAR(20),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Tasks (tarefas)
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    status VARCHAR(50) NOT NULL,  -- PENDENTE, EM_PROGRESSO, CONCLUIDA
    prioridade VARCHAR(50),       -- BAIXA, MEDIA, ALTA
    prazo TIMESTAMP,
    estimativa_horas INT,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Task Tags (relacionamento many-to-many)
CREATE TABLE task_tags (
    task_id BIGINT REFERENCES tasks(id),
    tag VARCHAR(100),
    PRIMARY KEY (task_id, tag)
);

-- Subtasks (tarefas filhas)
CREATE TABLE subtasks (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    concluida BOOLEAN DEFAULT FALSE,
    parent_task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
    ordem INT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Notifications (configurações)
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    tipo VARCHAR(50) NOT NULL,
    horario TIME,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- API Keys (credenciais criptografadas)
CREATE TABLE api_keys (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    provider VARCHAR(50) NOT NULL,  -- OPENAI, TWILIO
    encrypted_key TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, provider)
);
```

### Índices para Performance

```sql
-- Índices principais
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_prazo ON tasks(prazo);
CREATE INDEX idx_tasks_prioridade ON tasks(prioridade);
CREATE INDEX idx_subtasks_parent ON subtasks(parent_task_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
```

### Relacionamentos

```
User 1───N Task
         │
         └───N SubTask
         
User 1───N Notification

User 1───N APIKey

Task N───N Tags (via task_tags)
```

---

## 🤖 AI Integration Architecture

### OpenAI GPT-4 Integration

```java
@Service
public class AIService {
    
    @Traced("AIService.analyzeTask")
    public AIAnalysis analyzeTask(Task task) {
        // 1. Monta prompt
        String prompt = buildPrompt(task);
        
        // 2. Chama OpenAI API
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model("gpt-4")
            .messages(List.of(
                new Message("system", SYSTEM_PROMPT),
                new Message("user", prompt)
            ))
            .temperature(0.7)
            .maxTokens(500)
            .build();
            
        // 3. Obtém resposta
        ChatCompletionResult result = openAI.createChatCompletion(request);
        String response = result.getChoices().get(0).getMessage().getContent();
        
        // 4. Parse resposta
        AIAnalysis analysis = parseResponse(response);
        
        // 5. Registra métrica
        metricsService.recordAIAnalysis(analysis.getDurationMs());
        
        return analysis;
    }
    
    private String buildPrompt(Task task) {
        return String.format("""
            Analise esta tarefa e retorne:
            
            PRIORIDADE: (BAIXA|MEDIA|ALTA)
            TAGS: tag1, tag2, tag3
            SUBTAREFAS:
            1. Subtarefa 1
            2. Subtarefa 2
            ANÁLISE: Explicação breve
            HORAS: Estimativa numérica
            
            Tarefa:
            Título: %s
            Descrição: %s
            """, task.getTitulo(), task.getDescricao());
    }
}
```

### Fallback Strategy

```
OpenAI configurado?
    ├─ SIM → Usa GPT-4
    │         ↓
    │    Sucesso?
    │         ├─ SIM → Retorna análise real
    │         └─ NÃO → Fallback para mock
    │
    └─ NÃO → Usa análise mock
              (regex + keywords)
```

---

## 📱 WhatsApp Integration Architecture

### Twilio Flow

```java
@Service
public class WhatsAppService {
    
    @Traced("WhatsAppService.sendMessage")
    public void sendMessage(String to, String message) {
        // 1. Valida credenciais
        if (!isConfigured()) {
            log.warn("Twilio not configured, simulating message");
            return;
        }
        
        // 2. Envia via Twilio
        Message twilioMessage = Message.creator(
            new PhoneNumber("whatsapp:" + to),
            new PhoneNumber(twilioWhatsAppNumber),
            message
        ).create();
        
        // 3. Log e métrica
        log.info("WhatsApp sent: sid={}", twilioMessage.getSid());
        metricsService.recordWhatsAppMessage(true);
    }
    
    @Scheduled(cron = "0 0 9 * * *")  // 9 AM diariamente
    public void sendDailyReminders() {
        List<User> users = userRepository.findWithActiveNotifications();
        
        for (User user : users) {
            String summary = buildDailySummary(user);
            sendMessage(user.getTelefone(), summary);
        }
    }
}
```

---

## 📊 Observability Architecture

### OpenTelemetry Integration

```java
// 1. Annotation customizada
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traced {
    String value();  // Nome do span
}

// 2. AOP Aspect captura anotação
@Aspect
@Component
public class TracingAspect {
    
    @Around("@annotation(traced)")
    public Object trace(ProceedingJoinPoint joinPoint, Traced traced) {
        Span span = tracer.spanBuilder(traced.value()).startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            // Adiciona atributos
            span.setAttribute("method", joinPoint.getSignature().getName());
            
            // Executa método
            Object result = joinPoint.proceed();
            
            span.setStatus(StatusCode.OK);
            return result;
            
        } catch (Throwable e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
            throw e;
            
        } finally {
            span.end();
        }
    }
}

// 3. Uso
@Service
public class TaskService {
    
    @Traced("TaskService.createTask")  // ← Cria span automaticamente
    public Task createTask(CreateTaskRequest req, User user) {
        // ...
    }
}
```

### Métricas Customizadas

```java
@Component
public class MetricsService {
    
    private final Counter taskCounter;
    private final Histogram requestDuration;
    
    public MetricsService(MeterRegistry registry) {
        this.taskCounter = Counter.builder("tasks.created")
            .description("Total tasks created")
            .tag("app", "smart-task")
            .register(registry);
            
        this.requestDuration = Histogram.builder("ai.analysis.duration")
            .description("AI analysis duration in ms")
            .register(registry);
    }
    
    public void recordTaskCreated(Long userId) {
        taskCounter.increment();
    }
    
    public void recordAIAnalysis(long durationMs) {
        requestDuration.record(durationMs);
    }
}
```

### Stack de Observabilidade

```
Application
    ↓ emite
OpenTelemetry Collector
    ├─→ Traces → Jaeger
    ├─→ Metrics → Prometheus → Grafana
    └─→ Logs → (futuro: Loki)
```

---

## 🚀 Deployment Architecture

### Docker Compose Structure

```yaml
services:
  frontend:
    build: ./frontend
    ports: ["3000:3000"]
    depends_on: [backend]
    
  backend:
    build: ./backend
    ports: ["8080:8080"]
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/smarttask
    depends_on: [postgres]
    
  postgres:
    image: postgres:15
    ports: ["5432:5432"]
    volumes: [postgres-data:/var/lib/postgresql/data]
    
  # Profile: observability
  prometheus:
    image: prom/prometheus
    profiles: [observability]
    
  grafana:
    image: grafana/grafana
    profiles: [observability]
    
  jaeger:
    image: jaegertracing/all-in-one
    profiles: [observability]
```

### Production Considerations

```
Load Balancer (Nginx)
    ↓
Frontend (multiple instances)
    ↓
Backend (multiple instances)
    ├─→ PostgreSQL (master-replica)
    ├─→ Redis (cache) - futuro
    └─→ Message Queue (RabbitMQ) - futuro
```

---

## 🔄 Data Flow Examples

### Criar Tarefa com IA

```
1. User: Clica "Nova Tarefa" e preenche formulário
        ↓
2. Frontend: useMutation(taskAPI.create)
        ↓
3. Axios: POST /tasks com JWT header
        ↓
4. Backend: JwtFilter valida token
        ↓
5. TaskController: @PostMapping("/tasks")
        ↓
6. TaskService.createTask() com @Traced
        ↓ (OpenTelemetry inicia span)
7. AIService.analyzeTask()
        ↓
8. OpenAI API: Análise GPT-4
        ↓
9. AIService: Parse resposta
        ↓
10. TaskService: Aplica sugestões + salva
        ↓
11. TaskRepository.save()
        ↓
12. PostgreSQL: INSERT INTO tasks
        ↓
13. MetricsService: Registra métrica
        ↓
14. Response: TaskResponse DTO
        ↓
15. Frontend: TanStack Query atualiza cache
        ↓
16. UI: Re-renderiza + Toast success
        ↓
(Parallel) Jaeger: Registra trace completo
(Parallel) Prometheus: Coleta métricas
```

---

## 📚 Design Patterns Utilizados

### Backend
- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separação de entities e DTOs
- **Repository Pattern**: Spring Data JPA
- **Dependency Injection**: Spring IoC
- **AOP**: Tracing com AspectJ
- **Builder Pattern**: DTOs e requests
- **Factory Pattern**: Response builders
- **Strategy Pattern**: Fallback de IA

### Frontend
- **Component-Based**: React components
- **Container/Presenter**: Smart/Dumb components
- **Hooks Pattern**: Custom hooks
- **Composition**: Component composition
- **Observer**: TanStack Query observers

---

## 🎯 Princípios de Design

- **SOLID**: Principalmente SRP e DIP
- **DRY**: Reutilização de código
- **KISS**: Simplicidade
- **YAGNI**: Não over-engineering
- **Clean Code**: Nomes descritivos, funções pequenas
- **12-Factor App**: Config via env vars, stateless, logs

---

**📖 Mais Recursos:**
- [Getting Started](Getting-Started.md)
- [Development Guide](Development-Guide.md) (em breve)
- [API Documentation](API-Documentation.md) (em breve)
- [Deployment](Deployment.md) (em breve)

*Última atualização: Outubro 2025 | Versão: 1.0.0*
