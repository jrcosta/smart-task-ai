# 🔧 Scripts de Inicialização

Scripts utilitários para iniciar o backend e frontend da aplicação.

## 📋 Scripts Disponíveis

### `start-backend-with-env.sh`
**Descrição**: Inicia o backend carregando variáveis de ambiente do arquivo `.env`.

```bash
./start-backend-with-env.sh
```

**Funcionalidades**:
- Carrega variáveis de `.env`
- Valida credenciais do Twilio
- Para processos anteriores
- Inicializa SDKMAN (se disponível)
- Executa `mvn spring-boot:run` com variáveis passadas como argumentos

**Pré-requisitos**:
- `.env` configurado em `backend/.env`
- SDKMAN instalado (opcional)
- Maven 3.8+
- Java 25+

---

### `start-backend.sh`
**Descrição**: Inicia o backend de forma simples sem carregar `.env`.

```bash
./start-backend.sh
```

**Funcionalidades**:
- Para processos anteriores
- Inicializa SDKMAN
- Executa `mvn spring-boot:run`

**Quando usar**: Desenvolvimento rápido ou quando variáveis já estão configuradas globalmente.

---

### `start-backend-fix.sh`
**Descrição**: Versão corrigida do script de inicialização com melhorias.

```bash
./start-backend-fix.sh
```

**Melhorias**:
- Melhor tratamento de erros
- Suporte melhorado para diferentes shells
- Verificações adicionais de dependências

---

### `start-frontend.sh`
**Descrição**: Inicia o frontend em modo de desenvolvimento.

```bash
./start-frontend.sh
```

**Funcionalidades**:
- Navega até `frontend/`
- Instala dependências (se necessário)
- Executa `npm run dev`

**Acesso**: Frontend disponível em `http://localhost:3000`

---

### 📚 Scripts de Documentação JavaDoc

#### `RUN-JAVADOC.bat` (Windows - Recomendado)
**Descrição**: Script simples para gerar documentação JavaDoc (duplo-clique).

```bash
# Duplo-clique em:
RUN-JAVADOC.bat

# Ou via linha de comando:
cmd /c RUN-JAVADOC.bat
```

**Funcionalidades**:
- ✅ Verifica Maven
- ✅ Compila projeto
- ✅ Gera documentação JavaDoc
- ✅ Abre navegador automaticamente com resultado

**Resultado**: `backend/target/site/apidocs/index.html`

---

#### `generate-javadoc.bat` (Windows - Alternativo)
**Descrição**: Script batch alternativo para gerar JavaDoc.

```bash
generate-javadoc.bat
```

---

#### `generate-javadoc.ps1` (PowerShell - Alternativo)
**Descrição**: Script PowerShell para gerar JavaDoc com Maven.

```bash
powershell -ExecutionPolicy Bypass -File .\generate-javadoc.ps1
```

---

## 🚀 Uso em Diferentes Plataformas

### Windows (Recomendado)
```bash
# Para gerar JavaDoc:
scripts/RUN-JAVADOC.bat

# Ou duplo-clique em RUN-JAVADOC.bat na pasta scripts/
```

### Linux/macOS
```bash
# Para gerar JavaDoc via Maven:
cd backend
mvn javadoc:aggregate
```

---

## 🐧 Uso no WSL

Se você estiver usando WSL e encontrar erro "cannot execute: required file not found", execute os scripts assim:

```bash
bash scripts/start-backend-with-env.sh
bash scripts/start-backend.sh
bash scripts/start-frontend.sh
bash scripts/validate-observability.sh
```

Ou, para resolver permanentemente, execute dentro do WSL:

```bash
# Corrigir finais de linha (CRLF -> LF)
sed -i 's/\r$//' scripts/*.sh

# Dar permissão de execução
chmod +x scripts/*.sh
```

---

## 📦 Docker Alternative

Se preferir usar Docker para iniciar tudo:

```bash
# Ir até a raiz do projeto
cd ..

# Iniciar com Docker Compose
docker-compose up -d
```

Isso iniciará:
- Backend em `http://localhost:8080`
- Frontend em `http://localhost:3000`
- PostgreSQL em `localhost:5432`

---

## 🔍 Troubleshooting

### "Command not found"
- Certifique-se que está executando de dentro da pasta `scripts/` ou com caminho relativo: `./scripts/start-backend.sh`
- No WSL, use: `bash scripts/start-backend.sh`

### "SDKMAN not found"
- Não há problema. O script verificará se SDKMAN existe antes de tentar usá-lo.
- Certifique-se que Java 25+ está instalado e no PATH.

### "Port already in use"
- Backend: `lsof -i :8080` (Linux/Mac) ou `netstat -ano | findstr :8080` (Windows)
- Frontend: `lsof -i :3000` (Linux/Mac) ou `netstat -ano | findstr :3000` (Windows)
- Mate o processo anterior com `kill <PID>` ou `taskkill /PID <PID> /F`

---

**Última atualização**: Outubro 2025
