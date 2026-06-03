package com.vinicius.sweetRoom.Validator;

import java.time.DayOfWeek;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.vinicius.sweetRoom.exceptions.ReservationValidationException;

@Component
@Order(3)
public class DayAvailabilityValidator implements ReservationValidator {
    @Override
    public void validate(ReservationValidationContext context) {
        if (context.dto().reservationDate() == null) {
            throw new ReservationValidationException("Reservation date cannot be null");
        }

        DayOfWeek day = context.dto().reservationDate().getDayOfWeek();
        java.util.List<DayOfWeek> availableDays = context.room().getAvailableDays();

        if (availableDays == null || !availableDays.contains(day)) {
            throw new ReservationValidationException("The room is not operational on the selected day of the week (" + day + ")");
        }
    }
}
