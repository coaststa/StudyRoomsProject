package gr.kostas.studyrooms.core.service.mapper;

import gr.kostas.studyrooms.core.model.Reservation;
import gr.kostas.studyrooms.core.model.Room;
import gr.kostas.studyrooms.core.service.model.ReservationView;
import gr.kostas.studyrooms.core.service.model.RoomView;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomMapper() {
    }

    public RoomView convertRoomToRoomView(final Room room) {
        if (room == null) {
            return null;
        }
        return new RoomView(
                room.getRoomId(),
                room.getCapacity(),
                room.getOpeningTime(),
                room.getClosingTime()
        );
    }
}
