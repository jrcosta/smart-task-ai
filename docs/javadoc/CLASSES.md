# 📚 Documentação JavaDoc - Referência Completa

Este documento serve como referência de todas as classes documentadas no projeto Smart Task Manager.

## 🏗️ Estrutura de Pacotes

```
com.smarttask/
├── SmartTaskManagerApplication       (Classe Principal)
├── config/                           (Configurações)
├── controller/                       (REST Controllers)
├── service/                          (Serviços de Negócio)
├── model/                            (Entidades JPA)
├── repository/                       (Acesso a Dados)
├── security/                         (Autenticação & Autorização)
├── dto/                              (Data Transfer Objects)
├── exception/                        (Tratamento de Exceções)
└── observability/                    (Rastreamento & Métricas)
```

---

## 🎯 Classe Principal

### SmartTaskManagerApplication.java

**Localização:** `backend/src/main/java/com/smarttask/SmartTaskManagerApplication.java`

**Propósito:** Ponto de entrada (entry point) da aplicação Spring Boot.

**Anotações:**
- `@SpringBootApplication` - Ativa autoconfiguração Spring Boot
- `@EnableJpaAuditing` - Rastreamento automático de criação/modificação
- `@EnableScheduling` - Suporte a tarefas agendadas

**Uso:**
```bash
cd backend
mvn spring-boot:run
```

**Métodos Públicos:**
- `main(String[] args)` - Inicia a aplicação

---

## 📦 Entidades (Models)

### Task.java

**Localização:** `backend/src/main/java/com/smarttask/model/Task.java`

**Herança:** JPA Entity

**Propriedades Principais:**
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | Identificador único (PK) |
| `title` | String | Título da tarefa (3-200 caracteres) |
| `description` | String | Descrição detalhada (TEXT) |
| `status` | TaskStatus | TODO, IN_PROGRESS, COMPLETED, CANCELLED |
| `priority` | TaskPriority | LOW, MEDIUM, HIGH, URGENT |
| `dueDate` | LocalDateTime | Data de vencimento |
| `completedAt` | LocalDateTime | Data de conclusão |
| `estimatedHours` | Integer | Horas estimadas |
| `actualHours` | Integer | Horas reais gastas |
| `tags` | Set<String> | Categorias (ElementCollection) |
| `aiAnalysis` | String | Análise de IA (TEXT) |
| `aiSuggestedPriority` | Boolean | Sugestão de IA foi aplicada? |
| `user` | User | Proprietário (FK Many-to-One) |
| `parentTask` | Task | Tarefa pai (FK Many-to-One) |
| `subtasks` | Set<Task> | Subtarefas (One-to-Many) |
| `createdAt` | LocalDateTime | Data de criação (auditoria) |
| `updatedAt` | LocalDateTime | Data de modificação (auditoria) |

**Enumerações:**
```java
public enum TaskStatus {
    TODO,           // Tarefa não iniciada
    IN_PROGRESS,    // Tarefa em andamento
    COMPLETED,      // Tarefa concluída
    CANCELLED       // Tarefa cancelada
}

public enum TaskPriority {
    LOW,    // Baixa prioridade
    MEDIUM, // Prioridade média
    HIGH,   // Alta prioridade
    URGENT  // Muito urgente
}
```

**Relacionamentos:**
- `User` (Many-to-One) - Usuário proprietário
- `Task` (Many-to-One) - Tarefa pai
- `Set<Task>` (One-to-Many) - Subtarefas

---

### User.java

**Localização:** `backend/src/main/java/com/smarttask/model/User.java`

**Herança:** JPA Entity

**Propriedades Principais:**
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | Identificador único (PK) |
| `username` | String | Nome único (3-50 caracteres) |
| `email` | String | Email único e validado |
| `password` | String | Senha criptografada (BCrypt) |
| `fullName` | String | Nome completo |
| `avatarUrl` | String | URL do avatar |
| `active` | Boolean | Conta ativa? (padrão: true) |
| `roles` | Set<String> | Papéis (ROLE_USER, ROLE_ADMIN) |
| `tasks` | Set<Task> | Tarefas do usuário |
| `createdAt` | LocalDateTime | Data de criação (auditoria) |
| `updatedAt` | LocalDateTime | Data de modificação (auditoria) |

**Relacionamentos:**
- `Set<Task>` (One-to-Many) - Tarefas do usuário

---

### NotificationPreference.java

**Localização:** `backend/src/main/java/com/smarttask/model/NotificationPreference.java`

**Propósito:** Preferências de notificação do usuário.

**Campos Principais:**
- `id` - Identificador único
- `user` - Usuário (FK)
- `whatsappNumber` - Número WhatsApp
- `enableReminders` - Ativar lembretes?
- `reminderTime` - Hora dos lembretes (HH:mm)
- `reminderDays` - Dias da semana para lembretes

---

### PomodoroSession.java

**Localização:** `backend/src/main/java/com/smarttask/model/PomodoroSession.java`

**Propósito:** Representação de uma sessão Pomodoro.

**Campos Principais:**
- `id` - Identificador único
- `user` - Usuário (FK)
- `task` - Tarefa associada (FK)
- `startTime` - Hora de início
- `endTime` - Hora de término
- `duration` - Duração em minutos

---

## 🎮 Controladores (Controllers)

### TaskController.java

**Localização:** `backend/src/main/java/com/smarttask/controller/TaskController.java`

**Base URL:** `/tasks`

**Autenticação:** Requer JWT Bearer Token em todos os endpoints

#### Endpoints

| Método | Caminho | Descrição | Autenticação |
|--------|---------|-----------|--------------|
| `POST` | `/tasks` | Criar nova tarefa | ✅ Requerida |
| `POST` | `/tasks/ai` | Criar com análise de IA | ✅ Requerida |
| `GET` | `/tasks` | Listar todas as tarefas | ✅ Requerida |
| `GET` | `/tasks/status/{status}` | Filtrar por status | ✅ Requerida |
| `GET` | `/tasks/{id}` | Obter tarefa específica | ✅ Requerida |
| `PUT` | `/tasks/{id}` | Atualizar tarefa | ✅ Requerida |
| `DELETE` | `/tasks/{id}` | Deletar tarefa | ✅ Requerida |

#### Métodos Principais

```java
/**
 * Cria uma nova tarefa.
 */
ResponseEntity<TaskResponse> createTask(
    TaskRequest request,
    UserPrincipal currentUser
)

/**
 * Cria uma nova tarefa com análise de IA.
 */
ResponseEntity<TaskResponse> createTaskWithAI(
    TaskRequest request,
    UserPrincipal currentUser
)

/**
 * Lista todas as tarefas do usuário.
 */
ResponseEntity<List<TaskResponse>> getAllTasks(
    UserPrincipal currentUser
)

/**
 * Lista tarefas filtradas por status.
 */
ResponseEntity<List<TaskResponse>> getTasksByStatus(
    TaskStatus status,
    UserPrincipal currentUser
)
```

---

### AuthController.java

**Localização:** `backend/src/main/java/com/smarttask/controller/AuthController.java`

**Base URL:** `/auth`

#### Endpoints

| Método | Caminho | Descrição | Autenticação |
|--------|---------|-----------|--------------|
| `POST` | `/auth/register` | Registrar novo usuário | ❌ Não |
| `POST` | `/auth/login` | Fazer login e obter JWT | ❌ Não |
| `GET` | `/auth/me` | Obter dados do usuário | ✅ Requerida |

---

### AIController.java

**Localização:** `backend/src/main/java/com/smarttask/controller/AIController.java`

**Base URL:** `/ai`

#### Endpoints

| Método | Caminho | Descrição | Autenticação |
|--------|---------|-----------|--------------|
| `POST` | `/ai/analyze` | Analisar tarefa com IA | ✅ Requerida |
| `POST` | `/ai/suggest-priority` | Sugerir prioridade | ✅ Requerida |
| `POST` | `/ai/generate-subtasks` | Gerar subtarefas | ✅ Requerida |

---

### NotificationController.java

**Localização:** `backend/src/main/java/com/smarttask/controller/NotificationController.java`

**Base URL:** `/notifications`

#### Endpoints

| Método | Caminho | Descrição | Autenticação |
|--------|---------|-----------|--------------|
| `GET` | `/notifications` | Listar notificações | ✅ Requerida |
| `POST` | `/notifications/preferences` | Atualizar preferências | ✅ Requerida |
| `POST` | `/notifications/test` | Enviar notificação de teste | ✅ Requerida |

---

## 🔧 Serviços (Services)

Todos os métodos de serviço utilizam `@Traced` para observabilidade automática.

### TaskService.java

**Localização:** `backend/src/main/java/com/smarttask/service/TaskService.java`

#### Métodos Principais

```java
/**
 * Cria uma nova tarefa.
 * @param request Dados da tarefa
 * @param currentUser Usuário autenticado
 * @return TaskResponse com dados da tarefa criada
 */
@Traced("TaskService.createTask")
TaskResponse createTask(TaskRequest request, UserPrincipal currentUser)

/**
 * Cria uma tarefa com análise de IA.
 */
@Traced("TaskService.createTaskWithAI")
TaskResponse createTaskWithAI(TaskRequest request, UserPrincipal currentUser)

/**
 * Lista todas as tarefas do usuário.
 */
@Traced("TaskService.getAllTasks")
List<TaskResponse> getAllTasks(UserPrincipal currentUser)

/**
 * Obtém uma tarefa específica.
 */
@Traced("TaskService.getTaskById")
TaskResponse getTaskById(Long id, UserPrincipal currentUser)

/**
 * Atualiza uma tarefa.
 */
@Traced("TaskService.updateTask")
TaskResponse updateTask(Long id, TaskRequest request, UserPrincipal currentUser)

/**
 * Deleta uma tarefa.
 */
@Traced("TaskService.deleteTask")
void deleteTask(Long id, UserPrincipal currentUser)

/**
 * Filtra tarefas por status.
 */
@Traced("TaskService.getTasksByStatus")
List<TaskResponse> getTasksByStatus(TaskStatus status, UserPrincipal currentUser)
```

---

### AuthService.java

**Localização:** `backend/src/main/java/com/smarttask/service/AuthService.java`

#### Métodos Principais

```java
/**
 * Registra um novo usuário.
 */
@Traced("AuthService.register")
AuthResponse register(RegisterRequest request)

/**
 * Autentica um usuário e gera JWT.
 */
@Traced("AuthService.login")
AuthResponse login(AuthRequest request)

/**
 * Valida um token JWT.
 */
boolean validateToken(String token)

/**
 * Renovaa um token expirado.
 */
String refreshToken(String expiredToken)
```

---

### AIService.java

**Localização:** `backend/src/main/java/com/smarttask/service/AIService.java`

**Integração:** OpenAI GPT-4

**Fallback:** Retorna análise mock se `OPENAI_API_KEY` não estiver configurado

#### Métodos Principais

```java
/**
 * Realiza análise completa de uma tarefa com IA.
 */
@Traced("AIService.analyzeTask")
AIAnalysisResponse analyzeTask(String taskTitle, String taskDescription)

/**
 * Sugere prioridade automática para uma tarefa.
 */
@Traced("AIService.suggestPriority")
TaskPriority suggestPriority(String taskTitle, String taskDescription)

/**
 * Gera subtarefas recomendadas.
 */
@Traced("AIService.generateSubtasks")
List<String> generateSubtasks(String taskTitle, String taskDescription)

/**
 * Estima horas necessárias.
 */
@Traced("AIService.estimateHours")
Integer estimateHours(String taskTitle, String taskDescription)
```

---

### WhatsAppService.java

**Localização:** `backend/src/main/java/com/smarttask/service/WhatsAppService.java`

**Integração:** Twilio WhatsApp API

**Fallback:** Simula envio se credenciais não estiverem configuradas

#### Métodos Principais

```java
/**
 * Envia uma notificação WhatsApp.
 */
@Traced("WhatsAppService.sendNotification")
void sendNotification(String phoneNumber, String message)

/**
 * Envia lembrete diário de tarefas.
 */
@Traced("WhatsAppService.sendDailyReminder")
void sendDailyReminder(User user)

/**
 * Alerta sobre tarefas atrasadas.
 */
@Traced("WhatsAppService.sendLateTaskAlert")
void sendLateTaskAlert(User user, List<Task> lateTasks)
```

---

### NotificationService.java

**Localização:** `backend/src/main/java/com/smarttask/service/NotificationService.java`

#### Métodos Principais

```java
/**
 * Cria uma nova notificação.
 */
void createNotification(User user, String message)

/**
 * Lista notificações do usuário.
 */
List<Notification> getUserNotifications(User user)

/**
 * Marca notificação como lida.
 */
void markAsRead(Long notificationId)
```

---

## 📊 Repositórios (Repositories)

Todos herdam de `CrudRepository<Entity, ID>` do Spring Data JPA.

### TaskRepository.java

```java
List<Task> findByUserId(Long userId)
List<Task> findByUserIdAndStatus(Long userId, TaskStatus status)
List<Task> findByUserIdAndPriority(Long userId, TaskPriority priority)
List<Task> findByParentTaskId(Long parentTaskId)
```

---

### UserRepository.java

```java
Optional<User> findByUsername(String username)
Optional<User> findByEmail(String email)
boolean existsByUsername(String username)
boolean existsByEmail(String email)
```

---

### NotificationPreferenceRepository.java

```java
Optional<NotificationPreference> findByUserId(Long userId)
```

---

## 🛡️ Segurança (Security)

### JwtTokenProvider.java

**Propósito:** Provedor de tokens JWT.

#### Métodos Principais

```java
/**
 * Gera um novo token JWT para um usuário.
 */
String generateToken(UserDetails userDetails)

/**
 * Extrai username do token.
 */
String getUsernameFromToken(String token)

/**
 * Valida um token JWT.
 */
boolean validateToken(String token)

/**
 * Obtém tempo de expiração.
 */
long getExpirationTime()
```

---

### UserPrincipal.java

**Propósito:** Representação do usuário autenticado no contexto Spring Security.

#### Propriedades

```java
Long id           // ID do usuário
String username   // Nome do usuário
String email      // Email do usuário
Set<GrantedAuthority> authorities  // Permissões
```

---

## 📤 DTOs (Data Transfer Objects)

### TaskRequest.java

**Uso:** Requisição HTTP para criar/atualizar tarefas.

```java
String title                    // Obrigatório (3-200)
String description              // Opcional
TaskStatus status              // Opcional (padrão: TODO)
TaskPriority priority          // Opcional (padrão: MEDIUM)
LocalDateTime dueDate          // Opcional
Integer estimatedHours         // Opcional
Set<String> tags               // Opcional
Long parentTaskId              // Opcional (para subtarefas)
```

---

### TaskResponse.java

**Uso:** Resposta HTTP com dados da tarefa.

```java
Long id                         // ID da tarefa
String title                    // Título
String description              // Descrição
TaskStatus status              // Status atual
TaskPriority priority          // Prioridade
LocalDateTime dueDate          // Data de vencimento
LocalDateTime completedAt      // Data de conclusão
Integer estimatedHours         // Horas estimadas
Integer actualHours            // Horas reais
Set<String> tags               // Tags
Boolean aiSuggestedPriority    // IA sugeriu?
String aiAnalysis              // Análise de IA
LocalDateTime createdAt        // Data de criação
LocalDateTime updatedAt        // Data de atualização
Long userId                    // ID do proprietário
Long parentTaskId              // ID da tarefa pai
Set<Long> subtaskIds           // IDs das subtarefas
```

---

### AuthRequest.java

**Uso:** Requisição de login.

```java
String username    // Nome do usuário
String password    // Senha
```

---

### AuthResponse.java

**Uso:** Resposta de login com token.

```java
String token       // JWT Token
String type        // Tipo do token (Bearer)
Long userId        // ID do usuário
String username    // Nome do usuário
String email       // Email
Set<String> roles  // Papéis/permissões
```

---

### AIAnalysisRequest.java

**Uso:** Requisição para análise de IA.

```java
String taskTitle         // Título da tarefa
String taskDescription   // Descrição
```

---

### AIAnalysisResponse.java

**Uso:** Resposta com análise de IA.

```java
TaskPriority suggestedPriority    // Prioridade sugerida
List<String> suggestedTags        // Tags sugeridas
List<String> suggestedSubtasks    // Subtarefas sugeridas
Integer estimatedHours            // Horas estimadas
String analysis                   // Análise textual
```

---

## ⚠️ Exceções (Exceptions)

### ResourceNotFoundException.java

**Uso:** Quando um recurso não é encontrado.

```java
throw new ResourceNotFoundException("Task not found");
```

---

### ResourceAlreadyExistsException.java

**Uso:** Quando um recurso já existe (ex: username duplicado).

```java
throw new ResourceAlreadyExistsException("Username already exists");
```

---

### GlobalExceptionHandler.java

**Propósito:** Handler global para exceções da aplicação.

**Métodos:**
- Tratamento de `ResourceNotFoundException`
- Tratamento de `ResourceAlreadyExistsException`
- Tratamento de `ValidationException`
- Tratamento de `AccessDeniedException`
- Tratamento genérico de exceções

---

## 👁️ Observabilidade (Observability)

### TracingAspect.java

**Propósito:** AOP Aspect que intercepta métodos com `@Traced`.

**Funcionamento:**
1. Detecta anotação `@Traced`
2. Cria span OpenTelemetry
3. Captura parâmetros (se ativado)
4. Registra exceções
5. Encerra span

---

### MetricsService.java

**Propósito:** Registra métricas customizadas.

#### Métodos Principais

```java
void recordAuthenticationSuccess()
void recordAuthenticationFailure()
void recordTaskCreation()
void recordTaskUpdate()
void recordTaskDelete()
void recordAIAnalysis()
void recordWhatsAppSent()
```

---

### @Traced Annotation

**Uso:** Marca método para rastreamento automático.

```java
@Traced("ServiceName.methodName")
public TaskResponse createTask(...) {
    // Implementation - span será criado automaticamente
}
```

---

## 📖 Como Usar a Documentação

### 1. Visualizar no Navegador

```bash
cd backend
mvn clean javadoc:aggregate
# Abrir: backend/target/site/apidocs/index.html
```

### 2. Usar com IDE

- **IntelliJ IDEA:** Hover sobre classe para ver JavaDoc
- **Eclipse:** Mesma funcionalidade
- **VS Code:** Extensão JavaDoc ou hover

### 3. Consultar Documentação no Código

```java
// Hover sobre qualquer classe/método para ver JavaDoc
TaskResponse response = taskService.createTask(request, currentUser);
                                    // Veja a documentação aqui ↑
```

---

## 🔄 Manutenção da Documentação

### Quando Atualizar

- ✅ Adicionar nova classe pública
- ✅ Adicionar novo método público
- ✅ Mudar assinatura de método
- ✅ Alterar comportamento
- ✅ Adicionar exceções

### Checklist de JavaDoc

- [ ] Classe tem documentação descritiva?
- [ ] Método público tem documentação?
- [ ] Parâmetros documentados (@param)?
- [ ] Retorno documentado (@return)?
- [ ] Exceções documentadas (@throws)?
- [ ] @author, @version, @since adicionados?
- [ ] @see referências implementadas?

---

## 📚 Referências Úteis

- [JavaDoc Official](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/javadoc.html)
- [Maven JavaDoc Plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)
- [JavaDoc Best Practices](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [Google Style Guide](https://google.github.io/styleguide/javaguide.html)

---

**Última atualização:** Outubro 2025  
**Versão:** Smart Task AI 1.0.0  
**Status:** ✅ Documentação Completa e Atualizada
