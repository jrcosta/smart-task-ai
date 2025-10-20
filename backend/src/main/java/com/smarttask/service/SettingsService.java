package com.smarttask.service;

import com.smarttask.dto.SettingsRequest;
import com.smarttask.dto.SettingsResponse;
import com.smarttask.model.User;
import com.smarttask.model.UserSettings;
import com.smarttask.observability.Traced;
import com.smarttask.repository.UserRepository;
import com.smarttask.repository.UserSettingsRepository;
import com.smarttask.security.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gerencia as configurações seguras de credenciais do usuário.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SettingsService {

    /** Mensagem lancada quando o usuario nao e localizado. */
    private static final String USER_NOT_FOUND_MESSAGE =
        "Usuario nao encontrado";

    /** Mensagem exibida quando ainda nao existem configuracoes persistidas. */
    private static final String NO_SETTINGS_MESSAGE =
            "Nenhuma configuracao encontrada. Defina as chaves de API.";

    /** Mensagem de sucesso ao carregar configuracoes do usuario. */
    private static final String SETTINGS_LOADED_MESSAGE =
            "Configuracoes carregadas com sucesso";

    /** Prefixo utilizado pelo Twilio para numeros de WhatsApp. */
    private static final String WHATSAPP_PREFIX = "whatsapp:";

    /** Modelo de mensagem de log apos gravacao das configuracoes. */
    private static final String LOG_SETTINGS_UPDATED =
            "Configuracoes atualizadas com sucesso para o usuario {}";

    /** Quantidade de digitos exibidos apos a mascara. */
    private static final int PHONE_VISIBLE_DIGITS = 4;

    /** Prefixo aplicado aos numeros mascarados. */
    private static final String MASK_PREFIX = "****";

    /** Repositório responsável pelas configurações persistidas. */
    private final UserSettingsRepository userSettingsRepository;

    /** Repositório de usuários aplicado para validações. */
    private final UserRepository userRepository;

    /** Utilitário de criptografia simétrica. */
    private final EncryptionUtil encryptionUtil;

    /**
     * Obtem as configuracoes atuais do usuario, mascarando informacoes
     * sensiveis.
     *
     * @param userId identificador do usuario autenticado
     * @return representacao segura das configuracoes persistidas
     */
    @Traced("SettingsService.getUserSettings")
    @Transactional(readOnly = true)
    public SettingsResponse getUserSettings(final Long userId) {
        ensureUserExists(userId);

        final UserSettings settings = userSettingsRepository
                .findByUserId(userId)
                .orElse(null);
        if (settings == null) {
            return SettingsResponse.builder()
                    .openaiConfigured(false)
                    .twilioConfigured(false)
                    .message(NO_SETTINGS_MESSAGE)
                    .build();
        }

        final boolean openaiConfigured = hasText(settings.getOpenaiApiKey());
        final boolean twilioConfigured = hasText(settings.getTwilioAccountSid())
                && hasText(settings.getTwilioAuthToken());

        return SettingsResponse.builder()
                .openaiConfigured(openaiConfigured)
                .twilioConfigured(twilioConfigured)
                .twilioWhatsappNumber(
                        maskPhoneNumber(settings.getTwilioWhatsappNumber()))
                .userWhatsappNumber(
                        maskPhoneNumber(settings.getUserWhatsappNumber()))
                .message(SETTINGS_LOADED_MESSAGE)
                .build();
    }

    /**
     * Atualiza credenciais criptografadas e números associados ao WhatsApp.
     *
     * @param userId identificador do usuário autenticado
     * @param request dados enviados a partir do cliente
     * @return estado atualizado das configurações
     */
    @Traced("SettingsService.updateUserSettings")
    @Transactional
    public SettingsResponse updateUserSettings(final Long userId,
            final SettingsRequest request) {
    final User user =
        userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException(
                USER_NOT_FOUND_MESSAGE));

        final UserSettings settings = userSettingsRepository
                .findByUserId(userId)
                .orElse(UserSettings.builder().user(user).build());

        updateOpenAiKey(settings, request, userId);
        updateTwilioAccount(settings, request, userId);
        updateTwilioAuthToken(settings, request, userId);
        updatePhoneNumbers(settings, request);

        userSettingsRepository.save(settings);
        log.info(LOG_SETTINGS_UPDATED, userId);
        return getUserSettings(userId);
    }

    /**
     * Recupera a chave OpenAI descriptografada do usuário.
     *
     * @param userId identificador do usuário autenticado
     * @return chave descriptografada ou {@code null} se ausente
     */
    @Traced("SettingsService.getDecryptedOpenAIKey")
    @Transactional(readOnly = true)
    public String getDecryptedOpenAIKey(final Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .map(UserSettings::getOpenaiApiKey)
                .map(encryptionUtil::decrypt)
                .orElse(null);
    }

    /**
     * Recupera as credenciais Twilio descriptografadas do usuário.
     *
     * @param userId identificador do usuário autenticado
     * @return credenciais descriptografadas ou campos nulos
     */
    @Traced("SettingsService.getDecryptedTwilioCredentials")
    @Transactional(readOnly = true)
    public TwilioCredentials getDecryptedTwilioCredentials(final Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .map(settings -> new TwilioCredentials(
                        decryptOrNull(settings.getTwilioAccountSid()),
                        decryptOrNull(settings.getTwilioAuthToken()),
                        settings.getTwilioWhatsappNumber(),
                        settings.getUserWhatsappNumber()))
                .orElse(new TwilioCredentials(null, null, null, null));
    }

    private void ensureUserExists(final Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException(USER_NOT_FOUND_MESSAGE);
        }
    }

    private void updateOpenAiKey(final UserSettings settings,
            final SettingsRequest request,
            final Long userId) {
        final String originalKey = request.getOpenaiApiKey();
        final String trimmedKey = trimToNull(originalKey);

        if (trimmedKey != null) {
        settings.setOpenaiApiKey(encryptionUtil.encrypt(trimmedKey));
        log.info(
            "Chave OpenAI atualizada para o usuario {}",
            userId);
        } else if (originalKey != null) {
            settings.setOpenaiApiKey(null);
        log.info(
            "Chave OpenAI removida para o usuario {}",
            userId);
        }
    }

    private void updateTwilioAccount(final UserSettings settings,
            final SettingsRequest request,
            final Long userId) {
        final String originalSid = request.getTwilioAccountSid();
        final String trimmedSid = trimToNull(originalSid);

        if (trimmedSid != null) {
        settings.setTwilioAccountSid(encryptionUtil.encrypt(trimmedSid));
        log.info(
            "Twilio Account SID atualizado para o usuario {}",
            userId);
        } else if (originalSid != null) {
            settings.setTwilioAccountSid(null);
        log.info(
            "Twilio Account SID removido para o usuario {}",
            userId);
        }
    }

    private void updateTwilioAuthToken(final UserSettings settings,
            final SettingsRequest request,
            final Long userId) {
        final String originalToken = request.getTwilioAuthToken();
        final String trimmedToken = trimToNull(originalToken);

        if (trimmedToken != null) {
        settings.setTwilioAuthToken(encryptionUtil.encrypt(trimmedToken));
        log.info(
                "Twilio Auth Token atualizado para o usuario {}",
                userId);
        } else if (originalToken != null) {
            settings.setTwilioAuthToken(null);
            log.info(
                    "Twilio Auth Token removido para o usuario {}",
                    userId);
        }
    }

    private void updatePhoneNumbers(
            final UserSettings settings,
            final SettingsRequest request) {
        final String twilioNumber = trimToNull(
                request.getTwilioWhatsappNumber());
        if (twilioNumber != null || request.getTwilioWhatsappNumber() != null) {
            settings.setTwilioWhatsappNumber(twilioNumber);
        }

        final String userNumber = trimToNull(
                request.getUserWhatsappNumber());
        if (userNumber != null || request.getUserWhatsappNumber() != null) {
            settings.setUserWhatsappNumber(userNumber);
        }
    }

    private String trimToNull(final String value) {
        if (value == null) {
            return null;
        }
        final String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean hasText(final String value) {
        return trimToNull(value) != null;
    }

    /**
     * Mascara um número de telefone mantendo apenas os últimos dígitos.
     *
     * @param phoneNumber número original informado
     * @return número mascarado ou {@code null} quando vazio
     */
    private String maskPhoneNumber(final String phoneNumber) {
        if (!hasText(phoneNumber)) {
            return null;
        }

        final String sanitized = phoneNumber.replace(WHATSAPP_PREFIX, "");
        if (sanitized.length() <= PHONE_VISIBLE_DIGITS) {
            return MASK_PREFIX;
        }

        final int start = sanitized.length() - PHONE_VISIBLE_DIGITS;
        return MASK_PREFIX + sanitized.substring(start);
    }

    private String decryptOrNull(final String encryptedValue) {
        if (encryptedValue == null) {
            return null;
        }
        return encryptionUtil.decrypt(encryptedValue);
    }

    /**
     * Estrutura imutavel para transportar credenciais Twilio
     * descriptografadas.
     *
     * @param accountSid identificador da conta Twilio
     * @param authToken token de autenticacao Twilio
     * @param whatsappNumber numero de envio configurado
     * @param userWhatsappNumber numero de destino do usuario
     */
    public record TwilioCredentials(String accountSid,
            String authToken,
            String whatsappNumber,
            String userWhatsappNumber) {
    }
}
