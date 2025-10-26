# ❓ FAQ - Frequently Asked Questions

Respostas para as perguntas mais comuns sobre o Smart Task AI.

## 📋 Índice

- [Geral](#geral)
- [Instalação e Setup](#instalação-e-setup)
- [Uso da Aplicação](#uso-da-aplicação)
- [IA e OpenAI](#ia-e-openai)
- [Notificações WhatsApp](#notificações-whatsapp)
- [Segurança](#segurança)
- [Observabilidade](#observabilidade)
- [Desenvolvimento](#desenvolvimento)
- [Troubleshooting](#troubleshooting)

---

## 🌐 Geral

### O que é o Smart Task AI?
Smart Task AI é um gerenciador de tarefas open source com integração de IA (OpenAI GPT-4), notificações WhatsApp via Twilio, e observabilidade completa. Desenvolvido com Java Spring Boot e React TypeScript.

### É gratuito?
Sim! O Smart Task AI é 100% gratuito e open source sob licença MIT. Você pode usar, modificar e distribuir livremente.

### Preciso pagar pela IA?
A aplicação é gratuita, mas a API da OpenAI é paga (tem tier gratuito limitado). Se não configurar OpenAI, a aplicação usa análise mock e continua funcionando.

### Posso usar sem Docker?
Sim! Você pode rodar backend e frontend localmente com Maven e npm. Docker é apenas a opção mais fácil.

### Funciona no Windows/Mac/Linux?
Sim! A aplicação funciona em qualquer sistema operacional com:
- Docker (recomendado)
- Ou Java 25+ e Node 18+ para execução local

### Existe versão mobile?
Ainda não, mas está no [Roadmap V1.3](Roadmap.md). Por enquanto, use no navegador mobile - a interface é responsiva.

---

## 🛠️ Instalação e Setup

### Qual a forma mais fácil de instalar?
```bash
git clone https://github.com/jrcosta/smart-task-ai.git
cd smart-task-ai/infrastructure
docker-compose-menu.bat  # Windows
# Ou
docker-compose -f docker-compose-unified.yml up -d  # Linux/Mac
```

### Preciso instalar PostgreSQL?
Não! Se usar Docker, o PostgreSQL vem configurado. Se rodar localmente, pode usar H2 (banco em memória) que já vem configurado.

### Como sei que está funcionando?
1. Acesse http://localhost:3000 - deve mostrar tela de login
2. Execute `docker ps` - deve listar containers rodando
3. Acesse http://localhost:8080/api/actuator/health - deve retornar `{"status":"UP"}`

### Portas 3000 ou 8080 já estão em uso, o que fazer?
```bash
# Parar processo na porta 3000 (Windows)
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Parar processo na porta 3000 (Linux/Mac)
lsof -ti:3000 | xargs kill

# Ou edite docker-compose-unified.yml para usar portas diferentes:
ports:
  - "3001:3000"  # Acesse em localhost:3001
```

### Como atualizar para versão mais recente?
```bash
cd smart-task-ai
git pull origin main
cd infrastructure
docker-compose -f docker-compose-unified.yml pull
docker-compose -f docker-compose-unified.yml up -d
```

### Como fazer backup dos dados?
```bash
# Backup do volume do PostgreSQL
docker-compose -f docker-compose-unified.yml exec postgres pg_dump -U postgres smarttask > backup.sql

# Ou exporte via interface (futuro)
Configurações → Dados → Exportar Tarefas
```

---

## 💻 Uso da Aplicação

### Como criar minha primeira tarefa?
1. Faça login em http://localhost:3000
2. Clique em "Nova Tarefa"
3. Preencha título e descrição
4. Clique em "Salvar"

Veja o [Usage Guide](Usage-Guide.md) para detalhes.

### A IA não está funcionando, por quê?
Verifique:
1. Você configurou a API key da OpenAI?
   - Configurações → API Keys → OpenAI API Key
2. A key está correta?
   - Teste em https://platform.openai.com/playground
3. Tem crédito na conta OpenAI?
   - Verifique em https://platform.openai.com/account/usage

Se não configurar, a aplicação usa análise mock (menos precisa).

### Como funciona a estimativa de tempo?
A IA analisa a descrição da tarefa e estima quantas horas serão necessárias baseando-se em:
- Complexidade técnica mencionada
- Quantidade de subtarefas sugeridas
- Palavras-chave de escopo (pequeno, médio, grande)

### Posso ter tarefas recorrentes?
Ainda não, mas está planejado para V1.2. Por enquanto, duplique manualmente ou use a API.

### Como compartilhar tarefas com outras pessoas?
Ainda não suportado. Modo colaborativo está planejado para V2.0 no [Roadmap](Roadmap.md).

### Posso importar tarefas de outras ferramentas (Trello, Asana)?
Não nativamente. Você pode:
1. Exportar de lá em CSV/JSON
2. Usar a API do Smart Task AI para importar
3. Ou esperar por integração nativa (Roadmap)

---

## 🤖 IA e OpenAI

### Preciso de uma conta OpenAI?
Não é obrigatório. A aplicação funciona sem OpenAI, mas os recursos de IA usarão análise mock (menos precisa).

### Como obter uma API key da OpenAI?
1. Acesse https://platform.openai.com
2. Crie uma conta (ou faça login)
3. Vá em https://platform.openai.com/api-keys
4. Clique em "Create new secret key"
5. Copie a key (começa com `sk-`)
6. Cole em Configurações → API Keys no Smart Task AI

### Quanto custa a API da OpenAI?
OpenAI cobra por token usado:
- GPT-4: ~$0.03 por 1000 tokens de input
- Análise média usa ~500 tokens = $0.015 por análise
- Tier gratuito: $5 de crédito para novos usuários

Veja preços atualizados em https://openai.com/pricing

### A IA está dando sugestões ruins, como melhorar?
1. **Seja mais específico na descrição**: Mais detalhes = melhores sugestões
2. **Use palavras-chave técnicas**: "backend API REST" vs "fazer API"
3. **Mencione tecnologias**: "React component" vs "tela"
4. **Inclua contexto**: "urgente para cliente" vs sem contexto

### Minha chave OpenAI está segura?
Sim! A chave é:
- Armazenada criptografada no banco de dados
- Transmitida via HTTPS
- Nunca exposta no frontend
- Acessível apenas pelo seu usuário

### Posso usar outro modelo de IA além de OpenAI?
Atualmente apenas OpenAI GPT-4 é suportado. Suporte para outros modelos (Claude, Llama, etc.) está em consideração.

---

## 📱 Notificações WhatsApp

### Preciso de uma conta Twilio?
Sim, para receber notificações no WhatsApp. Mas a aplicação funciona perfeitamente sem.

### Como criar conta Twilio?
1. Acesse https://www.twilio.com/try-twilio
2. Preencha o formulário
3. Verifique seu email e telefone
4. Acesse o Console
5. Configure WhatsApp Sandbox

Tutorial completo em [Getting Started](Getting-Started.md).

### É gratuito?
Twilio oferece crédito gratuito para testes. Para produção, há custos:
- WhatsApp: ~$0.005 por mensagem
- Crédito trial: ~$15
- Suficiente para ~3000 mensagens de teste

### Posso usar meu próprio número WhatsApp?
Não diretamente. Você precisa:
- Usar o Sandbox da Twilio (grátis, para testes)
- Ou solicitar um WhatsApp Business Number (pago, produção)

### As notificações não estão chegando, por quê?
Verifique:
1. ✓ Credenciais Twilio estão corretas?
2. ✓ Seu número está no formato correto? (+5511999999999)
3. ✓ Você conectou ao Sandbox? (enviou código para Twilio)
4. ✓ Tem crédito na conta Twilio?
5. ✓ As notificações estão ativadas nas configurações?

### Como funciona o Sandbox da Twilio?
```
1. Twilio te dá um número: +1-415-523-8886
2. Você envia mensagem para ele com código: "join <seu-codigo>"
3. Agora pode receber mensagens desse número
4. Sandbox expira em 3 dias de inatividade
5. Re-envie "join <codigo>" para reativar
```

### Posso receber notificações por Email em vez de WhatsApp?
Ainda não implementado, mas está no Roadmap V1.1.

---

## 🔐 Segurança

### Meus dados estão seguros?
Sim! Implementamos:
- Autenticação JWT
- Senhas com hash bcrypt
- HTTPS em produção
- SQL injection prevention (JPA)
- XSS protection
- CORS configurado

### Como funciona a autenticação?
1. Você faz cadastro/login
2. Backend gera um JWT token
3. Token é armazenado no navegador (localStorage)
4. Toda requisição inclui o token
5. Backend valida token antes de processar

### Posso ter 2FA (autenticação de dois fatores)?
Ainda não, mas está planejado para V1.2.

### Como trocar minha senha?
```
Login → Configurações → Segurança → Alterar Senha
```

### Esqueci minha senha, o que fazer?
Reset de senha ainda não implementado. Por enquanto:
1. Acesse o banco de dados diretamente
2. Ou delete e recrie a conta

(Reset de senha será adicionado em breve)

### Posso ter múltiplas contas?
Sim, basta usar emails diferentes. Mas cada conta é independente (sem compartilhamento de tarefas).

---

## 📊 Observabilidade

### O que é observabilidade?
É a capacidade de entender o estado interno da aplicação através de:
- **Logs**: O que aconteceu
- **Métricas**: Quantas vezes, quão rápido
- **Traces**: Caminho de uma requisição

### Para que serve?
- Debug de problemas
- Monitorar performance
- Identificar gargalos
- Alertas de falhas
- Análise de uso

### Preciso usar Prometheus/Grafana/Jaeger?
Não! São opcionais, para quem quer:
- Monitorar produção
- Estudar observabilidade
- Debug avançado

A aplicação funciona sem eles.

### Como acesso Grafana?
```
1. Inicie com: docker-compose -f docker-compose-unified.yml --profile observability up -d
2. Acesse: http://localhost:3001
3. Login: admin / admin
4. Explore os dashboards
```

### Como acesso Jaeger (traces)?
```
1. Inicie com profile observability
2. Acesse: http://localhost:16686
3. Selecione "smart-task-backend"
4. Clique em "Find Traces"
5. Explore os traces de requisições
```

### Quais métricas são coletadas?
```
- http_server_requests_seconds: Latência HTTP
- jvm_memory_used_bytes: Memória da JVM
- database_queries_total: Queries ao banco
- ai_analysis_duration_seconds: Tempo de análise IA
- whatsapp_messages_sent_total: Mensagens enviadas
- E muitas outras...
```

Veja [Observability Guide](../docs/OBSERVABILITY.md) para detalhes.

---

## 💻 Desenvolvimento

### Como configurar ambiente de desenvolvimento?
Veja o [Development Guide](Development-Guide.md) completo.

```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend (outro terminal)
cd frontend
npm install
npm run dev
```

### Quais tecnologias preciso conhecer?
**Backend:**
- Java 25
- Spring Boot 3.5
- Spring Data JPA
- Spring Security
- Maven

**Frontend:**
- React 19
- TypeScript
- TailwindCSS
- TanStack Query
- Zustand

### Como adicionar uma nova feature?
1. Leia [Contributing Guide](../docs/CONTRIBUTING.md)
2. Abra uma issue ou discuta primeiro
3. Crie branch: `feature/minha-feature`
4. Implemente com testes
5. Abra Pull Request

### Como rodar os testes?
```bash
# Backend
cd backend
mvn test

# Frontend
cd frontend
npm test
```

### Onde encontro a documentação da API?
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/docs
- **Wiki**: [API Documentation](API-Documentation.md)

### Como gerar JavaDoc?
```bash
cd backend
mvn javadoc:aggregate
# Abra: backend/target/site/apidocs/index.html
```

---

## 🆘 Troubleshooting

### Docker não inicia
```bash
# Verifique se Docker está rodando
docker ps

# Veja logs de erro
docker-compose -f docker-compose-unified.yml logs

# Reconstrua as imagens
docker-compose -f docker-compose-unified.yml build --no-cache
docker-compose -f docker-compose-unified.yml up -d
```

### Backend não conecta ao banco
```bash
# Verifique se PostgreSQL está rodando
docker-compose -f docker-compose-unified.yml ps postgres

# Veja logs do banco
docker-compose -f docker-compose-unified.yml logs postgres

# Reinicie o banco
docker-compose -f docker-compose-unified.yml restart postgres
```

### Frontend mostra tela branca
```bash
# Veja logs do navegador (F12 → Console)
# Verifique se backend está rodando:
curl http://localhost:8080/api/actuator/health

# Limpe cache do navegador:
Ctrl+Shift+Delete → Limpar tudo

# Reconstrua frontend:
docker-compose -f docker-compose-unified.yml build frontend
docker-compose -f docker-compose-unified.yml up -d frontend
```

### Erro 401 Unauthorized
Seu token JWT expirou ou é inválido:
1. Faça logout
2. Faça login novamente
3. Se persistir, limpe localStorage: F12 → Application → Local Storage → Clear

### Erro "CORS policy"
```bash
# Backend não está permitindo requisições do frontend
# Verifique se backend está rodando na porta correta
# Frontend deve fazer requisições para http://localhost:8080/api
```

### Mais problemas?
Veja [Troubleshooting Guide](Troubleshooting.md) completo.

---

## 💬 Outras Perguntas

### Posso contribuir para o projeto?
Sim! Leia [Contributing Guide](../docs/CONTRIBUTING.md) e [Roadmap](Roadmap.md).

### Como reporto um bug?
Abra uma [issue](https://github.com/jrcosta/smart-task-ai/issues/new?template=bug_report.md) no GitHub.

### Como sugiro uma feature?
Abra uma [feature request](https://github.com/jrcosta/smart-task-ai/issues/new?template=feature_request.md).

### Onde posso tirar outras dúvidas?
- 💬 [GitHub Discussions](https://github.com/jrcosta/smart-task-ai/discussions)
- 📧 Email: [Abra uma issue no GitHub]
- 📖 [Wiki completa](Home.md)

### O projeto está ativo?
Sim! Desenvolvimento contínuo. Veja:
- [Changelog](../docs/CHANGELOG.md)
- [Roadmap](Roadmap.md)
- [Commits recentes](https://github.com/jrcosta/smart-task-ai/commits)

---

## 📚 Recursos Adicionais

- 🏠 [Home](Home.md) - Página principal da wiki
- 🚀 [Getting Started](Getting-Started.md) - Começar a usar
- 📖 [Usage Guide](Usage-Guide.md) - Guia completo de uso
- ✨ [Features](Features.md) - Todas as funcionalidades
- 🗺️ [Roadmap](Roadmap.md) - Futuras funcionalidades
- 🔧 [Configuration](Configuration.md) - Configurações avançadas
- 💻 [Development Guide](Development-Guide.md) - Desenvolver o projeto
- 🚢 [Deployment](Deployment.md) - Deploy em produção

---

**Não encontrou sua pergunta?**

Abra uma [discussion](https://github.com/jrcosta/smart-task-ai/discussions) no GitHub!

*Última atualização: Outubro 2025 | Versão: 1.0.0*
