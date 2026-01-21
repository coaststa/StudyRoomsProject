package gr.kostas.studyrooms.core.service.impl;

import gr.kostas.studyrooms.core.model.Room;
import gr.kostas.studyrooms.core.repository.RoomRepository;
import gr.kostas.studyrooms.core.service.RoomService;
import gr.kostas.studyrooms.core.service.mapper.RoomMapper;
import gr.kostas.studyrooms.core.service.model.MakeRoomRequest;
import gr.kostas.studyrooms.core.service.model.RoomView;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link RoomService}.
 *
 * */
@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomServiceImpl.class);


    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomServiceImpl(RoomRepository roomRepository, RoomMapper roomMapper) {
        if(roomMapper == null) throw new NullPointerException();
        this.roomMapper = roomMapper;
        if(roomRepository == null) throw new NullPointerException();
        this.roomRepository = roomRepository;
    }

    @Override
    public Optional<RoomView> getRoom(final Long roomid) {
        if(roomid == null) throw new NullPointerException();
        if(roomid <= 0) throw new IllegalArgumentException();
        final Room room;
        try{
            room = this.roomRepository.getReferenceById(roomid);
        }catch (EntityNotFoundException e){
            return Optional.empty();
        }
        final RoomView roomView = this.roomMapper.convertRoomToRoomView(room);
        return Optional.of(roomView);
    }

    @Override
    public List<RoomView> getRooms() {
        final List<Room> rooms;
        rooms = this.roomRepository.findAll();
        return rooms.stream().map(this.roomMapper::convertRoomToRoomView).toList();
    }

    @Override
    public RoomView makeRoom(@Valid MakeRoomRequest makeRoomRequest) {
        if(makeRoomRequest == null) throw new NullPointerException();
        final int capacity= makeRoomRequest.capacity();

        Room room= new Room();
        room.setCapacity(capacity);
        room.setOpeningTime(makeRoomRequest.openingtime());
        room.setClosingTime(makeRoomRequest.closingtime());
        room = this.roomRepository.save(room);
        final RoomView roomView = this.roomMapper.convertRoomToRoomView(room);
        return roomView;
    }

    @Override
    public void changeRoomCapacity(Room room, int capacity) {
        room.setCapacity(capacity);
    }

    @Override
    public void changeOpeningTime(Room room, LocalTime time) {
        room.setOpeningTime(time);
    }

    @Override
    public void changeClosingTime(Room room, LocalTime time) {
        room.setClosingTime(time);
    }


}
