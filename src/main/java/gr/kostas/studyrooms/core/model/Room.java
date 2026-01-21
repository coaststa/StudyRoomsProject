package gr.kostas.studyrooms.core.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(
        name = "room",
        indexes = {@Index(name = "idx_room_capacity", columnList = "capacity"),
                @Index(name = "idx_room_roomId", columnList = "roomId"),
                @Index(name = "idx_room_openingtime", columnList = "openingtime"),
                @Index(name = "idx_room_closingtime", columnList = "closingtime")}
)
public final class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="roomId")
    private Long roomId;

    @NotNull
    @Column(name = "capacity")
    private int capacity;

    @NotNull
    @Column(name = "openingtime")
    private LocalTime openingTime;

    @NotNull
    @Column(name = "closingtime")
    private LocalTime closingTime;
    public Room(long roomId, int capacity, LocalTime openingTime, LocalTime closingTime) {
        this.roomId = roomId;
        this.capacity = capacity;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public Room() {
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Room{");
        sb.append("roomId=").append(roomId);
        sb.append(", capacity=").append(capacity);
        sb.append(", openingTime=").append(openingTime);
        sb.append(", closingTime=").append(closingTime);
        sb.append('}');
        return sb.toString();
    }
}

