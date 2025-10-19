package com.smarttask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidade JPA que armazena configurações personalizadas do usuário,
 * incluindo chaves de API para integração com serviços externos.
 * 
 * <p>As chaves são armazenadas de forma criptografada para garantir segurança.</p>
 */
@Entity
@Table(name = "user_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserSettings {

    /**
     * Identificador único das configurações.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuário proprietário destas configurações.
     * Relacionamento One-to-One com User.
     */
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    /**
     * Chave de API da OpenAI (criptografada).
     * Usada para análises com IA.
     */
    @Column(name = "openai_api_key", length = 500)
    private String openaiApiKey;

    /**
     * Account SID do Twilio (criptografado).
     * Usado para envio de mensagens WhatsApp.
     */
    @Column(name = "twilio_account_sid", length = 500)
    private String twilioAccountSid;

    /**
     * Auth Token do Twilio (criptografado).
     * Usado para autenticação no serviço Twilio.
     */
    @Column(name = "twilio_auth_token", length = 500)
    private String twilioAuthToken;

    /**
     * Número WhatsApp do Twilio.
     * Formato: whatsapp:+14155238886
     */
    @Column(name = "twilio_whatsapp_number", length = 50)
    private String twilioWhatsappNumber;

    /**
     * Número de WhatsApp do usuário para receber notificações.
     * Formato: whatsapp:+5511999999999
     */
    @Column(name = "user_whatsapp_number", length = 50)
    private String userWhatsappNumber;

    /**
     * Data e hora de criação das configurações.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última modificação.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
