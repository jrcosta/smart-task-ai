package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisicao de atualizacao de configuracoes do usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsRequest {

    /**
     * Chave de API da OpenAI.
     * Deixe vazio para remover a configuracao.
     */
    private String openaiApiKey;

    /**
     * Account SID do Twilio.
     */
    private String twilioAccountSid;

    /**
     * Auth Token do Twilio.
     */
    private String twilioAuthToken;

    /**
     * Numero WhatsApp do Twilio (formato: whatsapp:+14155238886).
     */
    private String twilioWhatsappNumber;

    /**
     * Numero WhatsApp do usuario para receber notificacoes.
     * Use formato whatsapp:+5511999999999.
     */
    private String userWhatsappNumber;
}
