package com.vinicius.sweetRoom.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vinicius.sweetRoom.DTOs.reservationDTOs.CreateReservationDTO;
import com.vinicius.sweetRoom.DTOs.reservationDTOs.ResponseReservationDTO;
import com.vinicius.sweetRoom.model.enums.ReservationStatus;
import com.vinicius.sweetRoom.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/book")
    public ResponseEntity<ResponseReservationDTO> createReservation(@Valid @RequestBody CreateReservationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(dto));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<ResponseReservationDTO>> getReservationByUserIdAndStatus(@PathVariable("id") Long userId,
            @RequestParam ReservationStatus status) {
        return ResponseEntity.ok(reservationService.getByUserIdAndStatus(userId, status));
    }
}
