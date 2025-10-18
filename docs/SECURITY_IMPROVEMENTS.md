# Atualizações de Segurança e Observabilidade

## Visão Geral
As alterações desta entrega reforçam a autenticação, padronizam as respostas de erro e tornam o fluxo de notificações via WhatsApp mais resiliente. Também foram ajustadas as mensagens exibidas no frontend para orientar melhor o usuário.

## Melhorias Implementadas

### Autenticação e Tokens (Backend)
- **UserPrincipal respeita o estado `active` do usuário.**
  - *Motivação:* evitar que contas desativadas consigam autenticar.
  - *Impacto:* Spring Security bloqueia logins de usuários inativos automaticamente.
- **AuthService deixa de lançar `RuntimeException` genérica no login.**
  - *Motivação:* responder com erros tratáveis e registrar inconsistências.
  - *Impacto:* logs mais claros e respostas padronizadas ao frontend.
- **JwtTokenProvider registra falhas de validação de token.**
  - *Motivação:* facilitar auditoria sem expor dados sensíveis.
  - *Impacto:* observabilidade das tentativas com tokens inválidos.

### Tratamento de Erros (Backend)
- **GlobalExceptionHandler agora loga erros e responde mensagens em português.**
  - *Motivação:* reduzir exposição de stack traces e alinhar UX.
  - *Impacto:* respostas consistentes e seguras, mantendo rastreabilidade nos logs.
- **Inclusão de handler para `IllegalStateException`.**
  - *Motivação:* mapear conflitos de estado (ex.: falta de número WhatsApp) para HTTP 400.
  - *Impacto:* tratamentos específicos para falhas de configuração do usuário.

### Fluxo de Notificações WhatsApp (Backend)
- **Validação de número antes de habilitar preferências.**
  - *Motivação:* evitar agendamentos sem destino válido.
  - *Impacto:* erros amigáveis quando o usuário tenta ativar notificações sem número.
- **Envio abortado caso o número esteja ausente.**
  - *Motivação:* impedir chamadas desnecessárias ao Twilio e ruídos de log.
  - *Impacto:* logs em português indicando o motivo do cancelamento.
- **WhatsAppService verifica número de origem e destino.**
  - *Motivação:* impedir falhas silenciosas e manter fallback de simulação.
  - *Impacto:* mensagens de log claras quando faltar configuração do Twilio.

### UX de Erros (Frontend)
- **Helper `getErrorMessage` centraliza mensagens vindas da API.**
  - *Motivação:* reaproveitar parsing das respostas do backend.
  - *Impacto:* toasts padronizados e traduzidos.
- **Login, cadastro, tarefas e notificações usam mensagens consistentes.**
  - *Motivação:* orientar o usuário com feedbacks acionáveis.
  - *Impacto:* redução de mensagens genéricas e melhor aderência à linguagem da aplicação.

## Recomendações Futuras
- Adicionar métricas/alertas para monitorar taxas de falhas no envio do WhatsApp.
- Implementar testes automatizados cobrindo os novos fluxos de erro e simulação.
