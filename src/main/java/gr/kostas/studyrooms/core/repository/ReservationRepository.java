package gr.kostas.studyrooms.core.repository;

import gr.kostas.studyrooms.core.model.Reservation;
import gr.kostas.studyrooms.core.model.ReservationStatus;
import jdk.jfr.RecordingState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByStudentId(Long studentId);

    List<Reservation> findAllByRoomId(Long roomId);

    List<Reservation> findAllByStatusAndReserveBefore(ReservationStatus status, Instant time);

    List<Reservation> findByStatusAndReserveBefore(final ReservationStatus status, Instant before);

    boolean existsByStudentIdAndStatusIn(final long studentId, final Collection<ReservationStatus> statuses);

    long countByStudentIdAndStatusIn(final long studentId, final Collection<ReservationStatus> statuses);

}
