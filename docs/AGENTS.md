# Smart Task Manager — Visão Geral dos Agentes

## Agente Backend (Java + Spring Boot)
- **Responsabilidades**
    - CRUD de tarefas, usuários e configurações de notificações.
    - Integração com Twilio para envio de mensagens WhatsApp.
    - Exposição de API REST segura para o frontend React.
- **Principais Tecnologias**
    - Java 25, Spring Boot, Spring Data JPA, Spring Security.
    - Banco PostgreSQL, Flyway para migrações, Maven.
    - Integração REST com Twilio.
- **Fluxo de Trabalho**
    1. Recebe requisições do frontend.
    2. Valida e persiste dados.
    3. Dispara notificações via Twilio quando necessário.

## Agente Frontend (React)
- **Responsabilidades**
    - Interface em português para controle de tarefas.
    - Consumo da API backend, autenticação e feedback em tempo real.
    - Área de portfolio exibindo funcionalidades e stack.
- **Principais Tecnologias**
    - React 19.1.1, TypeScript, Vite.
    - Styled Components, React Query, React Router.
- **Fluxo de Trabalho**
    1. Autentica usuário com backend.
    2. Exibe dashboard e tarefas.
    3. Dispara ações CRUD e apresenta confirmações.

## Integração WhatsApp (Twilio)
- **Objetivo**
    - Enviar lembretes automáticos de tarefas via WhatsApp.
- **Processo**
    1. Backend identifica tarefas com alertas configurados.
    2. Monta mensagem personalizada em português.
    3. Chama API Twilio e registra status do envio.

## Boas Práticas
- Commits curtos e descritivos em português.
- Pull requests revisados antes do merge em `main`.
- Documentação atualizada em `README.md` e Wiki.

## ⚠️ Boas Práticas de Terminal
- **CRÍTICO**: Nunca executar comandos no mesmo terminal onde uma aplicação Spring Boot está rodando, pois isso irá **parar a aplicação**.
- **SEMPRE abrir um novo terminal** para executar comandos enquanto o Spring Boot está ativo.
- **REGRA GERAL**: Sempre preferir abrir um novo terminal para qualquer comando, evitando conflitos com processos em execução.
- Usar `isBackground=true` ao iniciar aplicações de longa duração.
- Verificar status e executar comandos sempre em terminais separados.
- Nunca reutilizar terminais que já têm processos importantes rodando.

## 🔒 Controle de Versões de Dependências
- **IMPORTANTE**: As versões de qualquer dependência do projeto (Java, React, bibliotecas, etc.) só devem ser alteradas após a autorização explícita com a frase **"pode alterar a versão"**.
- Sempre verificar compatibilidade antes de atualizar versões.
- Manter estabilidade do ambiente de desenvolvimento como prioridade.

## Hospedagem e Portfolio
- Repositório público no GitHub.
- Deploy backend em serviço Java (Railway, Render ou similar).
- Deploy frontend no GitHub Pages, Vercel ou Netlify.
- Demonstração em vídeo curta anexada ao README.

## Regras Gerais
- Código deve ser escrito em português.
- Seguir boas práticas de commits e pull requests.
- Manter documentação atualizada.
- Sempre responder o chat em português.