package gr.kostas.studyrooms.core.security;


import gr.kostas.studyrooms.core.model.PersonType;

/**
 * @see gr.hua.dit.officehours.core.security.CurrentUserProvider
 */
public record CurrentUser(long id, String emailAddress, PersonType type) {}
