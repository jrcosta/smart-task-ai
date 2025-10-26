package com.smarttask.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smarttask.observability.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class WhatsAppServiceTest {

    @Mock
    private MetricsService metricsService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private WhatsAppService whatsAppService;

    @BeforeEach
    void configurarCampos() {
        ReflectionTestUtils.setField(whatsAppService, "defaultAccountSid", "");
        ReflectionTestUtils.setField(whatsAppService, "defaultAuthToken", "");
        ReflectionTestUtils.setField(whatsAppService, "defaultTwilioWhatsAppNumber", "");
    }

    @Test
    void sendTestMessage_semCredenciaisDeveSimularEnvioEMetricas() {
        SettingsService.TwilioCredentials credentials =
                new SettingsService.TwilioCredentials(
                        null,
                        null,
                        null,
                        "+5511999999999");
        when(settingsService.getDecryptedTwilioCredentials(10L))
                .thenReturn(credentials);

        whatsAppService.sendTestMessage(10L, "Marina");

        verify(metricsService).recordWhatsAppMessage("test_message");
    }
}
