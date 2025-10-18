# 🗺️ Roadmap - Smart Task AI

Visão geral das funcionalidades planejadas e prioridades futuras.

## 📅 Timeline

### ✅ V1.0.0 (Atual - Outubro 2025)

- [x] CRUD completo de tarefas
- [x] Autenticação JWT
- [x] Integração OpenAI GPT-4
- [x] Notificações WhatsApp (Twilio)
- [x] Dashboard com estatísticas
- [x] Observabilidade completa (OpenTelemetry + Prometheus + Grafana + Jaeger)
- [x] Documentação com Swagger/OpenAPI
- [x] Docker & Docker Compose
- [x] GitHub Copilot Instructions

### 🚀 V1.1.0 (Q1 2026) - Melhorias de Usabilidade

- [ ] **Modo Escuro/Claro**: Toggle de tema no frontend
- [ ] **Filtros Avançados**: Filtrar por prioridade, status, tags, datas
- [ ] **Busca Full-text**: Buscar em título e descrição
- [ ] **Ordenação Customizável**: Por criação, prazo, prioridade
- [ ] **Atalhos de Teclado**: Command palette (Cmd+K / Ctrl+K)
- [ ] **Suporte a Múltiplos Idiomas**: i18n (PT-BR, EN, ES)

### 🤖 V1.2.0 (Q2 2026) - Recursos de IA Avançados

- [ ] **Sugestões Automáticas**: IA sugere próximas ações baseadas em histórico
- [ ] **Análise de Sentimento**: Categorizar tarefas por urgência/impacto emocional
- [ ] **Resumo Diário**: IA gera resumo de produtividade do dia
- [ ] **Predição de Tempo**: ML prediz duração com mais precisão
- [ ] **Detecção de Duplicatas**: IA sugere tarefas possivelmente duplicadas
- [ ] **Geração de Relatórios**: Relatórios em PDF com gráficos

### 📱 V1.3.0 (Q3 2026) - Expansão de Plataformas

- [ ] **App Mobile (React Native)**: iOS + Android
- [ ] **Progressive Web App (PWA)**: Funciona offline
- [ ] **Extensão Chrome**: Criar tarefas desde qualquer página
- [ ] **Notificações em Tempo Real**: WebSocket + Push notifications
- [ ] **Integração com Google Calendar**: Sincronizar prazos
- [ ] **Integração com Slack**: Comandos e notificações via Slack

### 🔄 V2.0.0 (Q4 2026) - Modo Colaborativo

- [ ] **Compartilhamento de Tarefas**: Colaborar com outros usuários
- [ ] **Comentários em Tarefas**: Discussão assíncrona
- [ ] **Histórico de Mudanças**: Audit trail completo
- [ ] **Permissões Granulares**: Roles (owner, editor, viewer)
- [ ] **Notificações de Atualizações**: Receber alertas de mudanças
- [ ] **Workspaces**: Separar tarefas por projeto/equipe

## 🎯 Prioridades Atuais

### 🔴 Alta Prioridade
1. Estabilidade e performance (otimizações de query)
2. Cobertura de testes (target: 80%)
3. Documentação melhorada
4. CI/CD pipeline automatizado
5. Rate limiting e proteção de força bruta

### 🟡 Média Prioridade
1. Tema escuro/claro
2. Filtros e busca avançada
3. Suporte a múltiplos idiomas
4. App mobile inicial

### 🟢 Baixa Prioridade
1. Extensão Chrome
2. Integração com Slack
3. Modo colaborativo completo

## 🐛 Backlog Técnico

- [ ] Migrar para TypeORM (substituir JPA manual mapping)
- [ ] Adicionar GraphQL como alternativa ao REST
- [ ] Melhorar performance com caching distribuído (Redis)
- [ ] Testes E2E com Playwright
- [ ] Storybook para componentes React
- [ ] Monorepo com Nx
- [ ] Kubernetes deployment configs

## 💡 Ideias da Comunidade

Tem uma ideia? Abra uma [Discussion](https://github.com/jrcosta/smart-task-ai/discussions) ou uma [Issue](https://github.com/jrcosta/smart-task-ai/issues) com a label `enhancement`.

## 🤝 Como Contribuir

Quer ajudar a implementar algo do roadmap? Veja [CONTRIBUTING.md](CONTRIBUTING.md) para instruções.

---

**Última atualização**: Outubro 2025  
**Status**: Em desenvolvimento ativo  
**Maintainer**: @jrcosta
