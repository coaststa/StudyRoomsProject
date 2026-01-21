package gr.kostas.studyrooms.core.service;

import gr.kostas.studyrooms.core.model.Room;
import gr.kostas.studyrooms.core.service.model.CompleteReservationRequest;
import gr.kostas.studyrooms.core.service.model.MakeRoomRequest;
import gr.kostas.studyrooms.core.service.model.RoomView;


import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    Optional<RoomView> getRoom(final Long roomid);

    List<RoomView> getRooms();

    RoomView makeRoom(final MakeRoomRequest makeRoomRequest);


    void changeRoomCapacity(Room room, int capacity);

    void changeOpeningTime(Room room,LocalTime time);

    void changeClosingTime(Room room,LocalTime time);
}
