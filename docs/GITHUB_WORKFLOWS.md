# 📦 Documentação dos GitHub Workflows - Smart Task AI

Este documento descreve cada workflow de integração contínua (CI) e entrega contínua (CD) configurado no repositório **Smart Task AI**.

---

## 📂 Localização dos Workflows

Todos os workflows estão na pasta:
```
.github/workflows/
```

---

## 🚦 Workflows Existentes

> **Nota:** Os nomes dos arquivos podem variar conforme a evolução do projeto. Consulte sempre a pasta `.github/workflows/` para a lista atualizada.

### 1. **build-backend.yml**

- **Propósito:** Compilar, testar e validar o backend Java (Spring Boot).
- **Disparo:** Push ou Pull Request em qualquer branch.
- **Etapas principais:**
  - Configura ambiente Java 25.
  - Instala dependências Maven.
  - Executa `mvn clean install` (build + testes).
  - Publica artefatos de build (se aplicável).
- **Validações:** Garante que o backend está íntegro e todos os testes passam antes de merge.

---

### 2. **build-frontend.yml**

- **Propósito:** Compilar e testar o frontend React (Vite + TypeScript).
- **Disparo:** Push ou Pull Request em qualquer branch.
- **Etapas principais:**
  - Configura ambiente Node 18+.
  - Instala dependências com `npm install`.
  - Executa `npm run build` e `npm run test` (se definido).
- **Validações:** Garante que o frontend está compilando corretamente e testes automatizados passam.

---

### 3. **docker-image.yml**

- **Propósito:** Construir e publicar imagens Docker do backend e frontend.
- **Disparo:** Push em branches principais (`main`, `master`, `release/*`).
- **Etapas principais:**
  - Build das imagens Docker usando os respectivos Dockerfiles.
  - (Opcional) Push das imagens para um registro Docker (ex: Docker Hub ou GitHub Packages).
- **Validações:** Garante que a aplicação pode ser containerizada sem erros.

---

### 4. **deploy-preview.yml**

- **Propósito:** Fazer deploy de preview (ambiente temporário) para Pull Requests.
- **Disparo:** Pull Request aberto ou atualizado.
- **Etapas principais:**
  - Build do backend e frontend.
  - Deploy em ambiente de preview (ex: Vercel, Netlify, Heroku ou ambiente customizado).
  - Publicação de URL de preview nos comentários do PR.
- **Validações:** Permite revisão visual e funcional antes do merge.

---

### 5. **codeql-analysis.yml**

- **Propósito:** Análise de segurança estática do código (CodeQL).
- **Disparo:** Push ou Pull Request em branches principais.
- **Etapas principais:**
  - Executa análise CodeQL para Java e/ou JavaScript/TypeScript.
  - Reporta vulnerabilidades potenciais.
- **Validações:** Ajuda a identificar falhas de segurança automaticamente.

---

## 📝 Observações Gerais

- Todos os workflows seguem as versões congeladas do projeto (Java 25, Node 18+, etc).
- Workflows de build/teste são obrigatórios para aprovação de Pull Requests.
- O deploy para produção deve ser feito apenas após revisão e aprovação do PR.
- Logs detalhados de cada execução ficam disponíveis na aba "Actions" do GitHub.

---

## 📚 Referências

- [Documentação GitHub Actions](https://docs.github.com/pt/actions)
- [README.md do projeto](../README.md)
- [copilot-instructions.md](../.github/copilot-instructions.md)

---

**Dúvidas?** Consulte o time de DevOps ou abra uma issue!
