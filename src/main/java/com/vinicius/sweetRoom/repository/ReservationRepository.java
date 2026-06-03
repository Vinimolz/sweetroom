package com.vinicius.sweetRoom.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vinicius.sweetRoom.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.room.id = :roomId " +
            "AND r.reservationDate = :date " +
            "AND r.reservationStatus <> com.vinicius.sweetRoom.model.enums.ReservationStatus.CANCELLED " +
            "AND (r.reservationStart < :endTime AND r.reservationEnd > :startTime)")
    boolean hasOverlappingReservation(
            @Param("roomId") Long roomId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime StartTime,
            @Param("endTime") LocalTime endtime);
}
