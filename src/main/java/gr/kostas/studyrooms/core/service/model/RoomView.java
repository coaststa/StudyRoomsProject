package gr.kostas.studyrooms.core.service.model;

import java.time.LocalTime;

public record RoomView(
        long roomid,
        int capacity,
        LocalTime  openingtime,
        LocalTime closingtime
) {
}
