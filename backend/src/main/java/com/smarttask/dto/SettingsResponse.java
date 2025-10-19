package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta com as configurações do usuário.
 * Por segurança, não retorna os valores reais das chaves, apenas indica se estão configuradas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsResponse {
    
    /**
     * Indica se a chave OpenAI está configurada.
     */
    private boolean openaiConfigured;
    
    /**
     * Indica se as credenciais Twilio estão configuradas.
     */
    private boolean twilioConfigured;
    
    /**
     * Número WhatsApp do Twilio (parcialmente mascarado por segurança).
     */
    private String twilioWhatsappNumber;
    
    /**
     * Número WhatsApp do usuário (parcialmente mascarado por segurança).
     */
    private String userWhatsappNumber;
    
    /**
     * Mensagem informativa sobre o estado das configurações.
     */
    private String message;
}
