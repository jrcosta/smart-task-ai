# 📚 Índice de JavaDoc do Smart Task Manager

## 📍 Localização da Documentação

Quando gerada, a documentação JavaDoc estará em:
```
backend/target/site/apidocs/index.html
```

## 🚀 Como Gerar a Documentação

### Opção 1: PowerShell (Recomendado no Windows)

```powershell
.\generate-javadoc.ps1
```

### Opção 2: Batch Script (Windows)

```bash
generate-javadoc.bat
```

### Opção 3: Maven Direto

```bash
cd backend
mvn clean javadoc:aggregate
```

Depois abra: `backend/target/site/apidocs/index.html`

### Opção 4: Docker

```bash
docker build -f docs/javadoc/Dockerfile -t smart-task-javadoc .
docker run -v $(pwd)/docs/javadoc:/app/docs smart-task-javadoc
```

## 📦 Requisitos

- **Maven 3.8+** (para gerar via linha de comando)
- **Java 25+** (compatibilidade com versão do projeto)
- **Docker** (alternativa ao Maven)

## 📄 Conteúdo da Documentação

A documentação JavaDoc inclui:

### 📋 Entidades (Models)
- **Task** - Representação de uma tarefa
- **User** - Dados do usuário
- **NotificationPreference** - Preferências de notificação
- **PomodoroSession** - Sessões Pomodoro

### 🎮 Controladores (Controllers)
- **TaskController** - Endpoints para tarefas
- **AuthController** - Endpoints de autenticação
- **AIController** - Endpoints de IA
- **NotificationController** - Endpoints de notificações

### 🔧 Serviços (Services)
- **TaskService** - Lógica de tarefas
- **AuthService** - Lógica de autenticação
- **AIService** - Integração com OpenAI
- **WhatsAppService** - Notificações WhatsApp
- **NotificationService** - Gerenciamento de notificações

### ⚙️ Configurações (Config)
- **SecurityConfig** - Configuração de segurança JWT
- **OpenApiConfig** - Configuração Swagger/OpenAPI
- **OpenTelemetryConfig** - Configuração observabilidade

### 🛡️ Segurança (Security)
- **JwtTokenProvider** - Provedor de tokens JWT
- **UserPrincipal** - Principal do usuário
- **SecurityConfig** - Configuração Spring Security

### 📊 Repositórios (Repositories)
- **TaskRepository** - Acesso a dados de tarefas
- **UserRepository** - Acesso a dados de usuários
- **NotificationPreferenceRepository** - Acesso a preferências

### 📤 DTOs (Data Transfer Objects)
- **TaskRequest/Response** - DTO de tarefas
- **AuthRequest/Response** - DTO de autenticação
- **AIAnalysisRequest/Response** - DTO de IA
- **NotificationRequest/Response** - DTO de notificações

### 👁️ Observabilidade (Observability)
- **TracingAspect** - AOP para rastreamento
- **MetricsService** - Métricas customizadas
- **@Traced** - Anotação para tracing

## 🔍 Convenções de Documentação Usadas

Todas as classes e métodos públicos incluem:

- **Descrição da classe/método** - O que faz
- **Parâmetros (@param)** - O que cada parâmetro significa
- **Retorno (@return)** - O que é retornado
- **Exceções (@throws)** - Exceções que podem ser lançadas
- **Referências (@see)** - Links para classes relacionadas
- **Autor (@author)** - Equipe responsável
- **Versão (@version)** - Versão da classe
- **Desde (@since)** - Quando foi adicionado

## 📖 Exemplo de Documentação

```java
/**
 * Cria uma nova tarefa para o usuário autenticado.
 * 
 * <p>Se a requisição indicar {@code analyzeWithAI = true}, 
 * a tarefa será enviada para análise pela IA.</p>
 * 
 * @param request DTO com dados da tarefa
 * @param currentUser Principal do usuário autenticado
 * @return {@link TaskResponse} com os dados da tarefa criada
 * @throws ResourceNotFoundException se o usuário não existir
 * @see TaskService#createTaskWithAI(TaskRequest, UserPrincipal)
 * @author Smart Task AI Team
 * @version 1.0
 * @since 2025-10
 */
public TaskResponse createTask(TaskRequest request, UserPrincipal currentUser) {
    // Implementation...
}
```

## 🌐 Visualização Online

Quando o backend está rodando, você também pode visualizar:

1. **Swagger UI (OpenAPI):** http://localhost:8080/api/swagger-ui.html
2. **OpenAPI JSON:** http://localhost:8080/api/docs
3. **JavaDoc Estático:** Gere com um dos métodos acima

## 📚 Estrutura do JavaDoc Gerado

```
apidocs/
├── index.html                    # Página inicial
├── allclasses.html               # Lista de todas as classes
├── overview-summary.html         # Resumo geral
├── com/
│   └── smarttask/
│       ├── model/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── security/
│       ├── config/
│       ├── dto/
│       ├── exception/
│       ├── observability/
│       └── SmartTaskManagerApplication.html
├── script.js                     # Scripts da documentação
├── stylesheet.css                # Estilos
└── resources/                    # Recursos adicionais
```

## ⚡ Dicas de Uso

### 1. Navegar pela Documentação
- Use o **índice de classes** no lado esquerdo
- Use a **busca** (Ctrl+F) para encontrar classes
- Clique em links para ver detalhes das classes referenciadas

### 2. Entender as Anotações
- **Métodos em verde** = públicos
- **Métodos em azul** = protegidos
- **Métodos em vermelho** = construtores
- Passe o mouse para ver descrições

### 3. Explorar Exemplos
- Cada método tem documentação de uso
- Veja as relacionadas via @see
- Consulte DTOs para estrutura de dados

## 🔄 Atualizar a Documentação

Sempre que adicionar:
- ✅ Nova classe
- ✅ Novo método público
- ✅ Novos campos importantes

**Atualize o JavaDoc** executando um dos scripts acima.

## 🐛 Resolução de Problemas

### Maven não encontrado
```bash
# Windows (Chocolatey)
choco install maven

# macOS
brew install maven

# Linux (Ubuntu/Debian)
sudo apt-get install maven
```

### Erro de permissão de arquivo
```powershell
# PowerShell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Docker não disponível
Use Maven diretamente via linha de comando.

## 📞 Suporte

Para dúvidas sobre a documentação ou bugs no JavaDoc:
1. Consulte [docs/API_DOCUMENTATION.md](../API_DOCUMENTATION.md)
2. Abra uma issue no GitHub
3. Verifique a [documentação oficial do JavaDoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)

---

**Última atualização:** Outubro 2025  
**Versão:** Smart Task AI 1.0.0  
**Status:** ✅ Documentação Completa
