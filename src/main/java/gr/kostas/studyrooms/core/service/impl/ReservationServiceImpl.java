package gr.kostas.studyrooms.core.service.impl;

import gr.kostas.studyrooms.core.model.Person;
import gr.kostas.studyrooms.core.model.PersonType;
import gr.kostas.studyrooms.core.model.Reservation;
import gr.kostas.studyrooms.core.model.ReservationStatus;
import gr.kostas.studyrooms.core.port.EmailNotificationPort;
import gr.kostas.studyrooms.core.repository.PersonRepository;
import gr.kostas.studyrooms.core.repository.ReservationRepository;
import gr.kostas.studyrooms.core.security.CurrentUser;
import gr.kostas.studyrooms.core.security.CurrentUserProvider;
import gr.kostas.studyrooms.core.service.ReservationService;
import gr.kostas.studyrooms.core.service.mapper.ReservationMapper;
import gr.kostas.studyrooms.core.service.model.CompleteReservationRequest;
import gr.kostas.studyrooms.core.service.model.MakeReservationRequest;
import gr.kostas.studyrooms.core.service.model.ReservationView;
import gr.kostas.studyrooms.core.service.model.CancelReservationRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Default implementation of {@link ReservationService}.
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private static final Set<ReservationStatus> ACTIVE = Set.of(ReservationStatus.RESERVED);
    //private static final Set<ReservationStatus> DIDNTSHOW = Set.of(ReservationStatus.DIDNTSHOW);

    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;
    private final PersonRepository personRepository;
    private final CurrentUserProvider currentUserProvider;
    private final EmailNotificationPort emailNotificationPort;

    public ReservationServiceImpl(final ReservationMapper reservationMapper,
                             final ReservationRepository reservationRepository,
                             final PersonRepository personRepository,
                             final CurrentUserProvider currentUserProvider,
                             final EmailNotificationPort emailNotificationPort) {
        if (reservationMapper == null) throw new NullPointerException();
        if (reservationRepository == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (currentUserProvider == null) throw new NullPointerException();
        if (emailNotificationPort == null) throw new NullPointerException();

        this.reservationMapper = reservationMapper;
        this.reservationRepository = reservationRepository;
        this.personRepository = personRepository;
        this.currentUserProvider = currentUserProvider;
        this.emailNotificationPort = emailNotificationPort;
    }

    private void notifyPerson(final ReservationView reservationView, final PersonType type) {
        final String emailaddress;
        if (type == PersonType.STUDENT) {
            emailaddress = reservationView.student().emailAddress();
        } else {
            throw new RuntimeException("Unreachable");
        }
        final String content = String.format("Reservation %s for %s  new status: %s", reservationView.id(),
                reservationView.reserve(), reservationView.status().name());
        final boolean sent = this.emailNotificationPort.sendMail(emailaddress, "Reservation Status", content);
        if (!sent) {
            LOGGER.warn("Email send to {} failed", emailaddress);
        }
    }

    @Override
    public Optional<ReservationView> getReservation(Long id) {
        if (id == null) throw new NullPointerException();
        if (id <= 0) throw new IllegalArgumentException();

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();

        final Reservation reservation;

        try{
            reservation = this.reservationRepository.getReferenceById(id);
        }catch(EntityNotFoundException e){ return Optional.empty(); }

        final long reservationPersonId;
        if (currentUser.type() == PersonType.STUDENT) {
            reservationPersonId = reservation.getStudent().getId();
        } else {
            throw new SecurityException("unsupported PersonType");
        }
        if (currentUser.id() != reservationPersonId) {
            return Optional.empty(); // this Ticket does not exist for this user.
        }

        final ReservationView reservationView = this.reservationMapper.convertTicketToTicketView(reservation);

        return Optional.of(reservationView);
    }

    @Override
    public List<ReservationView> getReservations() {
        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        final List<Reservation> reservationList;
        if (currentUser.type() == PersonType.STUDENT) {
            reservationList = this.reservationRepository.findAllByStudentId(currentUser.id());
        } else {
            throw new SecurityException("unsupported PersonType");
        }
        return reservationList.stream()
                .map(this.reservationMapper::convertTicketToTicketView)
                .toList();
    }

    @Transactional
    @Override
    public ReservationView makeReservation(MakeReservationRequest makeReservationRequest, boolean notify) {
        if (makeReservationRequest == null) throw new NullPointerException();

        final long studentId = makeReservationRequest.studentId();
        final Instant reservedfor = makeReservationRequest.reservedfor();

        final Person student = this.personRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("student not found"));

        if (student.getType() != PersonType.STUDENT) {
            throw new IllegalArgumentException("studentId must refer to a STUDENT");
        }

        final CurrentUser currentUser = this.currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.STUDENT) {
            throw new SecurityException("Student type/role required");
        }
        if (currentUser.id() != studentId) {
            throw new SecurityException("Authenticated student does not match the ticket's studentId");
        }

        if(this.reservationRepository.countByStudentIdAndStatusIn(studentId,ACTIVE)>3){
            throw new RuntimeException("Student has reached the limit of 3 active reservations");
        }


        Duration tolerance = Duration.ofDays(5);
        for(Reservation res : this.reservationRepository.findAllByStudentId(studentId)){
            if(Duration.between(res.getReserve(), Instant.now()).abs().compareTo(tolerance) <= 0
                    && res.getStatus() == ReservationStatus.DIDNTSHOW){
                throw new RuntimeException("Student is banned on that day");
            }
        }

        ZoneId zone = ZoneId.systemDefault();

        LocalDate reserveDate = makeReservationRequest.reservedfor().atZone(zone).toLocalDate();

        Instant closingtime =
                LocalDateTime.of(reserveDate, makeReservationRequest.room().getClosingTime())
                        .atZone(zone)
                        .toInstant();
        Instant openingtime =
                LocalDateTime.of(reserveDate, makeReservationRequest.room().getOpeningTime())
                        .atZone(zone)
                        .toInstant();

        if(makeReservationRequest.reservedfor().isBefore(openingtime) || makeReservationRequest.reservedfor().isAfter(closingtime)){
            throw new RuntimeException("Can't make reservation on closed hours");
        }

        Reservation reservation = new Reservation();
        reservation.setStudent(student);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setReserve(makeReservationRequest.reservedfor());
        reservation.setSent(Instant.now());
        reservation=this.reservationRepository.save(reservation);

        final ReservationView reservationView = this.reservationMapper.convertTicketToTicketView(reservation);

        if(notify){
            this.notifyPerson(reservationView,PersonType.STUDENT);
        }
        return reservationView;
    }

    @Override
    public ReservationView makeReservation(MakeReservationRequest makeReservationRequest) {
        return ReservationService.super.makeReservation(makeReservationRequest);
    }

    @Override
    public ReservationView cancelReservation(CancelReservationRequest cancelReservationRequest) {
        if(cancelReservationRequest == null) throw new NullPointerException();
        final long reserveId = cancelReservationRequest.id();

        final Reservation reservation = this.reservationRepository.findById(reserveId)
                .orElseThrow(() -> new IllegalArgumentException("reservation not found"));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new IllegalArgumentException("Only RESERVED reservations can be cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        final Reservation savedReservation = this.reservationRepository.save(reservation);
        final ReservationView reservationView = this.reservationMapper.convertTicketToTicketView(reservation);

        this.notifyPerson(reservationView,PersonType.STUDENT);
        return reservationView;
    }

    @Override
    public ReservationView completeReservation(CompleteReservationRequest completeReservationRequest) {
        if(completeReservationRequest == null) throw new NullPointerException();
        final long reserveId = completeReservationRequest.id();

        final Reservation reservation = this.reservationRepository.findById(reserveId)
                .orElseThrow(() -> new IllegalArgumentException("reservation not found"));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new IllegalArgumentException("Only RESERVED reservations can be completed");
        }

        reservation.setStatus(completeReservationRequest.reservationStatus());

        final Reservation savedReservation = this.reservationRepository.save(reservation);
        final ReservationView reservationView = this.reservationMapper.convertTicketToTicketView(reservation);

        this.notifyPerson(reservationView,PersonType.STUDENT);
        return reservationView;
    }

    @Override
    @Transactional
    public ReservationView checkInReservation(long reservationId) {
        if (reservationId <= 0) throw new IllegalArgumentException("Invalid reservation id");

        final CurrentUser currentUser = currentUserProvider.requireCurrentUser();
        if (currentUser.type() != PersonType.STUDENT) {
            throw new SecurityException("Only students can check in");
        }

        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new IllegalArgumentException("Only RESERVED reservations can be checked in");
        }

        if (reservation.getStudent().getId() != currentUser.id()) {
            throw new SecurityException("This reservation does not belong to the current student");
        }

        Instant now = Instant.now();

        Duration diff = Duration.between(reservation.getReserve(), now).abs();
        if (diff.toMinutes() > 1) {
            throw new RuntimeException("Can't check in at the moment");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        Reservation saved = reservationRepository.save(reservation);

        ReservationView view = reservationMapper.convertTicketToTicketView(saved);
        notifyPerson(view, PersonType.STUDENT);

        return view;
    }
}
