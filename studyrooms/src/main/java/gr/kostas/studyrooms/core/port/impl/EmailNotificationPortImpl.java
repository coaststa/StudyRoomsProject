package gr.kostas.studyrooms.core.port.impl;

import gr.kostas.studyrooms.config.RestApiClientConfig;
import gr.kostas.studyrooms.core.port.impl.dto.SendEmailRequest;
import gr.kostas.studyrooms.core.port.impl.dto.SendEmailResult;
import gr.kostas.studyrooms.core.port.EmailNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailNotificationPortImpl implements EmailNotificationPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationPortImpl.class);

    private static final boolean ACTIVE = true; // @future Get from application properties.

    private final RestTemplate restTemplate;

    public EmailNotificationPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) throw new NullPointerException();
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean sendMail(final String address, final String subtext, final String content) {
        if (address == null) throw new NullPointerException();
        if (address.isBlank()) throw new IllegalArgumentException();
        if (subtext == null) throw new NullPointerException();
        if (subtext.isBlank()) throw new IllegalArgumentException();
        if (content == null) throw new NullPointerException();
        if (content.isBlank()) throw new IllegalArgumentException();

        // --------------------------------------------------

        if (!ACTIVE) {
            LOGGER.warn("Email Notification is not active");
            return true;
        }


        // Headers
        // --------------------------------------------------

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Payload
        // --------------------------------------------------

        final SendEmailRequest body = new SendEmailRequest(address,subtext, content);


        // HTTP Request
        // --------------------------------------------------

        final String baseUrl = RestApiClientConfig.BASE_URL;
        final String url = baseUrl + "/api/v1/email";
        final HttpEntity<SendEmailRequest> entity = new HttpEntity<>(body, httpHeaders);
        final ResponseEntity<SendEmailResult> response = this.restTemplate.postForEntity(url, entity, SendEmailResult.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            final SendEmailResult sendSmsResult = response.getBody();
            if (sendSmsResult == null) throw new NullPointerException();
            return sendSmsResult.sent();
        }

        throw new RuntimeException("External service responded with " + response.getStatusCode());
    }
}
