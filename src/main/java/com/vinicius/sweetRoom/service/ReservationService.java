package com.vinicius.sweetRoom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vinicius.sweetRoom.DTOs.reservationDTOs.CreateReservationDTO;
import com.vinicius.sweetRoom.DTOs.reservationDTOs.ResponseReservationDTO;
import com.vinicius.sweetRoom.Validator.ReservationValidationContext;
import com.vinicius.sweetRoom.Validator.ReservationValidator;
import com.vinicius.sweetRoom.exceptions.ResourceNotFoundException;
import com.vinicius.sweetRoom.model.Reservation;
import com.vinicius.sweetRoom.model.Room;
import com.vinicius.sweetRoom.model.User;
import com.vinicius.sweetRoom.repository.ReservationRepository;
import com.vinicius.sweetRoom.repository.RoomRepository;
import com.vinicius.sweetRoom.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservationService {
        private final UserRepository userRepository;
        private final RoomRepository roomRepository;
        private final ReservationRepository reservationRepository;

        private final List<ReservationValidator> validators;

        public ReservationService(UserRepository userRepository, RoomRepository roomRepository,
                        ReservationRepository reservationRepository, List<ReservationValidator> validators) {

                this.userRepository = userRepository;
                this.roomRepository = roomRepository;
                this.reservationRepository = reservationRepository;
                this.validators = validators;
        }

        @Transactional
        public ResponseReservationDTO createReservation(CreateReservationDTO dto) {
                // Query both user and room
                User user = userRepository.findById(dto.userId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Could not find user with id: " + dto.userId()));

                Room room = roomRepository.findById(dto.roomId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Could not find room with id: " + dto.roomId()));

                // Create context for validation
                ReservationValidationContext context = new ReservationValidationContext(dto, user, room);

                // Chain of responsibility pattern
                for (ReservationValidator validator : validators) {
                        validator.validate(context);
                }

                // Create reservation
                Reservation reservation = new Reservation(user, room, dto.reservationDate(), dto.reservationStart(),
                                dto.reservationEnd());
                return reservationToDTO(reservationRepository.save(reservation));

        }

        private ResponseReservationDTO reservationToDTO(Reservation reservation) {
                return new ResponseReservationDTO(reservation.getUser().getId(),
                                reservation.getRoom().getId(),
                                reservation.getReservationDate(),
                                reservation.getReservationStart(),
                                reservation.getReservationEnd(),
                                reservation.getReservationStatus());
        }

}
