package com.vinicius.sweetRoom.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.vinicius.sweetRoom.model.enums.ReservationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reservationDate;

    private LocalTime reservationStart;

    private LocalTime reservationEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    public Reservation() {

    }

    public Reservation(User user, Room room, LocalDate reservationDate, LocalTime reservationStarts,
            LocalTime reservationEnds) {
        this.user = user;
        this.room = room;
        this.reservationDate = reservationDate;
        this.reservationStart = reservationStarts;
        this.reservationEnd = reservationEnds;

        this.reservationStatus = ReservationStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getReservationStart() {
        return reservationStart;
    }

    public void setReservationStart(LocalTime reservationStart) {
        this.reservationStart = reservationStart;
    }

    public LocalTime getReservationEnd() {
        return reservationEnd;
    }

    public void setReservationEnd(LocalTime reservationEnd) {
        this.reservationEnd = reservationEnd;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

}
