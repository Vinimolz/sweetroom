package com.vinicius.sweetRoom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vinicius.sweetRoom.DTOs.reservationDTOs.CreateReservationDTO;
import com.vinicius.sweetRoom.DTOs.reservationDTOs.ResponseReservationDTO;
import com.vinicius.sweetRoom.exceptions.ReservationValidationException;
import com.vinicius.sweetRoom.model.Reservation;
import com.vinicius.sweetRoom.model.Room;
import com.vinicius.sweetRoom.model.User;
import com.vinicius.sweetRoom.model.enums.UserRole;
import com.vinicius.sweetRoom.repository.ReservationRepository;
import com.vinicius.sweetRoom.repository.RoomRepository;
import com.vinicius.sweetRoom.repository.UserRepository;

@SpringBootTest
public class ReservationServiceIntegrationTest {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoomRepository roomRepo;

    private User savedUser;
    private Room savedRoom;

    @BeforeEach
    void setUp() {
        reservationRepo.deleteAll();
        userRepo.deleteAll();
        roomRepo.deleteAll();

        savedUser = userRepo.save(new User("Vinicius", "vinicius@email.com", UserRole.STUDENT));

        savedRoom = roomRepo
                .save(new Room("Test Room 1", 70, LocalTime.of(8, 0), LocalTime.of(18, 0), List.of(DayOfWeek.MONDAY)));
    }

    @Test
    void bookRoom_ValidRequest_Success() {
        CreateReservationDTO dto = new CreateReservationDTO(savedUser.getId(), savedRoom.getId(),
                LocalDate.of(2026, 6, 8), LocalTime.of(9, 0), LocalTime.of(10, 0));

        ResponseReservationDTO response = reservationService.createReservation(dto);

        assertNotNull(response);
        assertEquals(savedUser.getId(), response.userId());
        assertEquals(savedRoom.getId(), response.roomId());
        assertEquals(1, reservationRepo.count());
    }

    @Test
    void bookRoom_InvalidTImeRange_ThrowsException() {
        CreateReservationDTO dto = new CreateReservationDTO(savedUser.getId(), savedRoom.getId(),
                LocalDate.of(2026, 6, 8), LocalTime.of(10, 0), LocalTime.of(9, 0));

        ReservationValidationException ex = assertThrows(ReservationValidationException.class,
                () -> reservationService.createReservation(dto));

        assertTrue(ex.getMessage().contains("start time must be strictly before end time"));
        assertEquals(0, reservationRepo.count(), "No reservation should be saved");
    }

    @Test
    void bookRoom_InvalidOperatingHours_ThrowsException() {
        CreateReservationDTO dto = new CreateReservationDTO(savedUser.getId(), savedRoom.getId(),
                LocalDate.of(2026, 6, 8), LocalTime.of(7, 0), LocalTime.of(9, 0));

        ReservationValidationException ex = assertThrows(ReservationValidationException.class,
                () -> reservationService.createReservation(dto));

        assertTrue(ex.getMessage().contains("Reservation hours must be within room operating hours"));
        assertEquals(0, reservationRepo.count(), "No reservation should be saved");
    }

    @Test
    void bookRoom_InvalidAvailabilityDay_ThrowsException() {

        LocalDate reservationDate = LocalDate.of(2026, 6, 9);

        CreateReservationDTO dto = new CreateReservationDTO(savedUser.getId(), savedRoom.getId(),
                reservationDate, LocalTime.of(8, 0), LocalTime.of(9, 0));

        ReservationValidationException ex = assertThrows(ReservationValidationException.class,
                () -> reservationService.createReservation(dto));

        assertTrue(ex.getMessage().contains("The room is not operational on the selected day of the week ("
                + reservationDate.getDayOfWeek() + ")"));
        assertEquals(0, reservationRepo.count(), "No reservation should be saved");
    }

    @Test
    void bookRoom_doubleBooking_ThrowsException() {
        reservationRepo.save(new Reservation(savedUser, savedRoom, LocalDate.of(2026, 6, 8), LocalTime.of(8, 0),
                LocalTime.of(9, 0)));

        CreateReservationDTO dto = new CreateReservationDTO(savedUser.getId(), savedRoom.getId(),
                LocalDate.of(2026, 6, 8), LocalTime.of(8, 0), LocalTime.of(9, 0));

        ReservationValidationException ex = assertThrows(ReservationValidationException.class,
                () -> reservationService.createReservation(dto));

        assertTrue(ex.getMessage().contains("This room is already reserved during the selected time slot"));
        assertEquals(1, reservationRepo.count(), "Only one reservation");
    }
}
