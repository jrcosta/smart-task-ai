# 🐳 Infrastructure (Docker & Observabilidade)

Configurações de infraestrutura, Docker Compose e ferramentas de observabilidade.

## 📂 Estrutura

```
infrastructure/
├── docker-compose.yml              # Produção principal
├── docker-compose-observability.yml # Stack de observabilidade (Prometheus, Grafana, Jaeger)
└── README.md                       # Este arquivo
```

## 🚀 Docker Compose

### Produção Principal

Inicie a aplicação completa com:

```bash
docker-compose up -d
```

**O que será iniciado:**
- Backend (Spring Boot) na porta 8080
- Frontend (React) na porta 3000
- PostgreSQL na porta 5432

**Parar:**
```bash
docker-compose down
```

**Ver logs:**
```bash
docker-compose logs -f
```

---

### Observabilidade (Prometheus, Grafana, Jaeger)

Para iniciar o stack de observabilidade:

```bash
docker-compose -f docker-compose-observability.yml up -d
```

**O que será iniciado:**
- Prometheus (métricas) - http://localhost:9090
- Grafana (dashboards) - http://localhost:3000
- Jaeger (tracing distribuído) - http://localhost:6831 (UDP)
- Jaeger UI - http://localhost:16686

**Parar:**
```bash
docker-compose -f docker-compose-observability.yml down
```

---

## 📊 Observabilidade

Para detalhes sobre observabilidade e OpenTelemetry, consulte:
- [`docs/OBSERVABILITY.md`](../docs/OBSERVABILITY.md)
- [`docs/OBSERVABILITY_POINTS.md`](../docs/OBSERVABILITY_POINTS.md)
- [`docs/QUICKSTART_OBSERVABILITY.md`](../docs/QUICKSTART_OBSERVABILITY.md)

---

## 🔧 Configuração do Backend para Observabilidade

O backend está configurado com OpenTelemetry para exportar:
- **Traces** para Jaeger
- **Métricas** para Prometheus

Veja `backend/src/main/resources/application.yml` para configuração.

---

## 📝 Variáveis de Ambiente

Configure em `.env` ou `.env.local`:

```bash
# Observabilidade
OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
JAEGER_AGENT_HOST=localhost
JAEGER_AGENT_PORT=6831
```

---

## 🐳 Troubleshooting

**"Port already in use"**
```bash
# Listar containers rodando
docker ps

# Parar container específico
docker stop <container_id>
```

**"Cannot connect to Docker daemon"**
- Certifique-se que Docker está rodando
- No Windows/Mac, inicie Docker Desktop

**"Volumes permission denied"**
- No Linux, execute com `sudo`
- Ou adicione seu usuário ao grupo docker: `sudo usermod -aG docker $USER`

---

**Última atualização**: Outubro 2025
