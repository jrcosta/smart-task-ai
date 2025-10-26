# 📖 Usage Guide - Smart Task AI

Guia completo de uso de todas as funcionalidades do Smart Task AI.

## 📋 Índice

1. [Criando sua Primeira Tarefa](#criando-sua-primeira-tarefa)
2. [Gerenciando Tarefas](#gerenciando-tarefas)
3. [Usando IA para Análise](#usando-ia-para-análise)
4. [Configurando Notificações WhatsApp](#configurando-notificações-whatsapp)
5. [Trabalhando com Tags](#trabalhando-com-tags)
6. [Filtros e Busca](#filtros-e-busca)
7. [Dashboard e Estatísticas](#dashboard-e-estatísticas)
8. [Gerenciamento de Perfil](#gerenciamento-de-perfil)

---

## 🎯 Criando sua Primeira Tarefa

### Passo a Passo

1. **Acesse o Dashboard**
   - Faça login em http://localhost:3000
   - Você verá a tela principal com suas tarefas

2. **Clique em "Nova Tarefa"**
   - Botão azul no canto superior direito
   - Ou use o atalho: `Ctrl/Cmd + N` (futuro)

3. **Preencha o Formulário**

   **Título** (obrigatório)
   ```
   Exemplo: "Implementar autenticação JWT"
   Dica: Seja específico e objetivo
   ```

   **Descrição** (opcional, mas recomendado)
   ```
   Exemplo:
   "Criar sistema de autenticação usando JWT tokens.
   - Incluir endpoint de login
   - Implementar middleware de validação
   - Adicionar refresh token
   - Escrever testes unitários"
   
   Dica: Quanto mais detalhes, melhor a análise da IA
   ```

   **Status**
   - `PENDENTE` - Ainda não iniciada
   - `EM_PROGRESSO` - Trabalhando nisso
   - `CONCLUIDA` - Finalizada

   **Prioridade**
   - `BAIXA` - Pode esperar
   - `MEDIA` - Importante, mas não urgente
   - `ALTA` - Crítico, fazer logo

   **Prazo** (opcional)
   ```
   Clique no calendário e selecione uma data
   Dica: Use prazos realistas
   ```

   **Tags** (opcional)
   ```
   Digite e pressione Enter para adicionar
   Exemplos: backend, frontend, urgent, bug
   ```

4. **Use a IA (Opcional)**
   - Clique em "Analisar com IA"
   - Aguarde 2-5 segundos
   - A IA preencherá:
     - Prioridade sugerida
     - Tags relevantes
     - Subtarefas
     - Estimativa de horas

5. **Salve a Tarefa**
   - Clique em "Criar Tarefa"
   - Você verá uma notificação de sucesso
   - A tarefa aparecerá na lista

### Exemplo Completo

```
Título: Refatorar componente de login
Descrição: O componente de login está muito grande e difícil de manter. 
           Precisa ser dividido em componentes menores e mais testáveis.
Status: PENDENTE
Prioridade: ALTA (ou deixe a IA sugerir)
Prazo: 2025-11-30
Tags: frontend, refactor, react

→ Clique em "Analisar com IA"

IA Sugerirá:
- Prioridade: MEDIA
- Tags: frontend, refactor, react, components, testing
- Estimativa: 4 horas
- Subtarefas:
  1. Separar lógica de autenticação
  2. Criar componente de formulário
  3. Criar componente de validação
  4. Escrever testes unitários
  5. Atualizar documentação
```

---

## 🔧 Gerenciando Tarefas

### Visualizar Tarefas

#### Lista de Tarefas
```
Dashboard → Ver todas as tarefas em formato de lista
Cards mostram:
- Título
- Prioridade (cor coded)
- Status
- Prazo
- Tags
```

#### Detalhes da Tarefa
```
Clique em qualquer tarefa para ver:
- Descrição completa
- Subtarefas
- Estimativa de tempo
- Data de criação
- Última atualização
- Análise da IA (se foi usada)
```

### Editar Tarefa

1. **Clique na tarefa** para abrir detalhes
2. **Clique em "Editar"** (ícone de lápis)
3. **Modifique o que precisar**
4. **Salve as alterações**

```
Você pode editar:
✓ Título
✓ Descrição
✓ Status
✓ Prioridade
✓ Prazo
✓ Tags
✓ Subtarefas
```

### Atualizar Status

**Forma Rápida:**
```
No card da tarefa, clique no dropdown de status
Selecione: PENDENTE | EM_PROGRESSO | CONCLUIDA
```

**Forma Detalhada:**
```
Abra a tarefa → Editar → Altere status → Salvar
```

### Marcar como Concluída

**Opção 1: Checkbox**
```
Clique no checkbox ao lado do título
✓ Marca como CONCLUIDA
```

**Opção 2: Dropdown de Status**
```
Status → CONCLUIDA
```

**O que acontece:**
- ✓ Tarefa fica com ícone de check
- ✓ Aparece riscada (opcional)
- ✓ Move para seção "Concluídas"
- ✓ Atualiza estatísticas do dashboard

### Excluir Tarefa

```
Abra a tarefa → Clique em "Excluir" (ícone lixeira)
Confirme a exclusão

⚠️ ATENÇÃO: Esta ação não pode ser desfeita!
```

### Trabalhar com Subtarefas

#### Adicionar Subtarefa
```
1. Abra a tarefa pai
2. Role até seção "Subtarefas"
3. Clique em "+ Adicionar Subtarefa"
4. Preencha os campos
5. Salve
```

#### Marcar Subtarefa como Concluída
```
Clique no checkbox ao lado da subtarefa
Progresso da tarefa pai é atualizado automaticamente
```

#### Ver Progresso
```
Barra de progresso mostra:
"3 de 5 subtarefas concluídas (60%)"
```

---

## 🤖 Usando IA para Análise

### Quando Usar a IA

✅ **Use quando:**
- Criar uma tarefa complexa
- Não sabe como estimar prioridade
- Quer sugestões de como dividir a tarefa
- Precisa de estimativa de tempo
- Quer tags relevantes automaticamente

❌ **Não precisa usar quando:**
- Tarefa muito simples ("Comprar café")
- Já sabe exatamente o que fazer
- Já tem prioridade e tags definidas

### Como Funciona

```
1. Você preenche:
   - Título: "Criar API REST para autenticação"
   - Descrição: "Endpoint de login com JWT, refresh token..."

2. Clica em "Analisar com IA"

3. IA processa (2-5 segundos)

4. IA retorna:
   PRIORIDADE: ALTA
   Segurança é crítica para qualquer aplicação
   
   TAGS: backend, api, security, jwt, authentication
   
   SUBTAREFAS:
   1. Criar endpoint POST /auth/login
   2. Implementar geração de JWT tokens
   3. Criar endpoint POST /auth/refresh
   4. Adicionar middleware de validação
   5. Implementar logout e revogação
   6. Escrever testes de integração
   
   ANÁLISE:
   Esta tarefa envolve componentes críticos de segurança...
   
   HORAS ESTIMADAS: 10

5. Você revisa e aceita/modifica
```

### Analisar Tarefa Existente

```
1. Abra a tarefa
2. Clique em "Editar"
3. Clique em "Analisar com IA"
4. IA atualiza campos
5. Revise e salve
```

### Interpretar Resultados da IA

**Prioridade:**
```
BAIXA  → Pode ser feito depois
MEDIA  → Importante, planeje
ALTA   → Urgente ou crítico
```

**Tags:**
```
A IA sugere tags baseadas em:
- Palavras-chave no título/descrição
- Contexto técnico
- Categorias comuns (bug, feature, etc.)

Você pode:
✓ Aceitar todas
✓ Remover algumas
✓ Adicionar mais
```

**Subtarefas:**
```
IA divide a tarefa em passos lógicos
Você pode:
✓ Reorganizar ordem
✓ Adicionar mais
✓ Remover se não fizer sentido
✓ Criar como tarefas separadas
```

**Estimativa de Horas:**
```
Use como referência, não verdade absoluta
Baseada em complexidade descrita
Ajuste conforme sua experiência
```

### Fallback Sem OpenAI

Se você não configurou a API da OpenAI:

```
IA usará análise mock:
- Prioridade baseada em palavras-chave
- Tags extraídas do texto
- Subtarefas genéricas
- Estimativa padrão

Funciona, mas menos preciso
Configure OpenAI para melhor experiência
```

---

## 📱 Configurando Notificações WhatsApp

### Setup Inicial

1. **Obter Credenciais Twilio**
   ```
   1. Crie conta em https://www.twilio.com/try-twilio
   2. Vá para WhatsApp → Sandbox
   3. Copie:
      - Account SID
      - Auth Token
      - WhatsApp Number (ex: whatsapp:+14155238886)
   ```

2. **Configurar no App**
   ```
   1. Faça login
   2. Vá em: Configurações → API Keys
   3. Seção "Twilio WhatsApp"
   4. Cole suas credenciais
   5. Clique em "Salvar"
   6. Teste enviando uma mensagem
   ```

3. **Configurar seu Número**
   ```
   1. Vá em: Configurações → Notificações
   2. Digite seu número com código do país
      Formato: +5511999999999
   3. Escolha tipos de notificação
   4. Configure horários
   5. Salve
   ```

### Tipos de Notificação

#### Lembrete Diário
```
Configuração:
- Horário: 09:00 (personalizado)
- Frequência: Diária
- Conteúdo:
  - Total de tarefas pendentes
  - Tarefas com prazo hoje
  - Tarefas atrasadas

Exemplo:
"📋 Bom dia! Você tem:
- 5 tarefas pendentes
- 2 tarefas para hoje
- 1 tarefa atrasada
Bom trabalho! 💪"
```

#### Alerta de Prazo
```
Configuração:
- Quando: 1 dia antes do prazo
- Para: Tarefas de prioridade ALTA e MEDIA

Exemplo:
"⏰ Atenção!
A tarefa 'Entregar relatório' vence amanhã.
Prioridade: ALTA
Não esqueça!"
```

#### Resumo de Conclusões
```
Configuração:
- Horário: 18:00 (fim do dia)
- Frequência: Diária
- Conteúdo:
  - Tarefas concluídas hoje
  - Progresso vs meta
  - Parabéns/motivação

Exemplo:
"🎉 Parabéns!
Hoje você concluiu 7 tarefas!
Você está 40% acima da sua meta.
Continue assim! ⭐"
```

#### Tarefas Atrasadas
```
Configuração:
- Quando: Tarefa passa do prazo
- Frequência: Diária até resolver

Exemplo:
"⚠️ Tarefa Atrasada
'Revisar PR #123' está 2 dias atrasada.
Prioridade: ALTA
Precisa de atenção!"
```

### Gerenciar Notificações

```
Configurações → Notificações

Ligar/Desligar por tipo:
□ Lembretes Diários
☑ Alertas de Prazo
☑ Resumo de Conclusões
□ Tarefas Atrasadas

Horários Personalizados:
Lembrete Diário:    [09:00] ▼
Resumo Diário:      [18:00] ▼

Não Perturbe:
De [22:00] ▼ até [07:00] ▼
```

### Testar Notificações

```
Configurações → Notificações
↓
Clique em "Enviar Teste"
↓
Mensagem de teste será enviada
↓
Verifique seu WhatsApp
```

---

## 🏷️ Trabalhando com Tags

### Criar Tags

**Ao Criar Tarefa:**
```
Campo Tags → Digite nome → Pressione Enter
Exemplos:
- backend
- frontend
- urgent
- bug
- feature
```

**Tags Automáticas (IA):**
```
A IA sugere tags relevantes baseadas em:
- Palavras-chave no título
- Contexto da descrição
- Categorias técnicas
```

### Usar Tags Pré-definidas

```
Tags Comuns:
📱 frontend
⚙️  backend
🗄️  database
🔌 api
🐛 bug
✨ feature
🔧 refactor
📝 docs
🧪 testing
🚨 urgent
🔒 security
```

### Filtrar por Tags

```
Dashboard → Filtros → Tags
☑ frontend
☑ urgent
□ backend

Resultado: Todas tarefas frontend E urgent
```

### Organizar com Tags

**Por Projeto:**
```
Tags: projeto-alpha, projeto-beta
Filtre para ver tarefas de cada projeto
```

**Por Tipo:**
```
Tags: bug, feature, enhancement
Separe trabalho de manutenção vs novo desenvolvimento
```

**Por Área:**
```
Tags: frontend, backend, devops, design
Visualize tarefas por especialidade
```

**Por Cliente/Equipe:**
```
Tags: cliente-acme, cliente-globex
Use para freelancers ou consultores
```

### Cores de Tags

```
Tags recebem cores automáticas:
- Hash do nome determina cor
- Contraste automático para legibilidade
- Cores consistentes entre sessões
```

---

## 🔍 Filtros e Busca

### Busca Rápida

```
Barra de busca no topo
Digite: "autenticação"
↓
Busca em:
- Títulos de tarefas
- Descrições
- Tags
- Subtarefas

Resultados em tempo real
```

### Filtros Combinados

```
Painel de Filtros →

Status:
☑ PENDENTE
☑ EM_PROGRESSO
□ CONCLUIDA

Prioridade:
□ BAIXA
☑ MEDIA
☑ ALTA

Tags:
☑ backend
☑ urgent

Data:
De: [2025-10-01]
Até: [2025-10-31]

→ Aplica todos filtros ao mesmo tempo
```

### Salvar Filtros (Futuro)

```
Criar Filtro Favorito:
Nome: "Backend Urgente"
Filtros: prioridade=ALTA, tag=backend
Salvar

Usar Filtro Salvo:
Dropdown → "Backend Urgente"
Filtros aplicados instantaneamente
```

### Ordenação

```
Ordenar por: [Mais Recentes] ▼

Opções:
- Mais Recentes (data de criação DESC)
- Mais Antigas (data de criação ASC)
- Prazo Próximo (prazo ASC)
- Prazo Distante (prazo DESC)
- Prioridade Alta Primeiro
- Prioridade Baixa Primeiro
- Alfabética (A-Z)
- Alfabética (Z-A)
```

---

## 📊 Dashboard e Estatísticas

### Visão Geral

```
Dashboard Principal:

┌─────────────┬─────────────┬─────────────┐
│ Total       │ Concluídas  │ Pendentes   │
│    42       │     25      │     17      │
└─────────────┴─────────────┴─────────────┘

┌─────────────────────────────────────────┐
│  Atrasadas: 3                           │
│  ⚠️ Vencendo Hoje: 2                    │
│  📅 Vencendo Esta Semana: 5             │
└─────────────────────────────────────────┘
```

### Gráficos

#### Produtividade ao Longo do Tempo
```
Gráfico de linha mostrando:
- Eixo X: Últimos 30 dias
- Eixo Y: Tarefas concluídas
- Identifique padrões e picos
```

#### Distribuição por Status
```
Gráfico de pizza:
- PENDENTE: 40%
- EM_PROGRESSO: 35%
- CONCLUIDA: 25%
```

#### Tarefas por Prioridade
```
Gráfico de barras:
ALTA   ████████████ 12
MEDIA  ████████ 8
BAIXA  ████ 4
```

#### Tags Mais Usadas
```
Nuvem de tags:
backend ████
frontend ███
bug ██
feature ██
urgent █
```

### Métricas de Produtividade

```
Esta Semana:
✓ Concluídas: 12 tarefas
⏱️ Tempo Médio: 4.5 horas/tarefa
📈 +20% vs semana passada

Este Mês:
✓ Concluídas: 45 tarefas
⏱️ Tempo Total: 180 horas
🎯 Taxa de Conclusão: 85%

Streak:
🔥 5 dias consecutivos produtivos
```

### Alertas e Insights

```
💡 Insights:
- Você é mais produtivo às terças
- Tarefas backend levam 2x mais tempo que estimado
- 30% das tarefas não têm prazo definido
- Considere dividir tarefas >8 horas
```

---

## 👤 Gerenciamento de Perfil

### Editar Perfil

```
Configurações → Perfil

Nome: [João Silva]
Email: [joao@email.com] (não editável)
Telefone: [+5511999999999]
Timezone: [America/Sao_Paulo] ▼

Salvar Alterações
```

### Alterar Senha

```
Configurações → Segurança

Senha Atual: [••••••••]
Nova Senha: [••••••••]
Confirmar Nova: [••••••••]

Requisitos:
- Mínimo 8 caracteres
- 1 letra maiúscula
- 1 número
- 1 caractere especial

Alterar Senha
```

### Configurar API Keys

```
Configurações → API Keys

OpenAI:
API Key: [sk-••••••••••••••••••••]
[Testar Conexão]

Twilio:
Account SID: [AC••••••••••••••]
Auth Token: [••••••••••••••]
WhatsApp Number: [whatsapp:+14155238886]
[Testar Envio]

Salvar
```

### Preferências

```
Configurações → Preferências

Interface:
□ Modo Escuro (futuro)
☑ Animações
☑ Som de Notificações

Notificações:
☑ Email
☑ WhatsApp
□ Push (navegador)

Produtividade:
Meta Diária: [5] tarefas
Meta Semanal: [25] tarefas

Salvar
```

### Exportar Dados

```
Configurações → Dados

Exportar Tarefas:
Formato: [JSON] ▼
Período: [Últimos 30 dias] ▼
[Baixar]

Formatos disponíveis:
- JSON (completo)
- CSV (planilha)
- PDF (relatório) (futuro)
```

### Deletar Conta

```
Configurações → Conta

⚠️ Zona de Perigo

Deletar Conta:
[Excluir Minha Conta]

Ao deletar:
- Todas tarefas serão removidas
- Dados não podem ser recuperados
- Ação permanente

Confirmação necessária
```

---

## 💡 Dicas e Boas Práticas

### Criação de Tarefas
1. ✅ Use títulos descritivos e específicos
2. ✅ Adicione descrição detalhada
3. ✅ Use tags para organização
4. ✅ Defina prazos realistas
5. ✅ Use IA para tarefas complexas

### Organização
1. ✅ Revise tarefas pendentes diariamente
2. ✅ Atualize status conforme progresso
3. ✅ Use filtros para foco
4. ✅ Mantenha tags consistentes
5. ✅ Arquive tarefas concluídas regularmente

### Produtividade
1. ✅ Comece com tarefas de ALTA prioridade
2. ✅ Divida tarefas grandes em subtarefas
3. ✅ Use estimativas para planejamento
4. ✅ Configure notificações úteis
5. ✅ Revise métricas semanalmente

---

## 🆘 Precisa de Ajuda?

- 📖 [FAQ](FAQ.md) - Perguntas frequentes
- 🐛 [Troubleshooting](Troubleshooting.md) - Problemas comuns
- 💻 [API Documentation](API-Documentation.md) - Referência técnica
- 🤝 [Contributing](Contributing.md) - Como contribuir

---

*Última atualização: Outubro 2025 | Versão: 1.0.0*
