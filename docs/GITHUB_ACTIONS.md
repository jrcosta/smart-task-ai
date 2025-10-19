# 🚀 GitHub Actions Pipelines - Documentação

Este documento descreve todas as pipelines de CI/CD implementadas no projeto Smart Task AI.

## 📋 Visão Geral

O projeto possui **4 workflows automáticos** que executam em cada push e pull request:

```
┌─────────────────────────────────────────────────┐
│     GitHub Actions - Pipelines Automáticas     │
├─────────────────────────────────────────────────┤
│                                                 │
│  1️⃣  Code Validation (push/PR)                 │
│  2️⃣  Automated Tests (push/PR/daily)           │
│  3️⃣  Security Analysis (push/PR/weekly)        │
│  4️⃣  Build & Release (on tag)                  │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 1️⃣ Code Validation Pipeline

**Arquivo**: `.github/workflows/code-validation.yml`

**Triggers**:
- ✅ Push para `master`, `develop`, `feature/**`
- ✅ Pull requests para `master`, `develop`

### Etapas Backend (Java)

```bash
✓ Compilation (Maven)
✓ Checkstyle (Code Style)
✓ SpotBugs (Bug Detection)
✓ Unit Tests (JUnit)
✓ PMD (Code Smell Detection)
```

**Validações**:
- Compila código com Java 25
- Verifica estilo com Checkstyle
- Detecta bugs potenciais
- Roda testes unitários
- Verifica padrões de código

### Etapas Frontend (React)

```bash
✓ ESLint (Linting)
✓ TypeScript (Type Checking)
✓ Prettier (Formatting)
✓ Build (Webpack via Vite)
```

**Validações**:
- Lint com ESLint
- Type-check com TypeScript
- Verifica formatação com Prettier
- Testa build completo

### Etapas Adicionais

```bash
✓ SonarCloud Analysis (Code Quality)
✓ Dependency Check (Security)
✓ npm audit (Frontend Dependencies)
✓ Trivy (Container Scanning)
✓ Docker Build Check
```

### Resultado

- ✅ **Pass**: Código segue padrões
- ⚠️ **Warning**: Issues encontradas (não bloqueia)
- ❌ **Fail**: Erros críticos (bloqueia merge)

---

## 2️⃣ Automated Tests Pipeline

**Arquivo**: `.github/workflows/automated-tests.yml`

**Triggers**:
- ✅ Push para `master`, `develop`
- ✅ Pull requests para `master`, `develop`
- ✅ Agendado diariamente (2 AM UTC)

### Backend Tests

```
Job: backend-tests
├─ JUnit 5 Tests
├─ Spring Boot Integration Tests
├─ Test Database (PostgreSQL)
├─ Coverage Report (JaCoCo)
└─ Test Result Publishing
```

**Detalhes**:
- Usa PostgreSQL 15 como banco de testes
- Roda `mvn verify` (testes + integrações)
- Gera relatório de cobertura
- Publica resultados de testes

### Frontend Tests

```
Job: frontend-tests
├─ Vitest (ou Jest)
├─ Coverage Report
└─ Coverage Upload
```

### Integration Tests

```
Job: integration-tests
├─ Build Backend (JAR)
├─ Build Frontend (dist)
├─ Build Docker Images
├─ Start Services
├─ Health Checks
└─ Cleanup
```

**O que testa**:
- Containers iniciam corretamente
- Backend responde em `:8080`
- Frontend está acessível em `:3000`
- PostgreSQL está funcional

### Coverage Report

- Baixa artefatos de cobertura
- Gera relatório consolidado
- Envia para Codecov

---

## 3️⃣ Security Analysis Pipeline

**Arquivo**: `.github/workflows/security-analysis.yml`

**Triggers**:
- ✅ Push para `master`, `develop`
- ✅ Pull requests para `master`, `develop`
- ✅ Agendado semanalmente (3 AM UTC domingo)

### Dependency Scanning

```
✓ OWASP Dependency-Check (Java)
  └─ Identifica dependências vulneráveis
```

### Static Code Analysis (CodeQL)

```
✓ CodeQL - Java Analysis
  └─ Detecta vulnerabilidades em Java

✓ CodeQL - JavaScript/TypeScript
  └─ Detecta vulnerabilidades em JS/TS
```

### Code Quality (SonarCloud)

```
✓ SonarCloud Security Hotspots
  └─ Encontra problemas de segurança
  └─ Calcula score de qualidade
```

### Container Security (Trivy)

```
✓ Trivy Scan Backend
✓ Trivy Scan Frontend
  └─ Vulnerabilidades em imagens Docker
  └─ Analisa camadas
```

### Secret Scanning

```
✓ TruffleHog
  └─ Procura por secrets vazados
  └─ Detecta credenciais
```

### License Compliance

```
✓ Maven License Check
✓ npm License Checker
  └─ Verifica licenças compatíveis
```

---

## 4️⃣ Build & Release Pipeline

**Arquivo**: `.github/workflows/build-release.yml`

**Triggers**:
- ✅ Push de tags `v*.*.*` (ex: v1.0.0)
- ✅ Manual (workflow_dispatch)

### Build Backend

```
✓ Compila JAR
✓ Faz upload como artifact
```

### Build Frontend

```
✓ Build dist
✓ Faz upload como artifact
```

### Build Docker Images

```
✓ Backend Docker Image
✓ Frontend Docker Image
✓ Push para Docker Hub (opcional)
```

### Create Release

```
✓ Cria GitHub Release
✓ Anexa artifacts
✓ Gera release notes
```

---

## 🔧 Configuração Necessária

### Secrets do GitHub

Para funcionar completamente, adicione estes secrets em **Settings → Secrets and variables → Actions**:

```
SONAR_TOKEN
  └─ Token do SonarCloud para análises de qualidade

DOCKER_USERNAME
  └─ Username Docker Hub (opcional)

DOCKER_PASSWORD
  └─ Password Docker Hub (opcional)

GITHUB_TOKEN
  └─ Adicionado automaticamente pelo GitHub
```

### Como Adicionar Secrets

1. Vá para **Settings** do repositório
2. Clique em **Secrets and variables** → **Actions**
3. Clique em **New repository secret**
4. Nome: `SONAR_TOKEN`
5. Valor: Sua chave de SonarCloud
6. Clique em **Add secret**

---

## 📊 Visualizar Resultados

### GitHub Web Interface

1. **Actions tab**: Ver todas as pipelines
2. **Clique em um workflow**: Ver detalhes
3. **Clique em uma run**: Ver steps

### Badge Status

Adicione ao `README.md`:

```markdown
[![Code Validation](https://github.com/jrcosta/smart-task-ai/workflows/Code%20Validation%20Pipeline/badge.svg)](https://github.com/jrcosta/smart-task-ai/actions)
[![Tests](https://github.com/jrcosta/smart-task-ai/workflows/Automated%20Tests/badge.svg)](https://github.com/jrcosta/smart-task-ai/actions)
[![Security](https://github.com/jrcosta/smart-task-ai/workflows/Security%20Analysis/badge.svg)](https://github.com/jrcosta/smart-task-ai/actions)
```

---

## ⏱️ Tempos de Execução

| Pipeline | Tempo Médio |
|----------|------------|
| Code Validation | 5-8 minutos |
| Automated Tests | 10-15 minutos |
| Security Analysis | 15-20 minutos |
| Build & Release | 8-12 minutos |

---

## 🚫 Bloqueios Automáticos

Merge é **bloqueado** se:

```
❌ Backend não compila
❌ Frontend build falha
❌ Testes unitários falham
❌ Code quality score baixo
❌ Vulnerabilidades críticas encontradas
```

---

## 📝 Exemplo de Uso

### 1. Fazer Commit

```bash
git add .
git commit -m "Feature: Adicionar nova funcionalidade"
git push origin feature/nova-funcionalidade
```

### 2. Abrir Pull Request

```
GitHub automatically triggers:
→ Code Validation Pipeline
→ Automated Tests Pipeline
→ Security Analysis Pipeline
```

### 3. Ver Resultados

```
✅ All checks passed → Pode fazer merge
⚠️ Warnings → Pode fazer merge (com cuidado)
❌ Critical errors → Bloqueia merge
```

### 4. Mergear

```bash
# Via GitHub (recomendado)
# Clique em "Merge pull request"

# Via CLI
git checkout develop
git pull origin develop
git merge feature/nova-funcionalidade
git push origin develop
```

### 5. Lançar Release (opcional)

```bash
# Criar tag
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0

# Build & Release Pipeline executa automaticamente
```

---

## 🔍 Troubleshooting

### Pipeline falhando?

1. **Acesse**: GitHub → Actions → Workflow com falha
2. **Clique** em uma run
3. **Veja** os logs detalhados
4. **Corrija** o código baseado na mensagem

### Erro comum: "Maven build failed"

```bash
# Local
cd backend
mvn clean compile
# Corrija os erros

# Commit e push
git add .
git commit -m "Fix: Corrigir erro de compilação"
git push
```

### Erro comum: "npm build failed"

```bash
# Local
cd frontend
npm install
npm run build
# Corrija os erros

# Commit e push
git add .
git commit -m "Fix: Corrigir erro de build frontend"
git push
```

---

## 📚 Próximos Passos

### Melhorias Futuras

- [ ] Integração com Slack (notificações)
- [ ] Adicionar performance benchmarks
- [ ] E2E tests (Cypress/Playwright)
- [ ] Load testing
- [ ] Mutation testing

### Referências

- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [SonarCloud](https://sonarcloud.io)
- [CodeQL](https://codeql.github.com/)
- [Trivy](https://github.com/aquasecurity/trivy)

---

**Data**: Outubro 2025  
**Status**: ✅ Completo  
**Próximo**: Configurar secrets e testar pipelines
