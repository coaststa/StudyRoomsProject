package gr.kostas.studyrooms.core.port.impl.dto;

public record SendEmailRequest(
        String address,
        String subject,
        String text
) {
}
