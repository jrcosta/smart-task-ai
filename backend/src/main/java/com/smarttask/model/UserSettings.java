package com.smarttask.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entidade JPA que armazena configuracoes personalizadas do usuario.
 * Inclui chaves de API para integracoes com servicos externos.
 *
 * <p>As chaves ficam criptografadas para garantir seguranca.</p>
 */
@Entity
@Table(name = "user_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserSettings {

    /** Comprimento maximo permitido para chaves e tokens. */
    public static final int API_SECRET_MAX_LENGTH = 500;

    /** Comprimento maximo permitido para numeros de WhatsApp. */
    public static final int WHATSAPP_NUMBER_MAX_LENGTH = 50;

    /**
     * Identificador único das configurações.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
    * Usuario proprietario destas configuracoes.
     * Relacionamento One-to-One com User.
     */
    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    /**
    * Chave de API da OpenAI (criptografada).
    * Usada para analises com IA.
     */
    @Column(name = "openai_api_key", length = API_SECRET_MAX_LENGTH)
    private String openaiApiKey;

    /**
     * Account SID do Twilio (criptografado).
     * Usado para envio de mensagens WhatsApp.
     */
    @Column(name = "twilio_account_sid", length = API_SECRET_MAX_LENGTH)
    private String twilioAccountSid;

    /**
     * Auth Token do Twilio (criptografado).
     * Usado para autenticacao no servico Twilio.
     */
    @Column(name = "twilio_auth_token", length = API_SECRET_MAX_LENGTH)
    private String twilioAuthToken;

    /**
     * Numero WhatsApp do Twilio.
     * Formato: whatsapp:+14155238886
     */
    @Column(
        name = "twilio_whatsapp_number",
        length = WHATSAPP_NUMBER_MAX_LENGTH)
    private String twilioWhatsappNumber;

    /**
     * Numero de WhatsApp do usuario para receber notificacoes.
     * Formato: whatsapp:+5511999999999
     */
    @Column(
        name = "user_whatsapp_number",
        length = WHATSAPP_NUMBER_MAX_LENGTH)
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
