package com.vinicius.sweetRoom.Validator;

import com.vinicius.sweetRoom.DTOs.reservationDTOs.CreateReservationDTO;
import com.vinicius.sweetRoom.model.Room;
import com.vinicius.sweetRoom.model.User;

public record ReservationValidationContext(
                CreateReservationDTO dto,
                User user,
                Room room) {

}
