# 📱 Guia de Configuração do WhatsApp

Este guia explica como configurar as notificações do WhatsApp no Smart Task Manager.

## 🎯 Funcionalidades

- ✅ **Lembretes Diários**: Receba suas tarefas do dia no horário que você escolher (ex: 8:30 da manhã)
- ✅ **Alertas de Atraso**: Notificações sobre tarefas com prazo vencido
- ✅ **Resumo de Conclusões**: Resumo das tarefas que você completou
- ✅ **Mensagens Personalizadas**: Com emojis e formatação para melhor visualização

## 🚀 Configuração Passo a Passo

### 1. Criar Conta no Twilio (Gratuito)

1. Acesse: https://www.twilio.com/try-twilio
2. Clique em "Sign up" e preencha seus dados
3. Verifique seu email e número de telefone
4. Você receberá **$15 em créditos gratuitos** para testar

### 2. Configurar WhatsApp Sandbox

1. No console do Twilio, vá em: **Messaging** → **Try it out** → **Send a WhatsApp message**
2. Você verá um número do Twilio e um código de ativação
3. **No seu WhatsApp**, adicione o número fornecido (geralmente +1 415 523 8886)
4. Envie a mensagem de ativação (algo como: `join <código>`)
5. Você receberá uma confirmação no WhatsApp

### 3. Obter Credenciais do Twilio

No console do Twilio, encontre:

- **Account SID**: Na página inicial do console
- **Auth Token**: Clique em "Show" ao lado do Auth Token
- **WhatsApp Number**: O número que você usou no sandbox (ex: whatsapp:+14155238886)

### 4. Configurar o Backend

1. Navegue até a pasta do backend:
```bash
cd /home/liliane/CascadeProjects/smart-task-manager/backend
```

2. Crie ou edite o arquivo `.env`:
```bash
nano .env
```

3. Adicione suas credenciais:
```env
# Twilio WhatsApp
TWILIO_ACCOUNT_SID=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxx
TWILIO_AUTH_TOKEN=your_auth_token_here
TWILIO_WHATSAPP_NUMBER=whatsapp:+14155238886
```

4. Salve o arquivo (Ctrl+O, Enter, Ctrl+X)

5. Reinicie o backend:
```bash
mvn spring-boot:run
```

### 5. Configurar no Frontend

1. Acesse: http://localhost:3000
2. Faça login na sua conta
3. Vá em **Notificações** no menu
4. Configure:
   - **Número do WhatsApp**: Seu número com código do país (ex: +5511999999999)
   - **Ativar Notificações**: Marque como ativado
   - **Horário do Lembrete**: Escolha o horário (ex: 08:30)
   - **Opções Adicionais**: Marque as que desejar
5. Clique em **"Enviar Teste"** para verificar se está funcionando
6. Clique em **"Salvar Configurações"**

## 📋 Exemplo de Mensagem

Você receberá mensagens como esta:

```
🌅 *Bom dia, João!*

📋 *Suas tarefas para hoje:*

1. 🔴 *Finalizar relatório do projeto*
   📅 Prazo: 05/10 14:00
   ⏱️ Estimativa: 3h

2. 🟡 *Revisar código do backend*
   📅 Prazo: 05/10 18:00
   ⏱️ Estimativa: 2h

3. 🟢 *Responder emails*
   ⏱️ Estimativa: 1h

💪 Você consegue! Boa sorte!
```

## ⚙️ Como Funciona

### Agendamento Automático

O sistema verifica **a cada minuto** se há usuários que devem receber notificações naquele horário.

Por exemplo, se você configurou para receber às **08:30**:
- Às 08:30:00, o sistema busca suas tarefas pendentes
- Formata uma mensagem bonita com emojis
- Envia para seu WhatsApp via Twilio

### Alertas de Tarefas Atrasadas

O sistema verifica **a cada 6 horas** se há tarefas com prazo vencido e envia alertas.

### Prioridades

As tarefas são ordenadas por prioridade:
- 🔴 **URGENT** (Urgente)
- 🟠 **HIGH** (Alta)
- 🟡 **MEDIUM** (Média)
- 🟢 **LOW** (Baixa)

## 🧪 Modo de Teste (Sem Twilio)

Se você não configurar as credenciais do Twilio, o sistema funcionará em **modo de simulação**:

- As mensagens serão exibidas nos logs do backend
- Você pode ver o que seria enviado
- Útil para desenvolvimento e testes

Para ver as mensagens simuladas:
```bash
# Nos logs do backend, você verá:
📱 [SIMULAÇÃO] Mensagem WhatsApp para +5511999999999:
🌅 *Bom dia, João!*
...
```

## 💰 Custos

### Twilio Sandbox (Gratuito)
- **$15 em créditos gratuitos**
- Limitado a números que você adicionar manualmente
- Perfeito para testes e uso pessoal

### Twilio Produção (Pago)
- Necessário para uso comercial
- Requer aprovação do WhatsApp Business
- Custos por mensagem (varia por país)
- Mais informações: https://www.twilio.com/whatsapp/pricing

## 🔧 Troubleshooting

### Não recebo mensagens

1. **Verifique se ativou o sandbox**:
   - Envie a mensagem de ativação novamente
   - Confirme que recebeu a mensagem de confirmação

2. **Verifique as credenciais**:
   - Account SID e Auth Token estão corretos?
   - O número do WhatsApp está no formato correto?

3. **Verifique os logs do backend**:
   ```bash
   # Procure por erros relacionados ao Twilio
   tail -f logs/spring.log
   ```

4. **Teste a configuração**:
   - Use o botão "Enviar Teste" no frontend
   - Verifique se aparece erro

### Mensagens em modo simulação

Se as mensagens aparecem como `[SIMULAÇÃO]` nos logs:
- As credenciais do Twilio não estão configuradas
- Verifique o arquivo `.env` do backend
- Reinicie o backend após configurar

### Horário errado

- Verifique o timezone configurado (padrão: America/Sao_Paulo)
- O servidor usa o horário do sistema
- Ajuste o horário no frontend se necessário

## 📚 Recursos Adicionais

- **Documentação Twilio**: https://www.twilio.com/docs/whatsapp
- **Console Twilio**: https://console.twilio.com
- **Preços**: https://www.twilio.com/whatsapp/pricing
- **Suporte**: https://support.twilio.com

## 🎉 Pronto!

Agora você receberá lembretes diários das suas tarefas no WhatsApp! 🚀

Configure o horário que funciona melhor para você e nunca mais esqueça suas tarefas importantes.
