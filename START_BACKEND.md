# 🚀 Como Iniciar o Backend

## ✅ Comandos Corretos

Execute no terminal (na ordem):

```bash
# 1. Ir para a pasta do backend
cd /home/liliane/CascadeProjects/smart-task-manager/backend

# 2. Matar processos anteriores (se houver)
pkill -9 -f "spring-boot:run" 2>/dev/null || true

# 3. Limpar e compilar
bash -c "source ~/.sdkman/bin/sdkman-init.sh && mvn clean install -DskipTests"

# 4. Iniciar o backend
bash -c "source ~/.sdkman/bin/sdkman-init.sh && mvn spring-boot:run"
```

## 📝 Ou em Uma Linha Só

```bash
cd /home/liliane/CascadeProjects/smart-task-manager/backend && pkill -9 -f "spring-boot:run" 2>/dev/null; bash -c "source ~/.sdkman/bin/sdkman-init.sh && mvn clean install -DskipTests && mvn spring-boot:run"
```

## ✅ Aguarde Ver Esta Mensagem

```
Started SmartTaskManagerApplication in X.XXX seconds
Twilio WhatsApp service initialized successfully
```

## 🌐 URLs

- Backend: http://localhost:8080/api
- Frontend: http://localhost:3000

## ❓ Se Der Erro

### Erro: "Porta 8080 já em uso"
```bash
lsof -ti:8080 | xargs kill -9
```

### Erro: "Java version not supported"
```bash
bash -c "source ~/.sdkman/bin/sdkman-init.sh && sdk use java 21-tem"
```

### Ver Logs em Tempo Real
```bash
tail -f /home/liliane/CascadeProjects/smart-task-manager/backend/target/spring-boot.log
```

## 🎯 Checklist

- [ ] Java 21 instalado (via SDKMAN)
- [ ] Maven 3.9+ instalado (via SDKMAN)
- [ ] Arquivo .env configurado com credenciais do Twilio
- [ ] Porta 8080 livre
- [ ] Backend compilado sem erros
- [ ] Backend rodando e respondendo

## 🔧 Verificar se Está Funcionando

```bash
curl http://localhost:8080/api/auth/login
```

Se retornar algo (mesmo que erro 400/500), está funcionando!
