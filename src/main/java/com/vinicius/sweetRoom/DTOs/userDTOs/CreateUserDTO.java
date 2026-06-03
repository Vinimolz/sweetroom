package com.vinicius.sweetRoom.DTOs.userDTOs;

import com.vinicius.sweetRoom.model.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
                @NotBlank(message = "User name cannot be blank") String name,

                @NotBlank(message = "User must have a valid email") @Email(message = "User must have a valid email") String email,

                UserRole userRole) {
}
