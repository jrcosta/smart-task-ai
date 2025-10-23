# Changelog

## [Unreleased]

### 🔧 Melhorias Técnicas
- Atualização do Spring Boot para 3.5.2 (compatibilidade com Java 25 e melhorias de performance)
- Atualização do SpringDoc OpenAPI para 2.6.0 para compatibilidade com Spring Boot 3.5.x
- Configuração do compilador Maven ajustada para permitir override do `maven.compiler.release` via `-Dmaven.compiler.release` (padrão permanece 25)

## [1.1.0] - 2025-10-05

### ✨ Novidades

#### 📱 Notificações WhatsApp
- Agente de notificações automáticas via WhatsApp usando Twilio
- Lembretes diários configuráveis (ex: 8:30 da manhã)
- Alertas de tarefas atrasadas
- Resumo de conclusões do dia
- Interface de configuração no frontend
- Mensagens formatadas com emojis e prioridades

#### 🔧 Melhorias Técnicas
- Integração com Twilio WhatsApp API
- Spring Scheduler para agendamento automático
- Novo modelo `NotificationPreference` para preferências do usuário
- Endpoints REST para gerenciar notificações
- Página de configuração de notificações no frontend
- Modo de simulação quando Twilio não está configurado

### 📝 Documentação
- Guia completo de configuração do WhatsApp (WHATSAPP_SETUP.md)
- Atualização do README com novas funcionalidades
- Instruções de configuração do Twilio

---

## [1.0.0] - 2025-10-05

### 🎉 Lançamento Inicial

#### Funcionalidades Principais
- CRUD completo de tarefas
- Autenticação JWT
- Dashboard com estatísticas
- Integração com OpenAI GPT-4
- Sistema de tags e prioridades
- Subtarefas
- Interface moderna com React + TypeScript
- Backend robusto com Spring Boot

#### Recursos de IA
- Análise inteligente de tarefas
- Sugestão automática de prioridades
- Geração de subtarefas
- Estimativa de tempo
- Sugestão de tags

#### Infraestrutura
- Docker e docker-compose
- PostgreSQL e H2
- Documentação completa
- Scripts de inicialização
