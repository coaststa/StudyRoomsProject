package gr.kostas.studyrooms.web.ui;

import gr.kostas.studyrooms.core.repository.RoomRepository;
import gr.kostas.studyrooms.core.security.CurrentUserProvider;
import gr.kostas.studyrooms.core.service.RoomService;
import gr.kostas.studyrooms.core.service.model.CancelReservationRequest;
import gr.kostas.studyrooms.core.service.model.MakeRoomRequest;
import gr.kostas.studyrooms.core.service.model.ReservationView;
import gr.kostas.studyrooms.core.service.model.RoomView;
import gr.kostas.studyrooms.web.ui.model.MakeRoomForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final CurrentUserProvider currentUserProvider;
    private final RoomService roomService;
    private final RoomRepository roomRepository;

    public RoomController(final CurrentUserProvider currentUserProvider,
                                 final RoomService roomService,
                                 final     RoomRepository roomRepository) {
        if (currentUserProvider == null) throw new NullPointerException();
        if (roomService == null) throw new NullPointerException();
        if (roomRepository == null) throw new NullPointerException();

        this.currentUserProvider = currentUserProvider;
        this.roomService = roomService;
        this.roomRepository = roomRepository;
    }

    @GetMapping("")
    public String list(final Model model) {
        final List<RoomView> roomViewList = this.roomService.getRooms();
        model.addAttribute("rooms", roomViewList);
        return "rooms";
    }

    @GetMapping("/{roomId}")
    public String detail(@PathVariable final Long roomId, final Model model) {
        final RoomView roomView = this.roomService.getRoom(roomId).orElse(null);
        if (roomId == null || roomView == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(404), "Room not found");
        }
        model.addAttribute("room", roomView);
        return "room";
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/new")
    public String showMakeForm(final Model model) {
       final MakeRoomForm makeRoomForm = new MakeRoomForm(0, LocalTime.now(),LocalTime.now());
        model.addAttribute("form", makeRoomForm);
        return "new_room";
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/new")
    public String handleMakeForm(
            @ModelAttribute("form") @Valid final MakeRoomForm makeRoomForm,
            final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "new_room";
        }
        final MakeRoomRequest makeRoomRequest =
                new MakeRoomRequest(
                        makeRoomForm.capacity(),
                        makeRoomForm.openingtime(),
                        makeRoomForm.closingtime()

                );
        final RoomView roomView = this.roomService.makeRoom(makeRoomRequest);
        return "redirect:/rooms/" + roomView.roomid();
    }
    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/{roomId}/changecap")
    public String changecap(@PathVariable final Long roomId,@RequestParam("cap") int cap) {

        this.roomService.changeRoomCapacity(this.roomRepository.getRoomByRoomId(roomId),cap);

        return "redirect:/rooms/" + roomId;
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/{roomId}/changeopeningtime")
    public String changeopening(@PathVariable final Long roomId,@RequestParam("op") String op) {

        this.roomService.changeOpeningTime(this.roomRepository.getRoomByRoomId(roomId),LocalTime.parse(op));

        return "redirect:/rooms/" + roomId;
    }

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/{roomId}/changeclosingtime")
    public String changeclosing(@PathVariable final Long roomId,@RequestParam("clos") String clos) {

        this.roomService.changeClosingTime(this.roomRepository.getRoomByRoomId(roomId),LocalTime.parse(clos));

        return "redirect:/rooms/" + roomId;
    }
}
