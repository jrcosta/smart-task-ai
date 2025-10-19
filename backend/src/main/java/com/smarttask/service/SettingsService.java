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
 * Gerencia as configurações de usuário, incluindo chaves de API criptografadas.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;

    /**
     * Obtém as configurações do usuário.
     * Por segurança, retorna apenas se as chaves estão configuradas, não seus valores.
     */
    @Traced("SettingsService.getUserSettings")
    @Transactional(readOnly = true)
    public SettingsResponse getUserSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElse(null);

        if (settings == null) {
            return SettingsResponse.builder()
                    .openaiConfigured(false)
                    .twilioConfigured(false)
                    .message("Nenhuma configuração encontrada. Configure suas chaves de API.")
                    .build();
        }

        boolean openaiConfigured = settings.getOpenaiApiKey() != null && !settings.getOpenaiApiKey().isEmpty();
        boolean twilioConfigured = settings.getTwilioAccountSid() != null && 
                                   !settings.getTwilioAccountSid().isEmpty() &&
                                   settings.getTwilioAuthToken() != null &&
                                   !settings.getTwilioAuthToken().isEmpty();

        return SettingsResponse.builder()
                .openaiConfigured(openaiConfigured)
                .twilioConfigured(twilioConfigured)
                .twilioWhatsappNumber(maskPhoneNumber(settings.getTwilioWhatsappNumber()))
                .userWhatsappNumber(maskPhoneNumber(settings.getUserWhatsappNumber()))
                .message("Configurações carregadas com sucesso")
                .build();
    }

    /**
     * Atualiza as configurações do usuário com novas chaves de API.
     * As chaves são criptografadas antes de serem armazenadas.
     */
    @Traced("SettingsService.updateUserSettings")
    @Transactional
    public SettingsResponse updateUserSettings(Long userId, SettingsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElse(UserSettings.builder()
                        .user(user)
                        .build());

        // Criptografa e atualiza as chaves se fornecidas
        if (request.getOpenaiApiKey() != null && !request.getOpenaiApiKey().trim().isEmpty()) {
            settings.setOpenaiApiKey(encryptionUtil.encrypt(request.getOpenaiApiKey().trim()));
            log.info("Chave OpenAI atualizada para o usuário {}", userId);
        } else if (request.getOpenaiApiKey() != null && request.getOpenaiApiKey().trim().isEmpty()) {
            // Se enviado vazio, remove a configuração
            settings.setOpenaiApiKey(null);
            log.info("Chave OpenAI removida para o usuário {}", userId);
        }

        if (request.getTwilioAccountSid() != null && !request.getTwilioAccountSid().trim().isEmpty()) {
            settings.setTwilioAccountSid(encryptionUtil.encrypt(request.getTwilioAccountSid().trim()));
            log.info("Twilio Account SID atualizado para o usuário {}", userId);
        } else if (request.getTwilioAccountSid() != null && request.getTwilioAccountSid().trim().isEmpty()) {
            settings.setTwilioAccountSid(null);
            log.info("Twilio Account SID removido para o usuário {}", userId);
        }

        if (request.getTwilioAuthToken() != null && !request.getTwilioAuthToken().trim().isEmpty()) {
            settings.setTwilioAuthToken(encryptionUtil.encrypt(request.getTwilioAuthToken().trim()));
            log.info("Twilio Auth Token atualizado para o usuário {}", userId);
        } else if (request.getTwilioAuthToken() != null && request.getTwilioAuthToken().trim().isEmpty()) {
            settings.setTwilioAuthToken(null);
            log.info("Twilio Auth Token removido para o usuário {}", userId);
        }

        if (request.getTwilioWhatsappNumber() != null) {
            settings.setTwilioWhatsappNumber(request.getTwilioWhatsappNumber().trim());
        }

        if (request.getUserWhatsappNumber() != null) {
            settings.setUserWhatsappNumber(request.getUserWhatsappNumber().trim());
        }

        userSettingsRepository.save(settings);
        log.info("Configurações atualizadas com sucesso para o usuário {}", userId);

        return getUserSettings(userId);
    }

    /**
     * Obtém a chave OpenAI descriptografada do usuário.
     */
    @Traced("SettingsService.getDecryptedOpenAIKey")
    @Transactional(readOnly = true)
    public String getDecryptedOpenAIKey(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .map(UserSettings::getOpenaiApiKey)
                .map(encryptionUtil::decrypt)
                .orElse(null);
    }

    /**
     * Obtém as credenciais Twilio descriptografadas do usuário.
     */
    @Traced("SettingsService.getDecryptedTwilioCredentials")
    @Transactional(readOnly = true)
    public TwilioCredentials getDecryptedTwilioCredentials(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .map(settings -> {
                    String accountSid = settings.getTwilioAccountSid() != null ? 
                                       encryptionUtil.decrypt(settings.getTwilioAccountSid()) : null;
                    String authToken = settings.getTwilioAuthToken() != null ?
                                      encryptionUtil.decrypt(settings.getTwilioAuthToken()) : null;
                    String whatsappNumber = settings.getTwilioWhatsappNumber();
                    String userWhatsappNumber = settings.getUserWhatsappNumber();
                    
                    return new TwilioCredentials(accountSid, authToken, whatsappNumber, userWhatsappNumber);
                })
                .orElse(new TwilioCredentials(null, null, null, null));
    }

    /**
     * Mascara um número de telefone para exibição segura.
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }
        
        // Remove o prefixo whatsapp: se existir
        String number = phoneNumber.replace("whatsapp:", "");
        
        if (number.length() <= 4) {
            return "****";
        }
        
        // Mostra apenas os últimos 4 dígitos
        return "****" + number.substring(number.length() - 4);
    }

    /**
     * Classe interna para retornar credenciais Twilio descriptografadas.
     */
    public static class TwilioCredentials {
        public final String accountSid;
        public final String authToken;
        public final String whatsappNumber;
        public final String userWhatsappNumber;

        public TwilioCredentials(String accountSid, String authToken, String whatsappNumber, String userWhatsappNumber) {
            this.accountSid = accountSid;
            this.authToken = authToken;
            this.whatsappNumber = whatsappNumber;
            this.userWhatsappNumber = userWhatsappNumber;
        }
    }
}
