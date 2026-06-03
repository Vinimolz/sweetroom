package com.vinicius.sweetRoom.Validator;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.vinicius.sweetRoom.exceptions.ReservationValidationException;
import com.vinicius.sweetRoom.repository.ReservationRepository;

@Component
@Order(4)
public class RoomAvailabilityValidator implements ReservationValidator {
    private final ReservationRepository reservationRepository;

    public RoomAvailabilityValidator(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void validate(ReservationValidationContext context) {
        boolean hasOverlap = reservationRepository.hasOverlappingReservation(
                context.room().getId(),
                context.dto().reservationDate(),
                context.dto().reservationStart(),
                context.dto().reservationEnd());

        if (hasOverlap) {
            throw new ReservationValidationException("This room is already reserved during the selected time slot");
        }
    }
}
