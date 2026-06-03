package com.vinicius.sweetRoom.DTOs.roomDTOs;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ResponseRoomDTO(
        Long id,
        String name,
        Integer capacity,

        @JsonFormat(pattern = "HH:mm") 
        LocalTime operatingHoursStart,

        @JsonFormat(pattern = "HH:mm") 
        LocalTime operatingHoursEnd,

        List<DayOfWeek> availableDays) {
}
