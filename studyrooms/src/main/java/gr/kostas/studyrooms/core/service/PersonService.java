package gr.kostas.studyrooms.core.service;


import gr.kostas.studyrooms.core.service.model.CreatePersonRequest;
import gr.kostas.studyrooms.core.service.model.CreatePersonResult;

/**
 * Service for managing {@link gr.hua.dit.officehours.core.model.Person}.
 */

public interface PersonService {

    CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest);
}