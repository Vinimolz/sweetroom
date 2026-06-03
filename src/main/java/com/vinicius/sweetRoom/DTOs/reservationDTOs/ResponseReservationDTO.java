package com.vinicius.sweetRoom.DTOs.reservationDTOs;

import java.time.LocalDate;
import java.time.LocalTime;

import com.vinicius.sweetRoom.model.enums.ReservationStatus;

public record ResponseReservationDTO(
        Long userId,
        Long roomId,
        LocalDate reservationDate,
        LocalTime reservationStart,
        LocalTime reservationEnd,
        ReservationStatus status) {
}
