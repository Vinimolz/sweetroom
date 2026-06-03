package com.vinicius.sweetRoom.DTOs.reservationDTOs;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record CreateReservationDTO(
        @NotNull(message = "User ID cannot be null") 
        Long userId,

        @NotNull(message = "Room ID cannot be null") 
        Long roomId,

        @NotNull(message = "Reservation date cannot be null")
        @FutureOrPresent(message = "Reservation date must be in the present or future")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate reservationDate,

        @NotNull(message = "Reservation start time cannot be null")
        @JsonFormat(pattern = "HH:mm")
        LocalTime reservationStart,

        @NotNull(message = "Reservation end time cannot be null")
        @JsonFormat(pattern = "HH:mm")
        LocalTime reservationEnd) {
}
