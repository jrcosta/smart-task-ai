# 🔒 Política de Segurança

## Reportando Vulnerabilidades de Segurança

Se você descobrir uma vulnerabilidade de segurança, **NÃO** abra uma issue pública. Em vez disso, por favor envie um relatório privado para:

📧 **jr.icm.sc@gmail.com**

**Por favor inclua:**
- Descrição da vulnerabilidade
- Passos para reproduzir
- Versão afetada
- Possível impacto
- Sugestão de correção (se tiver)

## Processo de Divulgação

1. **Relatório Privado**: Envie detalhes da vulnerabilidade para o email acima
2. **Confirmação**: Você receberá uma confirmação em até 48 horas
3. **Investigação**: Nossa equipe investigará e criará uma correção
4. **Patch**: Uma versão corrigida será lançada
5. **Divulgação Pública**: Após o patch ser lançado, publicaremos um aviso de segurança

## Versões Suportadas

| Versão | Status | Data de Fim de Vida |
|--------|--------|---------------------|
| 1.0.x | ✅ Atual | - |
| 0.9.x | ⚠️ Suporte Limitado | 31/12/2025 |
| < 0.9 | ❌ Sem Suporte | - |

## Segurança por Design

Este projeto implementa as seguintes práticas de segurança:

- ✅ **Autenticação JWT**: Tokens seguros com Spring Security
- ✅ **Validação de Entrada**: Todas as requisições são validadas
- ✅ **Senhas Criptografadas**: BCrypt para hash de senhas
- ✅ **HTTPS Only**: Recomendado em produção
- ✅ **Dependências Auditadas**: Verificações regulares com `npm audit` e Maven dependency check
- ✅ **Observabilidade**: Rastreamento de eventos de segurança com OpenTelemetry
- ✅ **Rate Limiting**: Proteção contra força bruta (a implementar)

## Dependências Vulneráveis

Se você encontrar uma dependência vulnerável:

1. Verifique a versão instalada com `mvn dependency:tree` ou `npm list`
2. Reporte via email de segurança
3. Se possível, sugira uma versão segura

## Conformidade

Este projeto segue as melhores práticas de segurança recomendadas pelo:
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [CWE Top 25](https://cwe.mitre.org/top25/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)

---

**Obrigado por ajudar a manter o Smart Task AI seguro!** 🛡️
