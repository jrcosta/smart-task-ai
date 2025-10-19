# GitHub Actions - Setup & Configuration Guide

## 🚀 Quick Setup

### 1. Clone o Repositório

```bash
git clone https://github.com/jrcosta/smart-task-ai.git
cd smart-task-ai
```

### 2. Instale Dependências Locais

```bash
# Backend
cd backend
mvn clean install

# Frontend
cd frontend
npm install
```

### 3. Configure GitHub Secrets

Vá para **Settings → Secrets and variables → Actions** e adicione:

#### SONAR_TOKEN (Recomendado)

1. Acesse [SonarCloud](https://sonarcloud.io)
2. Login com GitHub
3. Crie um token em **Account → Security**
4. Copie o token
5. Adicione em GitHub secrets como `SONAR_TOKEN`

```bash
# Criar organization no SonarCloud
Nome: seu-username
Key: seu-username
```

---

## 📊 Workflows Disponíveis

### 1. Code Validation ✅

Executa em:
- Push para `master`, `develop`, `feature/**`
- Pull requests

**Validações**:
```
Backend:
  ✓ Compilação Maven
  ✓ Checkstyle
  ✓ SpotBugs
  ✓ PMD
  ✓ Testes JUnit

Frontend:
  ✓ ESLint
  ✓ TypeScript
  ✓ Prettier
  ✓ Build
```

### 2. Automated Tests 🧪

Executa em:
- Push para `master`, `develop`
- Pull requests
- Diariamente (2 AM UTC)

**Testes**:
```
Backend:
  ✓ JUnit 5
  ✓ Integration Tests
  ✓ Coverage Report

Frontend:
  ✓ Vitest/Jest
  ✓ Coverage Report

Integration:
  ✓ Docker compose up
  ✓ Health checks
```

### 3. Security Analysis 🔒

Executa em:
- Push para `master`, `develop`
- Pull requests
- Semanalmente (3 AM UTC domingo)

**Scans**:
```
✓ OWASP Dependency-Check
✓ CodeQL (Java + JS/TS)
✓ SonarCloud Security
✓ Trivy (Container Images)
✓ Secret Scanning
✓ License Compliance
```

### 4. Build & Release 🎯

Executa em:
- Push de tags `v*.*.*`
- Manual trigger

**Passos**:
```
✓ Build Backend JAR
✓ Build Frontend dist
✓ Build Docker Images
✓ Create GitHub Release
```

---

## 🔧 Configuração por Componente

### Backend (Java 25 + Maven)

**Arquivo**: `backend/pom.xml`

**Plugins necessários para CI/CD**:

```xml
<!-- Checkstyle -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.1</version>
</plugin>

<!-- SpotBugs -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.8.1.1</version>
</plugin>

<!-- PMD -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.21.2</version>
</plugin>

<!-- JaCoCo (Coverage) -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>
```

### Frontend (React 19 + npm)

**Arquivo**: `frontend/package.json`

**Scripts necessários**:

```json
{
  "scripts": {
    "lint": "eslint src/ --ext .ts,.tsx",
    "type-check": "tsc --noEmit",
    "format:check": "prettier --check src/",
    "build": "tsc && vite build",
    "test": "vitest run",
    "test:coverage": "vitest run --coverage"
  }
}
```

**Dependências de dev**:

```json
{
  "devDependencies": {
    "eslint": "^8.x",
    "@typescript-eslint/eslint-plugin": "^6.x",
    "@typescript-eslint/parser": "^6.x",
    "prettier": "^3.x",
    "vitest": "^0.x",
    "@vitest/coverage-v8": "^0.x"
  }
}
```

---

## 📋 Checklist de Setup

```
[ ] Clonar repositório
[ ] Instalar JDK 25
[ ] Instalar Node.js 20.x
[ ] Instalar Maven 3.9.x
[ ] npm install (frontend)
[ ] mvn clean install (backend)
[ ] Criar SonarCloud account
[ ] Adicionar SONAR_TOKEN secret
[ ] Fazer push para ativar pipelines
[ ] Verificar GitHub Actions tab
```

---

## 🚀 Trigger Pipelines

### Automaticamente

```bash
# Faz push → Code Validation executa
git add .
git commit -m "Feature: xyz"
git push origin feature/xyz

# Cria PR → Todos os workflows executam
# (via GitHub web interface)

# Merge para master/develop → Security Analysis
```

### Manualmente

```bash
# Build & Release
# Settings → Actions → Build & Release → Run workflow
```

---

## 📈 Monitorar Pipelines

### via CLI

```bash
# Instalar GitHub CLI
# https://cli.github.com

gh run list
gh run view <run-id>
gh run view <run-id> --log
```

### via Web

1. Acesse **GitHub → Actions**
2. Veja todos os workflows
3. Clique em um para ver detalhes
4. Clique em uma run para ver logs

---

## ✅ Status Badges

Adicione ao `README.md`:

```markdown
## Status das Pipelines

[![Code Validation](https://github.com/jrcosta/smart-task-ai/actions/workflows/code-validation.yml/badge.svg)](https://github.com/jrcosta/smart-task-ai/actions/workflows/code-validation.yml)
[![Tests](https://github.com/jrcosta/smart-task-ai/actions/workflows/automated-tests.yml/badge.svg)](https://github.com/jrcosta/smart-task-ai/actions/workflows/automated-tests.yml)
[![Security](https://github.com/jrcosta/smart-task-ai/actions/workflows/security-analysis.yml/badge.svg)](https://github.com/jrcosta/smart-task-ai/actions/workflows/security-analysis.yml)
```

---

## 🔐 Proteger Branch

**Settings → Branches → Add rule**

```
Branch name pattern: master

Require status checks to pass before merging:
  ✓ Code Validation Pipeline
  ✓ Automated Tests
  ✓ Security Analysis

Require code reviews before merging:
  ✓ Require at least 1 review

Restrict who can dismiss pull request reviews:
  ✓ Enable
```

---

## 📊 Exemplo de Run bem-sucedida

```
✅ Code Validation Pipeline
   ✓ backend-validation (5m 32s)
   ✓ frontend-validation (3m 18s)
   ✓ sonarcloud (2m 45s)
   ✓ security-check (4m 10s)
   ✓ docker-check (6m 22s)
   ✓ validation-summary (15s)

✅ Automated Tests
   ✓ backend-tests (12m 45s)
   ✓ frontend-tests (5m 20s)
   ✓ integration-tests (18m 30s)
   ✓ coverage-report (2m 15s)

✅ Security Analysis
   ✓ dependency-check (3m 20s)
   ✓ codeql-java (15m 10s)
   ✓ codeql-javascript (8m 45s)
   ✓ sonarcloud-security (4m 30s)
   ✓ container-scan (6m 20s)
   ✓ secret-scan (2m 15s)
   ✓ license-check (1m 30s)
   ✓ security-summary (10s)
```

---

## 🆘 Troubleshooting

### "SONAR_TOKEN not found"

```bash
# Adicione em GitHub → Settings → Secrets
# Nome: SONAR_TOKEN
# Valor: seu-token-sonarcloud
```

### "Maven build failed"

```bash
# Local: mvn clean compile
# Corrija erros
# Commit & push
```

### "GitHub Actions quota exceeded"

```
# Free tier: 2000 minutes/mês
# Se exceder, upgrade o plano
# Ou ajuste agendamento de workflows
```

---

## 📚 Referências

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven CI/CD Guide](https://maven.apache.org/)
- [npm CI/CD Guide](https://docs.npmjs.com/cli/v8/using-npm/ci)
- [SonarCloud Setup](https://sonarcloud.io/documentation)

---

**Status**: ✅ Pronto para usar  
**Próximo**: Fazer push e testar pipelines!
