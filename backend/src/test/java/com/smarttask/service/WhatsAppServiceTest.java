package com.smarttask.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class WhatsAppServiceTest {

    @Test
    void sendMessage_quandoSemCredenciais_naoDeveLancarExcecaoERetornarSucessoLogico() {
        WhatsAppService svc = new WhatsAppService(); // Requer construtor padrão; ajuste se necessário
        String to = "whatsapp:+5500000000000";
        String msg = "Teste de notificação";

        assertThatCode(() -> {
            boolean ok = svc.sendMessage(to, msg);
            // Alguns serviços podem retornar ID/String; ajuste o assert conforme seu contrato
            assertThat(ok).isTrue();
        }).doesNotThrowAnyException();
    }
}
