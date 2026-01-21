package gr.kostas.studyrooms.web.scheduler;

import gr.kostas.studyrooms.core.model.Reservation;
import gr.kostas.studyrooms.core.model.ReservationStatus;
import gr.kostas.studyrooms.core.repository.ReservationRepository;
import gr.kostas.studyrooms.core.service.ReservationService;
import gr.kostas.studyrooms.core.service.model.CompleteReservationRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    public ReservationScheduler(ReservationRepository reservationRepository,
                                ReservationService reservationService) {
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    @Scheduled(fixedRate = 60_000)
    public void completeExpiredReservations() {

        Instant now = Instant.now();

        List<Reservation> expired = reservationRepository.findAllByStatusAndReserveBefore(ReservationStatus.RESERVED, now);

        for (Reservation reservation : expired) {
            reservationService.completeReservation(new CompleteReservationRequest(reservation.getId(), ReservationStatus.DIDNTSHOW));
        }
    }
}
