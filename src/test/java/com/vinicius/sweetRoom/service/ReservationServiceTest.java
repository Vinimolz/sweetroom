package com.vinicius.sweetRoom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vinicius.sweetRoom.DTOs.reservationDTOs.CreateReservationDTO;
import com.vinicius.sweetRoom.DTOs.reservationDTOs.ResponseReservationDTO;
import com.vinicius.sweetRoom.Validator.ReservationValidationContext;
import com.vinicius.sweetRoom.Validator.ReservationValidator;
import com.vinicius.sweetRoom.exceptions.ReservationValidationException;
import com.vinicius.sweetRoom.model.Reservation;
import com.vinicius.sweetRoom.model.Room;
import com.vinicius.sweetRoom.model.User;
import com.vinicius.sweetRoom.model.enums.ReservationStatus;
import com.vinicius.sweetRoom.model.enums.UserRole;
import com.vinicius.sweetRoom.repository.ReservationRepository;
import com.vinicius.sweetRoom.repository.RoomRepository;
import com.vinicius.sweetRoom.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    private UserRepository userRepo;
    @Mock
    private RoomRepository roomRepo;
    @Mock
    private ReservationRepository reservationRepo;

    @Mock
    private ReservationValidator validator1;
    @Mock
    private ReservationValidator validator2;
    @Mock
    private ReservationValidator validator3;
    @Mock
    private ReservationValidator validator4;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        List<ReservationValidator> validators = List.of(validator1, validator2, validator3, validator4);
        reservationService = new ReservationService(userRepo, roomRepo, reservationRepo, validators);
    }

    @Test
    void createReservation_Success_RunsAllValidatorsAndSave() {
        // ARRANGE
        CreateReservationDTO reservationDTO = new CreateReservationDTO(1L, 1L, LocalDate.of(2026, 6, 3),
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        User mockUser = new User("Vinicius", "vinicius@email.com", UserRole.STUDENT);
        mockUser.setId(1L);

        Room mockRoom = new Room("Room A", 50, LocalTime.of(8, 0), LocalTime.of(18, 0), List.of(DayOfWeek.WEDNESDAY));
        mockRoom.setId(1L);

        Reservation savedReservation = new Reservation(mockUser, mockRoom, reservationDTO.reservationDate(),
                reservationDTO.reservationStart(), reservationDTO.reservationEnd());
        savedReservation.setId(100L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        when(roomRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(mockRoom));
        when(reservationRepo.save(any(Reservation.class))).thenReturn(savedReservation);

        // ACT
        ResponseReservationDTO result = reservationService.createReservation(reservationDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals(ReservationStatus.PENDING, result.status());

        verify(validator1, times(1)).validate(any(ReservationValidationContext.class));
        verify(validator2, times(1)).validate(any(ReservationValidationContext.class));
        verify(validator3, times(1)).validate(any(ReservationValidationContext.class));
        verify(validator4, times(1)).validate(any(ReservationValidationContext.class));

        verify(reservationRepo, times(1)).save(any(Reservation.class));

    }

    @Test
    void createReservation_ValidationErrorOccurrs_HaltsExecutionDoesNotSave() {
        CreateReservationDTO reservationDTO = new CreateReservationDTO(1L, 1L, LocalDate.of(2026, 6, 3),
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        User mockUser = new User("Vinicius", "vinicius@email.com", UserRole.STUDENT);
        mockUser.setId(1L);

        Room mockRoom = new Room("Room A", 50, LocalTime.of(8, 0), LocalTime.of(18, 0), List.of(DayOfWeek.WEDNESDAY));
        mockRoom.setId(1L);

        Reservation savedReservation = new Reservation(mockUser, mockRoom, reservationDTO.reservationDate(),
                reservationDTO.reservationStart(), reservationDTO.reservationEnd());
        savedReservation.setId(100L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        when(roomRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(mockRoom));

        doThrow(new ReservationValidationException("Time range is invalid")).when(validator1)
                .validate(any(ReservationValidationContext.class));

        // ACT ASSERT
        assertThrows(ReservationValidationException.class, () -> {
            reservationService.createReservation(reservationDTO);
        });

        verify(validator2, never()).validate(any(ReservationValidationContext.class));
        verify(reservationRepo, never()).save(any(Reservation.class));
    }
}
