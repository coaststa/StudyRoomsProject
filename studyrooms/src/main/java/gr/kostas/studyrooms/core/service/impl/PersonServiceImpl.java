package gr.kostas.studyrooms.core.service.impl;



import gr.kostas.studyrooms.core.model.Person;
import gr.kostas.studyrooms.core.model.PersonType;
import gr.kostas.studyrooms.core.port.EmailNotificationPort;
import gr.kostas.studyrooms.core.repository.PersonRepository;
import gr.kostas.studyrooms.core.service.PersonService;
import gr.kostas.studyrooms.core.service.mapper.PersonMapper;
import gr.kostas.studyrooms.core.service.model.CreatePersonRequest;
import gr.kostas.studyrooms.core.service.model.CreatePersonResult;
import gr.kostas.studyrooms.core.service.model.PersonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link PersonService}.
 */
@Service
public class PersonServiceImpl implements PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final EmailNotificationPort emailNotificationPort;

    public PersonServiceImpl(final PersonRepository personRepository,
                             final PersonMapper personMapper, EmailNotificationPort emailNotificationPort) {
        if(emailNotificationPort == null) throw new NullPointerException("emailNotificationPort cannot be null");
        if (personRepository == null) throw new NullPointerException();
        if (personMapper == null) throw new NullPointerException();

        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.emailNotificationPort = emailNotificationPort;
    }

    @Override
    public CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest) {
        if (createPersonRequest == null) throw new NullPointerException();

        // Unpack (we assume valid `CreatePersonRequest` instance)
        // --------------------------------------------------

        final PersonType type = createPersonRequest.type();
        final String huaId = createPersonRequest.libId().strip(); // remove whitespaces
        final String firstName = createPersonRequest.firstName().strip();
        final String lastName = createPersonRequest.lastName().strip();
        final String emailAddress = createPersonRequest.emailAddress().strip();
        String mobilePhoneNumber = createPersonRequest.mobilePhoneNumber().strip();
        final String rawPassword = createPersonRequest.rawPassword();

        // --------------------------------------------------

        if (this.personRepository.existsByLibIdIgnoreCase(huaId)) {
            return CreatePersonResult.fail("HUA ID already registered");
        }

        if (this.personRepository.existsByEmailAddressIgnoreCase(emailAddress)) {
            return CreatePersonResult.fail("Email Address already registered");
        }

        if (this.personRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            return CreatePersonResult.fail("Mobile Phone Number already registered");
        }

        // --------------------------------------------------


        // --------------------------------------------------

        final String hashedPassword = rawPassword; // TODO Implement: encode password.

        // Instantiate person.
        // --------------------------------------------------

        Person person = new Person();
        person.setId(null); // auto generated
        person.setLibId(huaId);
        person.setType(type);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmailAddress(emailAddress);
        person.setMobilePhoneNumber(mobilePhoneNumber);
        person.setPasswordHash(hashedPassword);
        person.setCreatedAt(null); // auto generated.

        // Persist person (save/insert to database)
        // --------------------------------------------------

        person = this.personRepository.save(person);

        // --------------------------------------------------

        final String content = String.format("You have successfully registered for the Study Rooms application. Use your email (%s) to log in.", emailAddress);
        final boolean sent = this.emailNotificationPort.sendMail(emailAddress,
                "Succesfull Registration", content);
        if (!sent) {
            LOGGER.warn("SMS send to {} failed!", mobilePhoneNumber);
        }

        // Map `Person` to `PersonView`.
        // --------------------------------------------------

        final PersonView personView = this.personMapper.convertPersonToPersonView(person);

        // --------------------------------------------------

        return CreatePersonResult.success(personView);
    }
}