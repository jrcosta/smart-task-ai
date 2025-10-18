# 🚀 Guia Rápido de Inicialização

## ⚠️ Importante: Recarregar Terminal

Você instalou Node.js 24, mas o terminal atual ainda está usando a versão antiga (12).

**Solução:**
1. **Feche este terminal**
2. **Abra um NOVO terminal**
3. Verifique a versão:
   ```bash
   node -v  # Deve mostrar v24.9.0
   ```

## 🎯 Opção 1: Scripts Automáticos (Mais Fácil)

### Terminal 1 - Backend:
```bash
cd /home/liliane/CascadeProjects/smart-task-manager
./start-backend.sh
```

### Terminal 2 - Frontend (em outro terminal):
```bash
cd /home/liliane/CascadeProjects/smart-task-manager
./start-frontend.sh
```

## 🎯 Opção 2: Manual

### Terminal 1 - Backend:
```bash
cd /home/liliane/CascadeProjects/smart-task-manager/backend
mvn spring-boot:run
```

### Terminal 2 - Frontend:
```bash
cd /home/liliane/CascadeProjects/smart-task-manager/frontend
npm install  # Só na primeira vez
npm run dev
```

## 🌐 Acessar a Aplicação

Após iniciar ambos os servidores:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api

## 📝 Primeiro Acesso

1. Acesse http://localhost:3000
2. Clique em "Cadastre-se"
3. Crie sua conta
4. Comece a criar tarefas!

## 🤖 Usar IA (Opcional)

Para usar os recursos de IA:

1. Obtenha uma chave da API OpenAI em: https://platform.openai.com/api-keys
2. Crie o arquivo `.env` no backend:
   ```bash
   cd /home/liliane/CascadeProjects/smart-task-manager/backend
   nano .env
   ```
3. Adicione:
   ```
   OPENAI_API_KEY=sk-sua-chave-aqui
   ```
4. Reinicie o backend

## ❓ Problemas?

### Node ainda mostra versão 12?
- Feche TODOS os terminais
- Abra um novo terminal
- Verifique: `node -v`

### Porta 8080 já em uso?
```bash
# Encontrar processo usando a porta
lsof -i :8080
# Matar o processo
kill -9 <PID>
```

### Porta 3000 já em uso?
```bash
# Encontrar processo usando a porta
lsof -i :3000
# Matar o processo
kill -9 <PID>
```

## 🐳 Alternativa: Docker

Se preferir usar Docker (não precisa do Node.js local):

```bash
cd /home/liliane/CascadeProjects/smart-task-manager
docker-compose up -d
```

Acesse: http://localhost:3000

## ✅ Checklist

- [ ] Fechei e reabri o terminal
- [ ] `node -v` mostra v24.x.x
- [ ] Backend rodando em http://localhost:8080
- [ ] Frontend rodando em http://localhost:3000
- [ ] Consegui criar uma conta
- [ ] Consegui criar uma tarefa

## 🎉 Pronto!

Seu Smart Task Manager está funcionando! 🚀
