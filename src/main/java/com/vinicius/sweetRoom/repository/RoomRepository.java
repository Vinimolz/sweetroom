package com.vinicius.sweetRoom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vinicius.sweetRoom.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByName(String name);
}
