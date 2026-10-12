package com.campus.lostfound.service;

import com.campus.lostfound.dto.request.LoginRequest;
import com.campus.lostfound.dto.request.RegisterRequest;
import com.campus.lostfound.dto.response.JwtAuthResponse;
import com.campus.lostfound.entity.User;

/**
 * Service interface for user authentication and registration.
 */
public interface AuthService {

    JwtAuthResponse register(RegisterRequest request);

    JwtAuthResponse login(LoginRequest request);

    User getCurrentUser(String email);
}
