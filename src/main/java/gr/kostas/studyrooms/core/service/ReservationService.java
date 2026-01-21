package gr.kostas.studyrooms.core.service;
import gr.kostas.studyrooms.core.model.Reservation;
import gr.kostas.studyrooms.core.security.CurrentUser;
import gr.kostas.studyrooms.core.service.model.CompleteReservationRequest;
import gr.kostas.studyrooms.core.service.model.MakeReservationRequest;
import gr.kostas.studyrooms.core.service.model.ReservationView;
import gr.kostas.studyrooms.core.service.model.CancelReservationRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing {@link Reservation}.
 *
 * <p><strong>All methods MUST be {@link CurrentUser}-aware.</strong></p>
 */
public interface ReservationService {
    Optional<ReservationView> getReservation(final Long id);

    List<ReservationView> getReservations();

    ReservationView makeReservation(final MakeReservationRequest makeReservationRequest, final boolean notify);

    default ReservationView makeReservation(final MakeReservationRequest makeReservationRequest) {
        return this.makeReservation(makeReservationRequest, true);
    }

    ReservationView cancelReservation(final CancelReservationRequest cancelReservationRequest);

    ReservationView completeReservation(final CompleteReservationRequest completeReservationRequest);

    ReservationView checkInReservation(long reservationId);
}
