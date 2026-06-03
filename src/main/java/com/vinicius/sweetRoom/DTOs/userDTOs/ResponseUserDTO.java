package com.vinicius.sweetRoom.DTOs.userDTOs;

import com.vinicius.sweetRoom.model.enums.UserRole;

public record ResponseUserDTO(
                Long id,
                String name,
                String email,
                UserRole userRole) {
}
