package gr.kostas.studyrooms.core.service.model;

import gr.kostas.studyrooms.core.model.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * General view of {@link gr.kostas.studyrooms.core.model.Reservation} entity.
 *
 * @see gr.kostas.studyrooms.core.model.Reservation
 * @see gr.kostas.studyrooms.core.service.ReservationService
 */
public record ReservationView(
        long id,
        PersonView student,
        ReservationStatus status,
        Instant sent,
        Instant reserve

) {}
