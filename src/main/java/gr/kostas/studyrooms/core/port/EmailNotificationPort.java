package gr.kostas.studyrooms.core.port;

/**
 * Port to external service for managing SMS notifications.
 */
public interface EmailNotificationPort {

    boolean sendMail(final String address, final String subject, final String content);
}