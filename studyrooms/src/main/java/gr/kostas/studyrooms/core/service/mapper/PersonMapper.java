package gr.kostas.studyrooms.core.service.mapper;



import gr.kostas.studyrooms.core.model.Person;
import gr.kostas.studyrooms.core.service.model.PersonView;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert {@link Person} to {@link PersonView}.
 */
@Component
public class PersonMapper {

    public PersonView convertPersonToPersonView(final Person person) {
        if (person == null) {
            return null;
        }
        final PersonView personView = new PersonView(
                person.getId(),
                person.getLibId(),
                person.getFirstName(),
                person.getLastName(),
                person.getMobilePhoneNumber(),
                person.getEmailAddress(),
                person.getType()
        );
        return personView;
    }
}