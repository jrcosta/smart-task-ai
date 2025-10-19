# Configuração de Chaves de API via Interface Web

## 📋 Visão Geral

A partir desta versão, o Smart Task AI permite que você configure suas chaves de API (OpenAI e Twilio) diretamente pela interface web, eliminando a necessidade de variáveis de ambiente. Isso torna o setup inicial muito mais simples e seguro.

## 🔒 Segurança

- **Criptografia AES**: Todas as chaves são criptografadas antes de serem armazenadas no banco de dados
- **Isolamento por usuário**: Cada usuário tem suas próprias chaves, isoladas de outros usuários
- **Não exposição**: As chaves nunca são retornadas pela API - apenas o status de configuração
- **Chave derivada do JWT**: A criptografia usa uma chave derivada do seu JWT secret

## 🚀 Como Usar

### 1. Inicie a Aplicação

Basta rodar o Docker Compose - não precisa mais configurar variáveis de ambiente:

```bash
cd infrastructure
docker-compose -f docker-compose-unified.yml up -d
```

Ou com o menu interativo (Windows):
```bash
docker-compose-menu.bat
```

### 2. Acesse o Frontend

Abra seu navegador em: http://localhost:3000

### 3. Faça Login ou Cadastre-se

Se é a primeira vez:
1. Clique em "Registrar"
2. Crie sua conta
3. Faça login

### 4. Configure suas Chaves

1. Clique em "Configurações" no menu superior
2. Configure suas chaves:

#### OpenAI (ChatGPT) - Para análise de tarefas com IA

- **API Key**: Obtenha em https://platform.openai.com/api-keys
- Cole a chave no campo "API Key"
- A chave começa com `sk-`

#### Twilio WhatsApp - Para notificações

- **Account SID**: Começa com `AC`
- **Auth Token**: Token de autenticação
- **Número WhatsApp Twilio**: Formato `whatsapp:+14155238886`
- **Seu Número WhatsApp**: Formato `whatsapp:+5511999999999` (onde você receberá notificações)

Obtenha suas credenciais em: https://www.twilio.com/console

5. Clique em "Salvar Configurações"

## ✨ Recursos Habilitados

### Com OpenAI Configurado:
- ✅ Análise inteligente de tarefas
- ✅ Sugestão automática de prioridades
- ✅ Geração de subtarefas
- ✅ Estimativa de tempo
- ✅ Sugestão de tags relevantes

### Com Twilio Configurado:
- ✅ Lembretes diários por WhatsApp
- ✅ Alertas de tarefas atrasadas
- ✅ Resumo de conclusões
- ✅ Notificações personalizadas

## 🔄 Modo Fallback

Se você não configurar as chaves:

- **OpenAI**: O sistema funcionará com análises mock (simuladas)
- **Twilio**: As mensagens serão simuladas apenas nos logs

Isso permite que você use a aplicação completa mesmo sem as integrações!

## 🛠️ Configuração Avançada (Opcional)

Se você ainda preferir usar variáveis de ambiente (para ambientes de produção, por exemplo):

1. Crie um arquivo `.env` na raiz do projeto:

```bash
# Opcional - Valores padrão do sistema
OPENAI_API_KEY=sk-sua-chave-openai
TWILIO_ACCOUNT_SID=AC...
TWILIO_AUTH_TOKEN=...
TWILIO_WHATSAPP_NUMBER=whatsapp:+14155238886
```

2. As configurações do usuário (via interface) têm **prioridade** sobre as variáveis de ambiente

## 📊 Ordem de Prioridade

O sistema usa a seguinte ordem para buscar as credenciais:

1. **Configurações do usuário** (via interface web) ← Prioridade máxima
2. **Variáveis de ambiente** (do Docker/sistema)
3. **Modo fallback** (simulação)

## 🔍 Verificando Status

Na página de Configurações, você verá indicadores visuais:

- ✅ **Configurado** (ícone verde) - Chaves estão configuradas
- ❌ **Não configurado** (ícone cinza) - Chaves não foram fornecidas

## 🔐 Boas Práticas de Segurança

1. **Não compartilhe suas chaves de API**
2. **Use chaves diferentes para desenvolvimento e produção**
3. **Revogue chaves antigas** quando não forem mais necessárias
4. **Monitore o uso** das suas APIs (OpenAI e Twilio têm dashboards)
5. **Defina limites de gastos** nas plataformas OpenAI e Twilio

## 🆘 Solução de Problemas

### "Erro ao atualizar configurações"
- Verifique se você está autenticado
- Certifique-se de que o backend está rodando
- Verifique os logs do backend: `docker logs smarttask-backend`

### "OpenAI não está funcionando"
- Verifique se sua chave é válida
- Confirme que você tem créditos na sua conta OpenAI
- Teste a chave em: https://platform.openai.com/playground

### "WhatsApp não está enviando mensagens"
- Verifique suas credenciais Twilio
- Confirme que o número Twilio está ativo
- Certifique-se de que o formato do número está correto
- Verifique os logs: `docker logs smarttask-backend`

## 📚 Mais Informações

- [Documentação Completa](../docs/INDICE_DOCUMENTACAO.md)
- [Guia de Início Rápido](../docs/COMECE_AQUI.md)
- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Twilio WhatsApp API](https://www.twilio.com/docs/whatsapp)

## 🎉 Pronto!

Agora você pode usar todas as funcionalidades do Smart Task AI sem precisar mexer em arquivos de configuração ou variáveis de ambiente! 🚀
