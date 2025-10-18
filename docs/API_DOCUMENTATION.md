# 📚 Documentação de API com Swagger/OpenAPI

Este projeto utiliza **Swagger/OpenAPI 3.0** para documentação automática e interativa da API.

## 🔍 Acessando a Documentação

Após iniciar o backend, acesse a documentação em:

| Recurso | URL |
|---------|-----|
| **Swagger UI (Recomendado)** | http://localhost:8080/api/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/api/docs |
| **OpenAPI YAML** | http://localhost:8080/api/docs.yaml |

## 🧪 Usando o Swagger UI

### 1. Visualizar Endpoints

O Swagger UI mostra todos os endpoints organizados por tags:
- **auth** - Autenticação e login
- **tasks** - Gerenciamento de tarefas
- **ai** - Recursos de IA
- **notifications** - Notificações
- **actuator** - Observabilidade (métricas, health, etc)

### 2. Entender um Endpoint

Para cada endpoint você verá:
- 📝 **Descrição**: O que o endpoint faz
- 🔧 **Método HTTP**: GET, POST, PUT, DELETE, etc
- 📥 **Parâmetros**: Path params, query params, body
- 📤 **Respostas**: Códigos HTTP e schemas de resposta
- 🔐 **Segurança**: Quais campos requerem autenticação

### 3. Testar Endpoints

Clique em um endpoint para expandir e ver o botão "Try it out":

```
1. Clique em "Try it out"
2. Preencha os parâmetros (se houver)
3. Clique em "Execute"
4. Veja a requisição enviada e a resposta recebida
```

## 🔐 Autenticação com JWT

### Fluxo de Autenticação

1. **Registrar novo usuário**
   - Endpoint: `POST /api/auth/register`
   - Não requer autenticação
   - Retorna: username, email, token JWT

2. **Fazer login**
   - Endpoint: `POST /api/auth/login`
   - Não requer autenticação
   - Retorna: token JWT

3. **Usar token em requisições protegidas**
   - No Swagger UI, clique em "Authorize"
   - Cole o token no formato: `Bearer {seu-token-aqui}`
   - Todos os endpoints protegidos estarão disponíveis

### Exemplo de Fluxo no Swagger

```
1. Expanda POST /auth/register
2. Clique "Try it out"
3. Preencha: username, email, password, fullName
4. Execute e copie o "token" da resposta
5. Clique no botão "Authorize" (cadeado no topo)
6. Cole: Bearer {token-copiado}
7. Agora todos os endpoints protegidos funcionam!
```

## 📊 Endpoints Principais

### Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/register` | Registrar novo usuário |
| POST | `/auth/login` | Fazer login e obter token |

### Tarefas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/tasks` | Listar todas as tarefas do usuário |
| POST | `/tasks` | Criar nova tarefa |
| GET | `/tasks/{id}` | Obter detalhes de uma tarefa |
| PUT | `/tasks/{id}` | Atualizar uma tarefa |
| DELETE | `/tasks/{id}` | Deletar uma tarefa |
| POST | `/tasks/ai` | Criar tarefa com análise de IA |

### IA

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/ai/analyze` | Analisar texto com IA |

### Notificações

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/notifications` | Listar notificações |
| POST | `/notifications/schedule` | Agendar notificação |

### Observabilidade (Actuator)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/actuator/health` | Status da aplicação |
| GET | `/actuator/metrics` | Métricas disponíveis |
| GET | `/actuator/prometheus` | Métricas em formato Prometheus |

## 🔗 Download da Especificação

Você pode baixar a especificação completa em formato:

- **JSON**: http://localhost:8080/api/docs
- **YAML**: http://localhost:8080/api/docs.yaml

Use estas URLs em ferramentas como:
- **Postman**: Importar → Link → Cole a URL
- **Insomnia**: File → Import → URL
- **API Clients**: Qualquer ferramenta que suporte OpenAPI 3.0

## 📱 Testando em Aplicações Externas

### Importar no Postman

1. Abra Postman
2. Clique em "Import"
3. Selecione "Link"
4. Cole: `http://localhost:8080/api/docs.json`
5. Pronto! Todos os endpoints estarão disponíveis no Postman

### Importar no Insomnia

1. Abra Insomnia
2. File → Import → URL
3. Cole: `http://localhost:8080/api/docs.json`
4. Pronto! Todos os endpoints estarão disponíveis no Insomnia

## 🔍 Explorando Schemas

No Swagger UI, role para o final da página para ver "Schemas":

- Veja a estrutura completa de cada modelo
- Entenda os tipos de dados
- Veja exemplos de requisições e respostas

## ❓ Dicas Úteis

| Situação | Solução |
|----------|---------|
| Token expirou | Faça login novamente em `/auth/login` |
| Erro 401 (Unauthorized) | Clique em "Authorize" e adicione o token |
| Erro 422 (Unprocessable Entity) | Verifique os tipos de dados no Swagger UI |
| Endpoint não aparece | Expanda a tag correspondente |
| Precisa testar sem token | Use endpoints públicos como `/auth/register` |

## 📚 Links Úteis

- **[OpenAPI 3.0 Spec](https://spec.openapis.org/oas/v3.0.3)** - Especificação oficial
- **[SpringDoc OpenAPI](https://springdoc.org/)** - Documentação do SpringDoc
- **[Swagger UI](https://swagger.io/tools/swagger-ui/)** - Documentação do Swagger UI
- **[README.md](../README.md)** - Voltar ao README principal

---

**Última atualização**: Outubro 2025  
**Versão**: Smart Task AI v1.0.0
