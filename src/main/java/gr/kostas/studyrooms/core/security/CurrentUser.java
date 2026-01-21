package gr.kostas.studyrooms.core.security;


import gr.kostas.studyrooms.core.model.PersonType;

/**
 * @see gr.kostas.studyrooms.core.security.CurrentUserProvider
 */
public record CurrentUser(long id, String emailAddress, PersonType type) {}
