# 🔧 Guia de Configuração do Ambiente

## ⚠️ Problema Atual

Você tem:
- ✅ **Java 25** (compatível)
- ❌ **Node.js 12.22.9** (muito antigo, precisa de 14+)

## 📦 Atualizar Node.js

### Opção 1: Usando NVM (Recomendado)

```bash
# Instalar NVM
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash

# Recarregar o terminal
source ~/.bashrc

# Instalar Node.js 18 LTS
nvm install 18
nvm use 18
nvm alias default 18

# Verificar
node -v  # Deve mostrar v18.x.x
npm -v   # Deve mostrar 9.x.x ou superior
```

### Opção 2: Usando apt (Ubuntu/Debian)

```bash
# Remover versão antiga
sudo apt remove nodejs npm

# Adicionar repositório NodeSource
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -

# Instalar Node.js 18
sudo apt install -y nodejs

# Verificar
node -v
npm -v
```

### Opção 3: Download Manual

Baixe e instale do site oficial: https://nodejs.org/

## 🚀 Depois de Atualizar o Node.js

### 1. Limpar e Reinstalar Frontend

```bash
cd /home/liliane/CascadeProjects/smart-task-manager/frontend
rm -rf node_modules package-lock.json
npm install
```

### 2. Executar o Backend

```bash
cd /home/liliane/CascadeProjects/smart-task-manager/backend
mvn clean install
mvn spring-boot:run
```

O backend estará em: http://localhost:8080

### 3. Executar o Frontend

```bash
cd /home/liliane/CascadeProjects/smart-task-manager/frontend
npm run dev
```

O frontend estará em: http://localhost:3000

## 🐳 Alternativa: Usar Docker

Se preferir não instalar Node.js localmente, use Docker:

```bash
cd /home/liliane/CascadeProjects/smart-task-manager
docker-compose up -d
```

Isso executará tudo em containers sem precisar instalar nada localmente.

## ✅ Verificar Versões

Após a instalação, verifique:

```bash
java -version    # Deve ser 17+
node -v          # Deve ser 14+
npm -v           # Deve ser 8+
mvn -version     # Qualquer versão 3.x
```

## 📝 Próximos Passos

1. Atualize o Node.js
2. Reinstale as dependências do frontend
3. Execute o backend com Maven
4. Execute o frontend com npm
5. Acesse http://localhost:3000 e crie sua conta!
