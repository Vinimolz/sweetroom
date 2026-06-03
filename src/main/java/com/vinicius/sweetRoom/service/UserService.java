package com.vinicius.sweetRoom.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vinicius.sweetRoom.DTOs.userDTOs.CreateUserDTO;
import com.vinicius.sweetRoom.DTOs.userDTOs.ResponseUserDTO;
import com.vinicius.sweetRoom.exceptions.DuplicatedResourceException;
import com.vinicius.sweetRoom.exceptions.ResourceNotFoundException;
import com.vinicius.sweetRoom.model.User;
import com.vinicius.sweetRoom.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ResponseUserDTO> getAllUsers() {
        return (convertListToDTO(userRepository.findAll()));
    }

    public ResponseUserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(u -> new ResponseUserDTO(u.getId(), u.getName(), u.getEmail(), u.getUserRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Could not find user with id: " + userId));
    }

    public ResponseUserDTO createUser(CreateUserDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new DuplicatedResourceException("This email already exists: " + dto.email());
        }

        User newUser = userRepository.save(new User(dto.name(), dto.email(), dto.userRole()));

        return toUserDTO(newUser);
    }

    private ResponseUserDTO toUserDTO(User user) {
        return new ResponseUserDTO(user.getId(), user.getName(), user.getEmail(), user.getUserRole());
    }

    private List<ResponseUserDTO> convertListToDTO(List<User> users) {
        return users.stream().map(this::toUserDTO).collect(Collectors.toList());
    }
}
