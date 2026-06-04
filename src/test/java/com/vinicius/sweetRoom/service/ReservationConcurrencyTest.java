package com.vinicius.sweetRoom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vinicius.sweetRoom.DTOs.reservationDTOs.CreateReservationDTO;
import com.vinicius.sweetRoom.model.Room;
import com.vinicius.sweetRoom.model.User;
import com.vinicius.sweetRoom.model.enums.UserRole;
import com.vinicius.sweetRoom.repository.ReservationRepository;
import com.vinicius.sweetRoom.repository.RoomRepository;
import com.vinicius.sweetRoom.repository.UserRepository;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User savedUser;
    private Room savedRoom;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();

        // Seed a User
        User user = new User("Vinicius", "vinicius@email.com", UserRole.STUDENT);
        savedUser = userRepository.save(user);

        // Seed a Room that is open on Monday (2026-06-08 is a Monday)
        Room room = new Room(
                "Conference Room A",
                10,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                List.of(DayOfWeek.MONDAY));
        savedRoom = roomRepository.save(room);
    }

    @Test
    void createReservation_ConcurrentRequests_OnlyOneSucceeds() throws InterruptedException {
        int threadCount = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // 2026-06-08 is a Monday
        CreateReservationDTO dto = new CreateReservationDTO(
                savedUser.getId(),
                savedRoom.getId(),
                LocalDate.of(2026, 6, 8),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0));

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Wait for the starting gun!
                    reservationService.createReservation(dto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Fire the starting gun!
        startLatch.countDown();

        // Wait for all threads to finish
        endLatch.await();
        executorService.shutdown();

        // ASSERTIONS
        assertEquals(1, successCount.get(), "Exactly one reservation request should succeed");
        assertEquals(threadCount - 1, failureCount.get(), "All other requests should fail");
        assertEquals(1, reservationRepository.count(), "Only one reservation should be saved in the database");
    }
}
