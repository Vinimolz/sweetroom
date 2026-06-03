package com.vinicius.sweetRoom.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.DayOfWeek;

import com.vinicius.sweetRoom.model.converters.DayOfWeekBitmaskConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer capacity;

    private LocalTime operatingHoursStart;

    private LocalTime operatingHoursEnd;

    @Convert(converter = DayOfWeekBitmaskConverter.class)
    @Column(name = "available_days_mask")
    private List<DayOfWeek> availableDays;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Reservation> reservation = new ArrayList<>();

    public Room() {
    }

    public Room(
            String name,
            Integer capacity,
            LocalTime operatingHoursStart,
            LocalTime operatingHoursEnd,
            List<DayOfWeek> availableDays) {

        this.name = name;
        this.capacity = capacity;
        this.operatingHoursStart = operatingHoursStart;
        this.operatingHoursEnd = operatingHoursEnd;
        this.availableDays = availableDays;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public void setOperatingHoursStart(LocalTime startTime) {
        this.operatingHoursStart = startTime;
    }

    public LocalTime getOperatingHoursStart() {
        return this.operatingHoursStart;
    }

    public void setOperatingHoursEnd(LocalTime endTime) {
        this.operatingHoursEnd = endTime;
    }

    public LocalTime getOperatingHoursEnd() {
        return this.operatingHoursEnd;
    }

    public void setAvailableDays(List<DayOfWeek> availableDays) {
        this.availableDays = availableDays;
    }

    public List<DayOfWeek> getAvailableDays() {
        return this.availableDays;
    }

    public void setReservation(Reservation reservation) {
        this.reservation.add(reservation);
    }

    public List<Reservation> getReservation() {
        return this.reservation;
    }
}
