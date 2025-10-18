# 🚀 Smart Task Manager

Um gerenciador de tarefas inteligente com integração de IA, desenvolvido com **Java Spring Boot** no backend e **React + TypeScript** no frontend.

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![React](https://img.shields.io/badge/React-19-blue)
![TypeScript](https://img.shields.io/badge/TypeScript-4.9-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)

## ✨ Funcionalidades

### 🎯 Principais
- ✅ **CRUD completo de tarefas** com status, prioridades e tags
- 🤖 **Integração com IA (OpenAI GPT-4)** para:
  - Sugestão automática de prioridades
  - Análise de tarefas e geração de subtarefas
  - Estimativa de tempo necessário
  - Sugestão de tags relevantes
- 📱 **Notificações WhatsApp** com agendamento diário
  - Lembretes automáticos no horário que você escolher
  - Alertas de tarefas atrasadas
  - Resumo de conclusões do dia
- 🔐 **Autenticação JWT** segura
- 📊 **Dashboard com estatísticas** de produtividade
- 🔍 **Busca e filtros avançados**
- 🏷️ **Sistema de tags** personalizável
- 📅 **Gerenciamento de prazos** com alertas de atraso
- 🎨 **Interface moderna e responsiva** com TailwindCSS

### 🔮 Recursos de IA
- **Análise Inteligente**: A IA analisa o título e descrição da tarefa para sugerir prioridade, tags e subtarefas
- **Estimativa de Tempo**: Previsão automática de horas necessárias baseada no conteúdo
- **Divisão de Tarefas**: Sugestão de quebra de tarefas grandes em subtarefas menores

## 🛠️ Tecnologias

### Backend
- **Java 25**
- **Spring Boot 3.2** (Web, Data JPA, Security, Scheduling)
- **Spring Security** com JWT
- **PostgreSQL** / H2 (desenvolvimento)
- **OpenAI Java Client**
- **Twilio WhatsApp API**
- **Maven**
- **Lombok**

### Frontend
- **React 19** com TypeScript
- **Vite** (build tool)
- **TailwindCSS** (estilização)
- **React Router** (navegação)
- **TanStack Query** (gerenciamento de estado servidor)
- **Zustand** (gerenciamento de estado local)
- **Axios** (requisições HTTP)
- **Lucide React** (ícones)
- **React Hot Toast** (notificações)

## 📋 Pré-requisitos

- **Java 25+**
- **Node.js 18+**
- **Maven 3.8+**
- **PostgreSQL 15+** (ou use H2 para desenvolvimento)
- **Chave da API OpenAI** (opcional, mas recomendado)

## 🚀 Como Executar

### Opção 1: Com Docker (Recomendado)

1. Clone o repositório:
```bash
git clone https://github.com/jrcosta/smart-task-ai.git
cd smart-task-ai
```

2. Configure as variáveis de ambiente:
```bash
cp .env.example .env
# Edite o arquivo .env com suas configurações
```

3. Execute com Docker Compose:
```bash
docker-compose -f infrastructure/docker-compose.yml up -d
```

4. Acesse a aplicação:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

### Opção 1b: Com Docker + Observabilidade (Prometheus, Grafana, Jaeger)

```bash
# Inicie o stack de observabilidade
docker-compose -f infrastructure/docker-compose-observability.yml up -d

# Acesse:
# - Prometheus: http://localhost:9090
# - Grafana: http://localhost:3000
# - Jaeger UI: http://localhost:16686
```

**Documentação**: Veja [`docs/OBSERVABILITY.md`](docs/OBSERVABILITY.md) para configuração completa.

### Opção 2: Execução Local

#### Backend

1. Configure o banco de dados PostgreSQL ou use H2 (já configurado para dev)

2. Navegue até a pasta do backend:
```bash
cd backend
```

3. Configure as variáveis de ambiente (opcional):
```bash
export JWT_SECRET=sua-chave-secreta
export OPENAI_API_KEY=sua-chave-openai
```

4. Execute o backend:
```bash
mvn spring-boot:run
```

O backend estará disponível em: http://localhost:8080

#### Frontend

1. Navegue até a pasta do frontend:
```bash
cd frontend
```

2. Instale as dependências:
```bash
npm install
```

3. Execute o frontend:
```bash
npm run dev
```

O frontend estará disponível em: http://localhost:3000

## 🔑 Configuração da API OpenAI

Para usar os recursos de IA, você precisa de uma chave da API OpenAI:

1. Crie uma conta em https://platform.openai.com
2. Gere uma API key em https://platform.openai.com/api-keys
3. Configure a variável de ambiente `OPENAI_API_KEY`

**Nota**: A aplicação funciona sem a API key, mas os recursos de IA usarão análises mock.

## 📱 Configuração do WhatsApp

Para receber notificações no WhatsApp:

1. Crie uma conta gratuita no Twilio: https://www.twilio.com/try-twilio
2. Configure o WhatsApp Sandbox no console do Twilio
3. Obtenha suas credenciais (Account SID, Auth Token, WhatsApp Number)
4. Configure no arquivo `.env` do backend
5. Acesse a página de Notificações no app e configure seu número

**Guia completo**: Veja o arquivo [WHATSAPP_SETUP.md](docs/WHATSAPP_SETUP.md) para instruções detalhadas.

## 📊 Observabilidade & Monitoramento

Este projeto inclui observabilidade completa com **OpenTelemetry**, **Prometheus**, **Grafana** e **Jaeger**:

- 📈 **Métricas**: Monitoradas pelo Prometheus
- 📊 **Dashboards**: Visualizadas no Grafana
- 🔍 **Traces**: Rastreamento distribuído com Jaeger
- 📝 **Logs**: Estruturados e correlacionados com traces

### Iniciar Stack de Observabilidade

```bash
docker-compose -f infrastructure/docker-compose-observability.yml up -d
```

### Acessar Ferramentas

- **Prometheus**: http://localhost:9090 - Métricas e alertas
- **Grafana**: http://localhost:3000 - Dashboards personalizados
- **Jaeger UI**: http://localhost:16686 - Tracing distribuído

**Documentação completa**: Veja [`docs/OBSERVABILITY.md`](docs/OBSERVABILITY.md) para configuração e uso.

## � Documentação do Projeto

A base de código possui geração automática de documentação tanto para o backend (Java) quanto para o frontend (TypeScript).

### Backend (Javadoc)

```bash
cd backend
mvn javadoc:aggregate
```

O conteúdo HTML será exportado para `backend/target/site/apidocs/index.html`.

### Frontend (TypeDoc)

```bash
cd frontend
npm install   # apenas na primeira vez
npm run docs
```

Os arquivos gerados ficarão em `frontend/docs/frontend/index.html`. Basta abrir no navegador para navegar pelas referências.

## 📚 Documentação da API (Swagger/OpenAPI)

A API é totalmente documentada com **Swagger/OpenAPI 3.0** e gerada automaticamente a partir do código.

### 🔍 Acessar Documentação Interativa

Após iniciar o backend (`mvn spring-boot:run` ou Docker), acesse:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/docs
- **OpenAPI YAML**: http://localhost:8080/api/docs.yaml

### 🧪 Testar Endpoints

No Swagger UI você pode:
- ✅ Visualizar todos os endpoints disponíveis
- ✅ Ver detalhes de request/response
- ✅ Testar endpoints diretamente (com autenticação JWT)
- ✅ Baixar a especificação OpenAPI

### 🔐 Autenticação JWT

1. Registre um usuário via endpoint `/auth/register`
2. Faça login em `/auth/login` para obter o token
3. Clique no botão "Authorize" no Swagger UI
4. Cole o token no formato: `Bearer {seu-token-aqui}`
5. Todos os endpoints protegidos estarão disponíveis

### 📊 Endpoints Principais

No Swagger UI você encontrará documentação completa para:
- **Autenticação**: POST `/auth/register`, POST `/auth/login`
- **Tarefas**: GET/POST `/tasks`, GET/PUT/DELETE `/tasks/{id}`
- **IA**: POST `/tasks/ai`, POST `/ai/analyze`
- **Notificações**: GET/POST `/notifications`

**Todos os endpoints incluem**: parâmetros, tipos de resposta, códigos HTTP e exemplos de uso.

## 🏗️ Arquitetura

```
smart-task-ai/
├── backend/                        # API Spring Boot com OpenTelemetry
│   ├── src/main/java/
│   │   └── com/smarttask/
│   │       ├── config/             # Configurações (Security, OpenTelemetry)
│   │       ├── controller/         # Controllers REST
│   │       ├── dto/                # Data Transfer Objects
│   │       ├── exception/          # Exception handlers
│   │       ├── model/              # Entidades JPA
│   │       ├── repository/         # Repositórios
│   │       ├── security/           # JWT e autenticação
│   │       └── service/            # Lógica de negócio
│   └── pom.xml
│
├── frontend/                       # Aplicação React com TypeScript
│   ├── src/
│   │   ├── components/             # Componentes reutilizáveis
│   │   ├── pages/                  # Páginas da aplicação
│   │   ├── store/                  # Estado global (Zustand)
│   │   ├── lib/                    # Utilitários e API client
│   │   └── types/                  # Tipos TypeScript
│   └── package.json
│
├── infrastructure/                 # 🐳 Docker & Observabilidade
│   ├── docker-compose.yml          # Produção (Backend + Frontend + DB)
│   ├── docker-compose-observability.yml  # Stack de observabilidade
│   └── README.md                   # Guia de uso
│
├── observability/                  # 📊 Configs Prometheus, Grafana, Jaeger
│   ├── prometheus/
│   ├── grafana/
│   └── jaeger/
│
├── docs/                           # 📖 Documentação
├── scripts/                        # 🔧 Scripts de inicialização
└── README.md                       # Este arquivo
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para:

1. Fazer um fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abrir um Pull Request

**Leia [CONTRIBUTING.md](docs/CONTRIBUTING.md) para diretrizes detalhadas.**

### 🎯 Formas de Contribuir

- 🐛 **Reportar Bugs**: Abra uma [issue](https://github.com/jrcosta/smart-task-ai/issues/new?template=bug_report.md)
- ✨ **Sugerir Features**: Abra uma [feature request](https://github.com/jrcosta/smart-task-ai/issues/new?template=feature_request.md)
- 💬 **Discutir Ideias**: Participe das [discussions](https://github.com/jrcosta/smart-task-ai/discussions)
- 📝 **Melhorar Documentação**: PRs de docs são bem-vindas
- 🧪 **Adicionar Testes**: Aumentar cobertura é muito apreciado
- 🔧 **Implementar Features**: Veja o [ROADMAP.md](ROADMAP.md)

### 📋 Código de Conduta

Este projeto adota o [Código de Conduta](CODE_OF_CONDUCT.md). Esperamos que todos os contribuidores sigam estas diretrizes.

### 🔒 Segurança

Se encontrou uma vulnerabilidade de segurança, **não abra uma issue pública**. Veja [SECURITY.md](SECURITY.md) para instruções de divulgação responsável.

### 📚 Recursos para Contribuidores

- [Guia de Contribuição](docs/CONTRIBUTING.md) - Como começar
- [Instruções para Copilot/Agentes](docs/.github/copilot-instructions.md) - Para desenvolvimento com IA
- [Roadmap](ROADMAP.md) - Funcionalidades planejadas
- [Documentação da API](docs/API_DOCUMENTATION.md) - Swagger interativo

## 📝 Melhorias Futuras

- [ ] Modo Pomodoro integrado
- [ ] Notificações em tempo real (WebSocket)
- [ ] Compartilhamento de tarefas entre usuários
- [ ] Relatórios de produtividade com gráficos
- [ ] Integração com Google Calendar
- [ ] App mobile (React Native)
- [ ] Temas personalizáveis (dark mode)
- [ ] Exportação de tarefas (PDF, CSV)

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👨‍💻 Autor

Desenvolvido com ❤️ utilizando **GitHub Student Pack** e **GitHub Copilot Pro** como ferramentas principais para aprofundar o aprendizado em **Java**, **Inteligência Artificial** e **GitHub Copilot**. Este projeto demonstra como aplicar essas tecnologias em uma solução full-stack completa.

---

⭐ Se este projeto foi útil para você, considere dar uma estrela no GitHub!

**Tecnologias de Desenvolvimento:**
- 🎓 GitHub Student Pack
- 🤖 GitHub Copilot Pro
- ☕ Java & Spring Boot
- 🧠 OpenAI & Inteligência Artificial
- ⚛️ React & TypeScript
