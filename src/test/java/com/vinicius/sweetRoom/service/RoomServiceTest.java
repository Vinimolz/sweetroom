package com.vinicius.sweetRoom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vinicius.sweetRoom.DTOs.roomDTOs.CreateRoomDTO;
import com.vinicius.sweetRoom.DTOs.roomDTOs.ResponseRoomDTO;
import com.vinicius.sweetRoom.exceptions.DuplicatedResourceException;
import com.vinicius.sweetRoom.exceptions.ResourceNotFoundException;
import com.vinicius.sweetRoom.model.Room;
import com.vinicius.sweetRoom.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @Mock
    private RoomRepository roomRepo;

    @InjectMocks
    private RoomService roomService;

    @Test
    void createRoom_SuccessfulCreation_ReturnsResponseRoomDTO() {
        // ARRANGE
        CreateRoomDTO createRoomDTO = new CreateRoomDTO(
                "Conference Room A",
                10,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        );

        Room mockRoom = new Room(
                "Conference Room A",
                10,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        );
        mockRoom.setId(1L);

        when(roomRepo.existsByName(createRoomDTO.name())).thenReturn(false);
        when(roomRepo.save(any(Room.class))).thenReturn(mockRoom);

        // ACT
        ResponseRoomDTO result = roomService.createRoom(createRoomDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Conference Room A", result.name());
        assertEquals(10, result.capacity());
        assertEquals(LocalTime.of(8, 0), result.operatingHoursStart());
        assertEquals(LocalTime.of(18, 0), result.operatingHoursEnd());
        assertEquals(3, result.availableDays().size());

        verify(roomRepo, times(1)).existsByName("Conference Room A");
        verify(roomRepo, times(1)).save(any(Room.class));
    }

    @Test
    void createRoom_DuplicateName_ThrowsDuplicatedResourceException() {
        // ARRANGE
        CreateRoomDTO createRoomDTO = new CreateRoomDTO(
                "Conference Room A",
                10,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        );

        when(roomRepo.existsByName(createRoomDTO.name())).thenReturn(true);

        // ACT & ASSERT
        DuplicatedResourceException ex = assertThrows(DuplicatedResourceException.class, () -> {
            roomService.createRoom(createRoomDTO);
        });

        assertEquals("This room name already exists: Conference Room A", ex.getMessage());

        verify(roomRepo, never()).save(any(Room.class));
    }

    @Test
    void getRoomById_SuccessfulFetch_ReturnsResponseRoomDTO() {
        // ARRANGE
        Long mockId = 1L;
        Room mockRoom = new Room(
                "Conference Room A",
                10,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        );
        mockRoom.setId(1L);

        when(roomRepo.findById(mockId)).thenReturn(Optional.of(mockRoom));

        // ACT
        ResponseRoomDTO result = roomService.getRoomById(mockId);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Conference Room A", result.name());
        assertEquals(10, result.capacity());

        verify(roomRepo, times(1)).findById(mockId);
    }

    @Test
    void getRoomById_NotFound_ThrowsResourceNotFoundException() {
        // ARRANGE
        Long mockId = 999L;
        when(roomRepo.findById(mockId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            roomService.getRoomById(mockId);
        });

        assertEquals("Could not find room with id: 999", ex.getMessage());

        verify(roomRepo, times(1)).findById(mockId);
    }
}
