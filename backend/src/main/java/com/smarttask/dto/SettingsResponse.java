package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta com as configuracoes do usuario.
 * Por seguranca, nao retorna os valores reais; apenas indica se estao ativos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingsResponse {

    /**
     * Indica se a chave OpenAI esta configurada.
     */
    private boolean openaiConfigured;

    /**
     * Indica se as credenciais Twilio estao configuradas.
     */
    private boolean twilioConfigured;

    /**
     * Numero WhatsApp do Twilio (mascarado por seguranca).
     */
    private String twilioWhatsappNumber;

    /**
     * Numero WhatsApp do usuario (mascarado por seguranca).
     */
    private String userWhatsappNumber;

    /**
     * Mensagem informativa sobre o estado das configuracoes.
     */
    private String message;
}
