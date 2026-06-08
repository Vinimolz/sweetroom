package com.vinicius.sweetRoom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vinicius.sweetRoom.DTOs.userDTOs.CreateUserDTO;
import com.vinicius.sweetRoom.DTOs.userDTOs.ResponseUserDTO;
import com.vinicius.sweetRoom.exceptions.DuplicatedResourceException;
import com.vinicius.sweetRoom.model.User;
import com.vinicius.sweetRoom.model.enums.UserRole;
import com.vinicius.sweetRoom.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_SuccessfulCreation_ReturnsResponseUserDTO() {
        // ARRANGE
        CreateUserDTO createUserDTO = new CreateUserDTO("vinicius", "validemail@email.com", "password123", UserRole.STUDENT);

        User mockUser = new User("Vinicius", "validemail@email.com", UserRole.STUDENT);
        mockUser.setId(1L);

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("hashedPassword");

        when(userRepo.existsByEmail(mockUser.getEmail())).thenReturn(false);

        when(userRepo.save(any(User.class))).thenReturn(mockUser);

        // ACT
        ResponseUserDTO result = userService.createUser(createUserDTO);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.id());

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepo, times(1)).existsByEmail("validemail@email.com");
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void createUser_UnsuccessfulCreation_ReturnsDuplicatedResourceException() {
        // ARRANGE
        CreateUserDTO createUserDTO = new CreateUserDTO("vinicius", "validemail.com", "password123", UserRole.STUDENT);

        when(userRepo.existsByEmail(createUserDTO.email())).thenReturn(true);

        // ACT & ASSERT
        DuplicatedResourceException ex = assertThrows(DuplicatedResourceException.class, () -> userService.createUser(createUserDTO));

        assertEquals("This email already exists: " + createUserDTO.email(), ex.getMessage());

        verify(userRepo, never()).save(any());
    }

    @Test
    void getUserById_SuccessfulFetch_ReturnResponseUserDTO() {
        // ARRANGE
        Long mockId = 1L;

        User mockUser = new User("Vinicius", "validemail@email.com", UserRole.STUDENT);
        mockUser.setId(1L);

        when(userRepo.findById(mockId)).thenReturn(Optional.of(mockUser));

        // ACT
        ResponseUserDTO result = userService.getUserById(mockId);

        // ASSERT
        assertNotNull(result);
        assertEquals(1L, result.id());

        verify(userRepo, times(1)).findById(mockId);
    }
}
