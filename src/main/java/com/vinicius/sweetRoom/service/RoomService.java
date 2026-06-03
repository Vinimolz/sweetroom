package com.vinicius.sweetRoom.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vinicius.sweetRoom.DTOs.roomDTOs.CreateRoomDTO;
import com.vinicius.sweetRoom.DTOs.roomDTOs.ResponseRoomDTO;
import com.vinicius.sweetRoom.exceptions.DuplicatedResourceException;
import com.vinicius.sweetRoom.exceptions.ResourceNotFoundException;
import com.vinicius.sweetRoom.model.Room;
import com.vinicius.sweetRoom.repository.RoomRepository;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<ResponseRoomDTO> getAllRooms() {
        return listToDto(roomRepository.findAll());
    }

    public ResponseRoomDTO getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .map(r -> new ResponseRoomDTO(r.getId(), r.getName(), r.getCapacity(), r.getOperatingHoursStart(),
                        r.getOperatingHoursEnd(), r.getAvailableDays()))
                .orElseThrow(() -> new ResourceNotFoundException("Could not find room with id: " + roomId));
    }

    public ResponseRoomDTO createRoom(CreateRoomDTO roomDTO) {
        if (roomRepository.existsByName(roomDTO.name())) {
            throw new DuplicatedResourceException("This room name already exists: " + roomDTO.name());
        }

        Room room = new Room(roomDTO.name(), roomDTO.capacity(), roomDTO.operatingHoursStart(),
                roomDTO.operatingHoursEnd(), roomDTO.availableDays());
        Room savedRoom = roomRepository.save(room);
        return roomToDto(savedRoom);
    }

    private ResponseRoomDTO roomToDto(Room room) {
        return new ResponseRoomDTO(room.getId(), room.getName(), room.getCapacity(), room.getOperatingHoursStart(),
                room.getOperatingHoursEnd(), room.getAvailableDays());
    }

    private List<ResponseRoomDTO> listToDto(List<Room> rooms) {
        return rooms.stream().map(this::roomToDto).collect(Collectors.toList());
    }
}