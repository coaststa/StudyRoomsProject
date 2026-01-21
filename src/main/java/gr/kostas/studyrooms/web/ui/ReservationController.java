package gr.kostas.studyrooms.web.ui;


import gr.kostas.studyrooms.core.model.Room;
import gr.kostas.studyrooms.core.repository.RoomRepository;
import gr.kostas.studyrooms.core.security.CurrentUserProvider;
import gr.kostas.studyrooms.core.service.ReservationService;
import gr.kostas.studyrooms.core.service.model.CancelReservationRequest;
import gr.kostas.studyrooms.core.service.model.MakeReservationRequest;
import gr.kostas.studyrooms.core.service.model.ReservationView;
import gr.kostas.studyrooms.web.ui.model.MakeReservationForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final CurrentUserProvider currentUserProvider;
    private final ReservationService reservationService;
    private final RoomRepository roomRepository;

    public ReservationController(final CurrentUserProvider currentUserProvider,
                            final ReservationService reservationService,
                            final     RoomRepository roomRepository) {
        if (currentUserProvider == null) throw new NullPointerException();
        if (reservationService == null) throw new NullPointerException();
        if (roomRepository == null) throw new NullPointerException();

        this.currentUserProvider = currentUserProvider;
        this.reservationService = reservationService;
        this.roomRepository = roomRepository;
    }

    @GetMapping("")
    public String list(final Model model) {
        final List<ReservationView> reservationViewList = this.reservationService.getReservations();
        model.addAttribute("reservations", reservationViewList);
        return "reservations";
    }

    @GetMapping("/{reserveId}")
    public String detail(@PathVariable final Long reserveId, final Model model) {
        final ReservationView reservationView = this.reservationService.getReservation(reserveId).orElse(null);
        if (reserveId == null || reservationView == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Reservation not found");
        }
        model.addAttribute("reservation", reservationView);
        return "reservation";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/new")
    public String showOpenForm(final Model model) {
        LocalDateTime now = LocalDateTime.now();
        String reservedForValue = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        final MakeReservationForm makeReservationForm = new MakeReservationForm(null, LocalDateTime.now());
        model.addAttribute("form", makeReservationForm);
        model.addAttribute("reservedForValue", reservedForValue);
        model.addAttribute("rooms", roomRepository.findAll());
        return "new_reservation";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/new")
    public String handleMakeForm(
            @ModelAttribute("form") @Valid final MakeReservationForm makeReservationForm,
            final BindingResult bindingResult
        ) {
        if (bindingResult.hasErrors()) {
            return "new_reservation";
        }
        Room room = this.roomRepository.findById(makeReservationForm.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Selected room does not exist"));
        final MakeReservationRequest makeReservationRequest =
                new MakeReservationRequest(
                        this.currentUserProvider.requiredStudentId(),
                        makeReservationForm.reservedFor().atZone(ZoneId.systemDefault()).toInstant(),
                        room

                );
        final ReservationView reservationView = this.reservationService.makeReservation(makeReservationRequest);
        return "redirect:/reservations/" + reservationView.id();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{reservationId}/cancel")
    public String cancel(@PathVariable final Long reservationId) {

        final CancelReservationRequest request =
                new CancelReservationRequest(reservationId);

        final ReservationView reservation =
                this.reservationService.cancelReservation(request);

        return "redirect:/reservations/" + reservation.id();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{reservationId}/checkin")
    public String checkIn(@PathVariable final Long reservationId) {
        reservationService.checkInReservation(reservationId);
        return "redirect:/reservations/" + reservationId;
    }

}
