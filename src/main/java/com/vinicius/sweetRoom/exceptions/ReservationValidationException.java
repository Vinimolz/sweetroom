package com.vinicius.sweetRoom.exceptions;

public class ReservationValidationException extends RuntimeException {
    public ReservationValidationException(String message) {
        super(message);
    }
}
