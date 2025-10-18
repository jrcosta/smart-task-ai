# 📚 Índice do Projeto - Smart Task Manager

Bem-vindo! Este arquivo guia você pela estrutura reorganizada do projeto.

## 📂 Estrutura de Diretórios

```
smart-task-ai/
├── backend/              # Backend Java com Spring Boot
├── frontend/             # Frontend React com TypeScript
├── docs/                 # 📖 Documentação do projeto
├── scripts/              # 🔧 Scripts de inicialização e utilitários
├── infrastructure/       # 🐳 Docker Compose e observabilidade
├── observability/        # 📊 Configurações de observabilidade (Prometheus, Grafana)
├── docker-compose.yml    # Composição Docker (em infrastructure/)
├── README.md             # Documentação principal
└── INDEX.md              # Este arquivo
```

## 📖 Documentação (pasta `docs/`)

| Arquivo | Descrição |
|---------|-----------|
| **[QUICKSTART.md](docs/QUICKSTART.md)** | 🚀 Guia rápido para começar em 5 minutos |
| **[SETUP.md](docs/SETUP.md)** | ⚙️ Instruções detalhadas de configuração |
| **[START_BACKEND.md](docs/START_BACKEND.md)** | 🖥️ Como iniciar o backend |
| **[WHATSAPP_SETUP.md](docs/WHATSAPP_SETUP.md)** | 📱 Configuração de notificações WhatsApp |
| **[WHATSAPP_FEATURE.md](docs/WHATSAPP_FEATURE.md)** | 📲 Detalhes da feature de WhatsApp |
| **[SECURITY_IMPROVEMENTS.md](docs/SECURITY_IMPROVEMENTS.md)** | 🔐 Melhorias de segurança implementadas |
| **[AGENTS.md](docs/AGENTS.md)** | 🤖 Documentação de agents e IA |
| **[CONTRIBUTING.md](docs/CONTRIBUTING.md)** | 🤝 Diretrizes para contribuir |
| **[CHANGELOG.md](docs/CHANGELOG.md)** | 📝 Histórico de mudanças |

## 🔧 Scripts (pasta `scripts/`)

Execute os scripts da pasta `scripts/` para iniciar o projeto:

```bash
# Iniciar backend com variáveis de ambiente
./scripts/start-backend-with-env.sh

# Iniciar backend simples
./scripts/start-backend.sh

# Iniciar backend (versão corrigida)
./scripts/start-backend-fix.sh

# Iniciar frontend
./scripts/start-frontend.sh

# Validar observabilidade
./scripts/validate-observability.sh
```

**Nota para WSL**: Use `bash scripts/nome-do-script.sh` se encontrar erros de permissão.

## 🐳 Infrastructure (pasta `infrastructure/`)

Configurações de Docker e observabilidade:

```bash
# Iniciar aplicação completa (Docker Compose)
docker-compose -f infrastructure/docker-compose.yml up -d

# Iniciar stack de observabilidade (Prometheus, Grafana, Jaeger)
docker-compose -f infrastructure/docker-compose-observability.yml up -d

# Acessar:
# - Prometheus: http://localhost:9090
# - Grafana: http://localhost:3000
# - Jaeger UI: http://localhost:16686
```

Veja [`infrastructure/README.md`](infrastructure/README.md) para mais detalhes.

## 🚀 Como Começar

1. **Primeiro acesso?** → Leia [QUICKSTART.md](docs/QUICKSTART.md)
2. **Configuração completa?** → Veja [SETUP.md](docs/SETUP.md)
3. **Quer WhatsApp?** → Consulte [WHATSAPP_SETUP.md](docs/WHATSAPP_SETUP.md)
4. **Docker?** → Execute `docker-compose up -d`

## 📌 Links Úteis

- **[README.md](README.md)** - Documentação principal completa
- **[GitHub](https://github.com/jrcosta/smart-task-ai)** - Repositório do projeto
- **[LICENSE](LICENSE)** - Licença do projeto

## 🤝 Contribuindo

Antes de contribuir, leia [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md) para as diretrizes.

---

**Última atualização**: Outubro 2025  
**Versão**: Smart Task AI v1.0.0
