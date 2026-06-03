package com.vinicius.sweetRoom.Validator;

import java.time.LocalTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.vinicius.sweetRoom.exceptions.ReservationValidationException;

@Component
@Order(1)
public class TimeRangeValidator implements ReservationValidator {
    @Override
    public void validate(ReservationValidationContext context) {
        LocalTime start = context.dto().reservationStart();
        LocalTime end = context.dto().reservationEnd();

        if (start == null || end == null || !start.isBefore(end)) {
            throw new ReservationValidationException("Reservation start time must be strictly before end time");
        }
    }
}
