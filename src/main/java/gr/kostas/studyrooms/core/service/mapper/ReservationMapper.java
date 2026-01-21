package gr.kostas.studyrooms.core.service.mapper;

import gr.kostas.studyrooms.core.model.Reservation;
import gr.kostas.studyrooms.core.service.model.ReservationView;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert {@link Reservation} to {@link ReservationView}.
 */
@Component
public class ReservationMapper {

    private final PersonMapper personMapper;

    public ReservationMapper(final PersonMapper personMapper) {
        if (personMapper == null) throw new NullPointerException();
        this.personMapper = personMapper;
    }

    public ReservationView convertTicketToTicketView(final Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        return new ReservationView(
                reservation.getId(),
                this.personMapper.convertPersonToPersonView(reservation.getStudent()),
                reservation.getStatus(),
                reservation.getSent(),
                reservation.getReserve()
        );
    }
}
