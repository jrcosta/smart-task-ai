package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisição de atualização de configurações do usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsRequest {
    
    /**
     * Chave de API da OpenAI.
     * Deixe vazio para remover a configuração.
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
     * Número WhatsApp do Twilio (formato: whatsapp:+14155238886).
     */
    private String twilioWhatsappNumber;
    
    /**
     * Número WhatsApp do usuário para receber notificações (formato: whatsapp:+5511999999999).
     */
    private String userWhatsappNumber;
}
