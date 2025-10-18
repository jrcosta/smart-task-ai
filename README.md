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
- **Java 17**
- **Spring Boot 3.2** (Web, Data JPA, Security, Scheduling)
- **Spring Security** com JWT
- **PostgreSQL** / H2 (desenvolvimento)
- **OpenAI Java Client**
- **Twilio WhatsApp API**
- **Maven**
- **Lombok**

### Frontend
- **React 18** com TypeScript
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
docker-compose up -d
```

4. Acesse a aplicação:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

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

## �📚 Documentação da API

### Autenticação

#### Registrar
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "usuario",
  "email": "usuario@exemplo.com",
  "password": "senha123",
  "fullName": "Nome Completo"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "senha123"
}
```

### Tarefas

#### Listar todas as tarefas
```http
GET /api/tasks
Authorization: Bearer {token}
```

#### Criar tarefa
```http
POST /api/tasks
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Minha tarefa",
  "description": "Descrição detalhada",
  "priority": "HIGH",
  "dueDate": "2024-12-31T23:59:59",
  "tags": ["trabalho", "urgente"]
}
```

#### Criar tarefa com IA
```http
POST /api/tasks/ai
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Implementar autenticação no sistema",
  "description": "Adicionar login e registro de usuários com JWT"
}
```

#### Atualizar tarefa
```http
PUT /api/tasks/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Tarefa atualizada",
  "status": "COMPLETED"
}
```

#### Deletar tarefa
```http
DELETE /api/tasks/{id}
Authorization: Bearer {token}
```

### IA

#### Analisar texto
```http
POST /api/ai/analyze
Authorization: Bearer {token}
Content-Type: application/json

{
  "text": "Criar documentação completa do projeto",
  "context": "Projeto de gerenciador de tarefas"
}
```

## 🏗️ Arquitetura

```
smart-task-manager/
├── backend/                 # API Spring Boot
│   ├── src/main/java/
│   │   └── com/smarttask/
│   │       ├── config/      # Configurações (Security, etc)
│   │       ├── controller/  # Controllers REST
│   │       ├── dto/         # Data Transfer Objects
│   │       ├── exception/   # Exception handlers
│   │       ├── model/       # Entidades JPA
│   │       ├── repository/  # Repositórios
│   │       ├── security/    # JWT e autenticação
│   │       └── service/     # Lógica de negócio
│   └── pom.xml
│
├── frontend/                # Aplicação React
│   ├── src/
│   │   ├── components/      # Componentes reutilizáveis
│   │   ├── pages/           # Páginas da aplicação
│   │   ├── store/           # Estado global (Zustand)
│   │   ├── lib/             # Utilitários e API client
│   │   └── types/           # Tipos TypeScript
│   └── package.json
│
└── docker-compose.yml       # Orquestração de containers
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para:

1. Fazer um fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abrir um Pull Request

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
