# 🤖 GitHub Copilot Instructions - Smart Task AI

Este arquivo fornece instruções essenciais para agentes de IA acelerar o desenvolvimento neste codebase.

## 🏗️ Arquitetura do Projeto

**Smart Task AI** é um gerenciador de tarefas full-stack com IA integrada (OpenAI GPT-4), notificações WhatsApp (Twilio) e observabilidade completa (OpenTelemetry + Prometheus + Grafana + Jaeger).

### Componentes Principais

```
Frontend (React 19 + TypeScript + Vite)
         ↓ (Axios)
Backend API (Java 25 + Spring Boot 3.2 + OpenTelemetry)
    ├─→ Database (PostgreSQL / H2)
    ├─→ OpenAI API (análise de tarefas)
    ├─→ Twilio API (notificações WhatsApp)
    └─→ OpenTelemetry → Jaeger (traces) / Prometheus (métricas) → Grafana
```

## 🔧 Estrutura de Código

### Backend (`backend/src/main/java/com/smarttask/`)

- **`config/`** - Configurações centralizadas (Security, OpenAPI Swagger, OpenTelemetry)
- **`controller/`** - REST endpoints com `@Traced` para observabilidade
- **`service/`** - Lógica de negócio instrumentada (TaskService, AIService, AuthService, WhatsAppService)
- **`model/`** - Entidades JPA com relações Many-to-One/One-to-Many
- **`repository/`** - Interfaces Spring Data JPA estendendo CrudRepository
- **`security/`** - JWT (JwtTokenProvider, SecurityConfig) com Spring Security
- **`observability/`** - AOP aspect TracingAspect, métrica customizada MetricsService, anotação @Traced

### Frontend (`frontend/src/`)

- **`components/`** - Componentes reutilizáveis (TaskCard, TaskModal, Layout)
- **`pages/`** - Páginas de rota (Dashboard, Tasks, Login, Notifications)
- **`store/`** - Estado global com Zustand (authStore, taskStore)
- **`lib/api.ts`** - Cliente Axios com interceptador JWT automático
- **`types/index.ts`** - Interfaces TypeScript alinhadas com DTO backend

## 📋 Padrões Críticos do Projeto

### 1. Autenticação JWT
- **Fluxo**: POST `/auth/register` → POST `/auth/login` → JWT token → Bearer header
- **Localização**: `backend/security/JwtTokenProvider.java`, `frontend/store/authStore.ts`
- **Interceptador Frontend**: `lib/api.ts` adiciona automaticamente token JWT a cada request
- **Regra**: Sempre validar token em endpoints protegidos (Spring Security annotations)

### 2. Observabilidade com OpenTelemetry (OBRIGATÓRIA em Services)
- **Anotação**: Use `@Traced("OperationName")` em métodos de serviço
- **AOP Automático**: `observability/TracingAspect.java` cria spans automaticamente
- **Métricas**: `MetricsService.recordXxx()` para métricas customizadas (ex: recordAuthenticationFailure)
- **Exemplo**:
  ```java
  @Service
  public class TaskService {
    @Traced("TaskService.createTask")
    public Task createTask(CreateTaskRequest req) {
      // Logic here - span será criado automaticamente
    }
  }
  ```
- **Não implementar**: Não adicione observabilidade manual (spans, scopes) pois o AspectJ cuida disso

### 3. Integração com IA (Fallback Mock)
- **Arquivo**: `backend/service/AIService.java`
- **Comportamento**: Se `OPENAI_API_KEY` não está configurado, retorna análise mock (não falha)
- **Padrão**: Parse resposta em português (PRIORIDADE:, TAGS:, SUBTAREFAS:, ANÁLISE:, HORAS:)
- **Ao modificar**: Sempre manter fallback mock funcional

### 4. Integração Twilio WhatsApp
- **Arquivo**: `backend/service/WhatsAppService.java`
- **Credenciais**: Requer `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_WHATSAPP_NUMBER` (opcional)
- **Fallback**: Se não configurado, simula envio com logs em vez de falhar
- **Rastreamento**: Todo envio registra eventos no @Traced e MetricsService

### 5. DTOs vs Models
- **DTOs** (request/response): `backend/dto/` - controllers recebem DTOs, nunca models diretos
- **Models** (entities JPA): `backend/model/` - persistência, relacionamentos, anotações JPA
- **Conversão**: Services fazem mapeamento entre DTO ↔ Model (não use automapper, faça manualmente)

### 6. Resposta Frontend
- **Cliente**: `frontend/lib/api.ts` expõe `taskAPI`, `authAPI`, `aiAPI`
- **Handlers**: Use `react-hot-toast` para notificações (sucesso/erro)
- **TanStack Query**: Todas requisições usam `.useMutation()` ou `.useQuery()`
- **Exemplo**:
  ```tsx
  const { mutate: createTask } = useMutation({
    mutationFn: taskAPI.create,
    onSuccess: () => toast.success('Tarefa criada!'),
    onError: (err) => toast.error(err.message),
  });
  ```

## 🚀 Workflows Críticos

### Build & Run Backend
```bash
cd backend
mvn clean install        # Compila, testa, instala
mvn spring-boot:run      # Rodando em http://localhost:8080/api
```

### Build & Run Frontend
```bash
cd frontend
npm install              # Instala dependências
npm run dev              # Vite dev server em http://localhost:5173 (proxy para localhost:8080/api)
```

### Docker & Observabilidade
```bash
docker-compose -f infrastructure/docker-compose.yml up -d          # Produção
docker-compose -f infrastructure/docker-compose-observability.yml up -d  # Stack completo com Prometheus/Grafana/Jaeger
./scripts/validate-observability.sh  # Validar serviços
```

### Acessar Documentação Interativa
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **Jaeger Traces**: http://localhost:16686
- **Prometheus Métricas**: http://localhost:9090
- **Grafana Dashboard**: http://localhost:3001 (admin/admin)

## ⚠️ Regras CRÍTICAS

### Terminal & Processos
- **NUNCA** execute comandos no mesmo terminal onde Spring Boot está rodando
- **SEMPRE** abra novo terminal para qualquer comando
- Use `isBackground=true` para processos de longa duração

### Git & Branches (OBRIGATÓRIO)
- **TODA mudança deve ser criada em uma branch nova** (nunca commitar direto em `master`)
- **Convenção de branch**: `feature/nome-da-feature`, `bugfix/nome-do-bug`, `docs/nome-do-doc`
- **Exemplo**: 
  ```bash
  git checkout -b feature/adicionar-autenticacao-2fa
  git commit -m "Adicionar autenticação 2FA"
  git push origin feature/adicionar-autenticacao-2fa
  ```
- **Pull Request obrigatório**: Toda mudança deve ser enviada como um novo PR para revisão
- **PR deve incluir**: Descrição clara, testes, documentação atualizada
- **Review antes do merge**: Nunca fazer merge direto sem revisão

### Dependências
- **Versões CONGELADAS**: Java 25, Spring Boot 3.2, React 19, Maven, Node 18+
- **Alterar versão REQUER** autorização explícita ("pode alterar a versão")
- Verificar compatibilidade antes de qualquer update

### Código
- **Português obrigatório**: Comments, commits, mensagens em português
- **JWT sempre**: Endpoints protegidos devem validar token
- **@Traced sempre**: Métodos de service devem ter anotação (exceto getters triviais)
- **Sem boilerplate**: Use Lombok (`@Data`, `@RequiredArgsConstructor`, `@Slf4j`)

## 📁 Arquivos-Chave para Referência

| Arquivo | Propósito |
|---------|-----------|
| `backend/pom.xml` | Dependências, versões Maven |
| `backend/src/main/resources/application.yml` | Config banco, JWT, OpenAI, Twilio, Observabilidade |
| `backend/src/main/java/com/smarttask/config/OpenApiConfig.java` | Swagger/OpenAPI 3.0 |
| `backend/src/main/java/com/smarttask/config/SecurityConfig.java` | JWT, Spring Security |
| `backend/src/main/java/com/smarttask/observability/TracingAspect.java` | AOP para @Traced |
| `backend/src/main/java/com/smarttask/service/AIService.java` | OpenAI com fallback mock |
| `backend/src/main/java/com/smarttask/service/WhatsAppService.java` | Twilio com fallback |
| `frontend/src/lib/api.ts` | Cliente HTTP com JWT interceptador |
| `frontend/src/store/authStore.ts` | Estado autenticação Zustand |
| `docs/AGENTS.md` | Diretrizes específicas do projeto |
| `docs/OBSERVABILITY_POINTS.md` | Estrutura de traces e métricas |

## 🔄 Fluxo de Dados Comum

1. **Frontend** dispara `useMutation(taskAPI.create({...}))`
2. **api.ts interceptor** adiciona JWT header
3. **Backend TaskController** recebe `CreateTaskRequest` DTO
4. **SecurityConfig** valida JWT automaticamente (Spring Security)
5. **TaskService.createTask()** é executado com `@Traced` → AOP cria span
6. **AIService.analyzeTask()** chamado se `OPENAI_API_KEY` configurado (ou mock)
7. **TaskRepository.save()** persiste entity
8. **MetricsService** registra métrica customizada
9. **TaskController** retorna `TaskResponse` DTO
10. **Frontend** recebe resposta, atualiza cache TanStack Query, exibe toast

## 💡 Dicas para Agentes

- **Leia primeiro**: `docs/AGENTS.md`, `docs/OBSERVABILITY_POINTS.md`, `docs/IMPLEMENTATION_SUMMARY.md`
- **Sempre instrumentalize**: Se criar novo service, adicione `@Traced` nos métodos públicos
- **Teste com Docker**: Validar integração Twilio/OpenAI com Docker antes de commit
- **Commits descritivos**: Mensagens em português, referenciar feature/issue
- **Não remova fallbacks**: Mock de IA e Twilio são essenciais para trabalhar sem credenciais

---

**Última atualização**: Outubro 2025  
**Versão**: Smart Task AI v1.0.0  
**Mantido por**: GitHub Student Pack + Copilot Pro
