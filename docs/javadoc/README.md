# 📚 Documentação JavaDoc - Smart Task Manager

Este diretório contém a documentação JavaDoc gerada automaticamente do projeto backend Java.

## 📋 Como Gerar a Documentação

### Pré-requisitos
- **Maven 3.8+** instalado e adicionado ao PATH
- **Java 25+**

### Geração Automática

```bash
cd backend
mvn clean javadoc:aggregate
```

A documentação será gerada em:
```
backend/target/site/apidocs/index.html
```

### Visualizar a Documentação

Após gerar, abra o arquivo `backend/target/site/apidocs/index.html` em seu navegador.

## 📖 Classes Documentadas

### Entidades (Models)

#### 1. **Task.java** 
Representa uma tarefa individual com todas as suas propriedades.

**Propriedades principais:**
- `id` - Identificador único
- `title` - Título da tarefa (3-200 caracteres)
- `description` - Descrição detalhada
- `status` - TODO, IN_PROGRESS, COMPLETED, CANCELLED
- `priority` - LOW, MEDIUM, HIGH, URGENT
- `dueDate` - Data de vencimento
- `estimatedHours` - Horas estimadas
- `tags` - Categorias (múltiplos valores)
- `aiAnalysis` - Análise fornecida pela IA
- `subtasks` - Tarefas filhas

**Relacionamentos:**
- `User` - Proprietário da tarefa (Many-to-One)
- `Task` - Tarefa pai para subtarefas (Many-to-One)

**Campos de Auditoria:**
- `createdAt` - Data de criação (automática)
- `updatedAt` - Última modificação (automática)

---

#### 2. **User.java**
Representa um usuário do sistema.

**Propriedades principais:**
- `id` - Identificador único
- `username` - Nome de usuário único (3-50 caracteres)
- `email` - Email único e validado
- `password` - Senha criptografada (BCrypt)
- `fullName` - Nome completo
- `avatarUrl` - URL do avatar/foto
- `active` - Status da conta (ativo/inativo)
- `roles` - Papéis/permissões (ROLE_USER, ROLE_ADMIN)

**Relacionamentos:**
- `Task` - Tarefas do usuário (One-to-Many)

**Campos de Auditoria:**
- `createdAt` - Data de criação (automática)
- `updatedAt` - Última modificação (automática)

---

### Controladores (Controllers)

#### 1. **TaskController.java**
Gerencia os endpoints HTTP relacionados a tarefas.

**Endpoints principais:**
- `POST /tasks` - Criar nova tarefa
- `POST /tasks/ai` - Criar tarefa com análise de IA
- `GET /tasks` - Listar todas as tarefas
- `GET /tasks/status/{status}` - Filtrar por status
- `GET /tasks/{id}` - Obter tarefa específica
- `PUT /tasks/{id}` - Atualizar tarefa
- `DELETE /tasks/{id}` - Deletar tarefa

**Autenticação:** Todos os endpoints requerem JWT Bearer Token

---

#### 2. **AuthController.java**
Gerencia endpoints de autenticação e autorização.

**Endpoints principais:**
- `POST /auth/register` - Registrar novo usuário
- `POST /auth/login` - Fazer login e obter token JWT
- `GET /auth/me` - Obter dados do usuário autenticado

---

#### 3. **AIController.java**
Gerencia endpoints relacionados a análise de IA.

**Endpoints principais:**
- `POST /ai/analyze` - Analisar tarefa com IA
- `POST /ai/suggest-priority` - Sugerir prioridade automática
- `POST /ai/generate-subtasks` - Gerar subtarefas sugeridas

---

### Serviços (Services)

#### 1. **TaskService.java**
Lógica de negócio principal para tarefas.

**Métodos principais:**
- `createTask()` - Criar nova tarefa
- `createTaskWithAI()` - Criar com análise de IA
- `getAllTasks()` - Listar tarefas do usuário
- `getTaskById()` - Obter tarefa específica
- `updateTask()` - Atualizar propriedades
- `deleteTask()` - Deletar tarefa
- `getTasksByStatus()` - Filtrar por status
- `getTasksByPriority()` - Filtrar por prioridade

**Observabilidade:**
- Todos os métodos públicos usam `@Traced` para rastreamento OpenTelemetry
- Métricas customizadas registradas via `MetricsService`

---

#### 2. **AuthService.java**
Lógica de autenticação e autorização.

**Métodos principais:**
- `register()` - Registrar novo usuário
- `login()` - Autenticar e gerar JWT
- `validateToken()` - Validar token JWT
- `refreshToken()` - Renovar token expirado

**Segurança:**
- Senhas criptografadas com BCrypt
- Tokens JWT com expiração configurável
- Spring Security integrado

---

#### 3. **AIService.java**
Integração com OpenAI GPT-4.

**Métodos principais:**
- `analyzeTask()` - Análise completa de tarefa
- `suggestPriority()` - Sugerir prioridade automática
- `generateSubtasks()` - Gerar subtarefas recomendadas
- `estimateHours()` - Estimar horas de trabalho

**Fallback:**
- Se `OPENAI_API_KEY` não estiver configurado, retorna análise mock
- Nunca falha, sempre fornece resposta

---

#### 4. **WhatsAppService.java**
Notificações via Twilio WhatsApp.

**Métodos principais:**
- `sendNotification()` - Enviar mensagem WhatsApp
- `sendDailyReminder()` - Enviar lembrete diário
- `sendLateTaskAlert()` - Alertar tarefas atrasadas

**Fallback:**
- Se credenciais Twilio não configuradas, simula envio com logs
- Nunca falha, sempre registra evento

---

### Classe Principal

#### 1. **SmartTaskManagerApplication.java**
Ponto de entrada da aplicação Spring Boot.

**Configurações ativadas:**
- `@SpringBootApplication` - Autoconfiguração Spring Boot
- `@EnableJpaAuditing` - Rastreamento automático de datas
- `@EnableScheduling` - Tarefas agendadas com @Scheduled

---

## 🏗️ Estrutura de Pacotes

```
com.smarttask
├── config/               # Configurações centralizadas
│   ├── OpenApiConfig     # Swagger/OpenAPI
│   ├── OpenTelemetryConfig
│   └── SecurityConfig    # JWT + Spring Security
│
├── controller/           # Controladores REST
│   ├── TaskController
│   ├── AuthController
│   ├── AIController
│   └── NotificationController
│
├── service/              # Lógica de negócio
│   ├── TaskService
│   ├── AuthService
│   ├── AIService
│   ├── WhatsAppService
│   └── NotificationService
│
├── model/                # Entidades JPA
│   ├── Task
│   ├── User
│   ├── NotificationPreference
│   └── PomodoroSession
│
├── repository/           # Spring Data JPA
│   ├── TaskRepository
│   ├── UserRepository
│   └── NotificationPreferenceRepository
│
├── security/             # JWT + Spring Security
│   ├── JwtTokenProvider
│   ├── UserPrincipal
│   └── SecurityConfig
│
├── dto/                  # Data Transfer Objects
│   ├── TaskRequest/Response
│   ├── AuthRequest/Response
│   └── AIAnalysisRequest/Response
│
├── exception/            # Tratamento de exceções
│   ├── ResourceNotFoundException
│   └── GlobalExceptionHandler
│
├── observability/        # OpenTelemetry
│   ├── TracingAspect (AOP)
│   ├── MetricsService
│   └── @Traced (Annotation)
│
└── SmartTaskManagerApplication  # Main class
```

---

## 🔍 Convenções de Documentação

### Anotações Usadas

- **@author** - Autor/time responsável
- **@version** - Versão da classe
- **@since** - Data/versão de introdução
- **@param** - Documentação de parâmetros
- **@return** - Documentação de retorno
- **@throws** - Exceções possíveis
- **@see** - Referências cruzadas

### Exemplo de JavaDoc Completo

```java
/**
 * Cria uma nova tarefa para o usuário autenticado.
 * 
 * <p>Se a requisição indicar {@code analyzeWithAI = true}, a tarefa será
 * enviada para análise pela IA.</p>
 * 
 * @param request DTO com dados da tarefa
 * @param currentUser Principal do usuário autenticado
 * @return {@link TaskResponse} com os dados da tarefa criada
 * @throws ResourceNotFoundException se o usuário não existir
 * @see TaskService#createTaskWithAI(TaskRequest, UserPrincipal)
 */
public TaskResponse createTask(TaskRequest request, UserPrincipal currentUser) {
    // Implementation...
}
```

---

## 📊 Geração com Docker

Se Maven não estiver instalado, você pode usar Docker:

```bash
docker run -v $(pwd):/workspace -w /workspace/backend maven:3.9-eclipse-temurin-25 \
  mvn clean javadoc:aggregate
```

Depois, abra `backend/target/site/apidocs/index.html` no navegador.

---

## 🔗 Recursos Adicionais

- [Java Documentation Comments (JavaDoc)](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [Maven JavaDoc Plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Swagger UI Interativa](/api/swagger-ui.html) - Quando o backend estiver rodando

---

## ✅ Checklist de Documentação

- [x] Todas as classes públicas têm JavaDoc
- [x] Todos os métodos públicos têm documentação
- [x] Todos os campos importantes têm documentação
- [x] Exemplos de uso incluídos onde apropriado
- [x] @author, @version, @since adicionados
- [x] @see referências cruzadas implementadas
- [x] DTOs documentados com @param e @return
- [x] Exceções documentadas com @throws

---

**Última atualização:** Outubro 2025  
**Versão:** Smart Task AI 1.0.0  
**Manutenido por:** Smart Task AI Team
