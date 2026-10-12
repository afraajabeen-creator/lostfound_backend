package com.campus.lostfound.service;

import com.campus.lostfound.dto.request.UserUpdateRequest;
import com.campus.lostfound.dto.response.UserSummaryDto;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.enums.Role;
import com.campus.lostfound.exception.BadRequestException;
import com.campus.lostfound.exception.ResourceNotFoundException;
import com.campus.lostfound.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of UserService.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserSummaryDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserSummaryDto.fromEntity(user);
    }

    @Override
    public UserSummaryDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getIsBlocked() != null) {
            user.setBlocked(request.getIsBlocked());
        }

        User updated = userRepository.save(user);
        return UserSummaryDto.fromEntity(updated);
    }

    @Override
    public UserSummaryDto toggleBlockStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Prevent blocking the master admin
        if ("admin@campus.com".equalsIgnoreCase(user.getEmail())) {
            throw new BadRequestException("Cannot block default campus administrator!");
        }

        user.setBlocked(!user.isBlocked());
        User updated = userRepository.save(user);
        return UserSummaryDto.fromEntity(updated);
    }
}
