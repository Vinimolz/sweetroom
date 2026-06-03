package com.vinicius.sweetRoom.DTOs.roomDTOs;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateRoomDTO(
        @NotBlank(message = "Room name cannot be blank") 
        String name,

        @NotNull(message = "Room capacity cannot be null") 
        @Min(value = 1, message = "Room capacity must be at least 1") 
        Integer capacity,

        @NotNull(message = "Operating start hours cannot be null") 
        @JsonFormat(pattern = "HH:mm") 
        LocalTime operatingHoursStart,

        @NotNull(message = "Operating end hours cannot be null") 
        @JsonFormat(pattern = "HH:mm") 
        LocalTime operatingHoursEnd,

        @NotEmpty(message = "Available days list cannot be empty") 
        List<DayOfWeek> availableDays) {
}
