package com.vinicius.sweetRoom.Validator;

import java.time.LocalTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.vinicius.sweetRoom.exceptions.ReservationValidationException;

@Component
@Order(2)
public class OperatingHoursValidator implements ReservationValidator {

    @Override
    public void validate(ReservationValidationContext context) {
        LocalTime reservationStart = context.dto().reservationStart();
        LocalTime reservationEnd = context.dto().reservationEnd();

        LocalTime roomOperationStart = context.room().getOperatingHoursStart();
        LocalTime roomOperationEnd = context.room().getOperatingHoursEnd();

        if (reservationStart.isBefore(roomOperationStart) || reservationEnd.isAfter(roomOperationEnd)) {
            throw new ReservationValidationException("Reservation hours must be within room operating hours");
        }
    }

}
