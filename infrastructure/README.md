# 🐳 Infrastructure (Docker & Observabilidade)

Configurações de infraestrutura, Docker Compose e ferramentas de observabilidade.

## 📂 Estrutura

```
infrastructure/
├── docker-compose-unified.yml      # ✨ NOVO - Arquivo único unificado com Profiles
├── docker-compose.yml              # (legado) Produção principal
├── docker-compose-observability.yml # (legado) Stack de observabilidade
├── docker-compose-menu.bat          # ✨ NOVO - Menu auxiliar (Windows)
├── DOCKER_SETUP.md                 # ✨ NOVO - Documentação completa
└── README.md                       # Este arquivo
```

## 🎯 Recomendação (NOVO!)

**Use o arquivo `docker-compose-unified.yml` - é o melhor para estudo!**

### Por que usar o arquivo unificado?

✅ Um único arquivo para todas as situações  
✅ Suporte a Profiles para flexibilidade  
✅ Melhor para fins de estudo  
✅ Documentação completa em `DOCKER_SETUP.md`  

---

## 🚀 Como Usar (Quick Start)

### Opção 1: Menu Interativo (Windows)

```bash
cd infrastructure
docker-compose-menu.bat
```

Escolha a opção desejada e o script cuida de tudo!

### Opção 2: Modo Completo (Backend + Frontend + BD + Observabilidade)

```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml up -d
```

Acesse:
- 🎨 Frontend: http://localhost:3000
- 🔙 Backend: http://localhost:8080/api
- 📈 Grafana: http://localhost:3001 (admin/admin)
- 🔍 Jaeger: http://localhost:16686

### Opção 3: Só Observabilidade (para Backend local)

```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml --profile observability up -d
```

Depois rode Backend localmente:
```bash
cd backend
mvn spring-boot:run
```

### Parar Tudo

```bash
docker-compose -f docker-compose-unified.yml down
```

---

## 📚 Documentação Completa

**Leia `DOCKER_SETUP.md` para:**
- Variáveis de ambiente
- Troubleshooting
- Exemplos de uso
- Configurações avançadas
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
