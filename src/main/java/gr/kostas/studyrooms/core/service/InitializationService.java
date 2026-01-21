package gr.kostas.studyrooms.core.service;

import gr.kostas.studyrooms.core.model.PersonType;
import gr.kostas.studyrooms.core.service.model.CreatePersonRequest;
import gr.kostas.studyrooms.core.service.model.MakeRoomRequest;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class InitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    private final PersonService personService;
    private final RoomService roomService;
    private final AtomicBoolean initialized;

    public InitializationService(final PersonService personService,
                                 final RoomService roomService) {
        if (personService == null) throw new NullPointerException();
        if (roomService == null) throw new NullPointerException();
        this.personService = personService;
        this.roomService = roomService;
        this.initialized = new AtomicBoolean(false);
    }

    @PostConstruct
    public void populateDatabaseWithInitialData() {
        final boolean alreadyInitialized = this.initialized.getAndSet(true);
        if (alreadyInitialized) {
            LOGGER.warn("Database initialization skipped: initial data has already been populated.");
            return;
        }
        LOGGER.info("Starting database initialization with initial data...");
        final List<CreatePersonRequest> createPersonRequestList = List.of(
                // User with ID 2
                new CreatePersonRequest(
                        PersonType.STUDENT,
                        "it2023001",
                        "Test 1",
                        "Test 1",
                        "it2023001@hua.gr",
                        "+306900000001",
                        "1234"
                ),
                // User with ID 3
                new CreatePersonRequest(
                        PersonType.STUDENT,
                        "it2023002",
                        "Test 2",
                        "Test 2",
                        "it2023002@hua.gr",
                        "+306900000002",
                        "1234"
                )
        );
        for (final var createPersonRequest : createPersonRequestList) {
            this.personService.createPerson(createPersonRequest);
        }
        final List<MakeRoomRequest> makeRoomRequests = List.of(
            new MakeRoomRequest(
                10,
                LocalTime.of(9,0),
                LocalTime.of(17,0)
            ),
            new MakeRoomRequest(
                    12,
                    LocalTime.of(9,0),
                    LocalTime.of(20,0)
            ),
            new MakeRoomRequest(
                    5,
                    LocalTime.of(12,0),
                    LocalTime.of(18,0)
            )
        );
        for (final var makeRoomRequest : makeRoomRequests) {
            this.roomService.makeRoom(makeRoomRequest);
        }

        LOGGER.info("Database initialization completed successfully.");
    }
}
