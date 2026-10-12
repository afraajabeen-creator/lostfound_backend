package com.campus.lostfound.service;

import com.campus.lostfound.dto.request.UserUpdateRequest;
import com.campus.lostfound.dto.response.UserSummaryDto;

import java.util.List;

/**
 * Service interface for administrator user management operations.
 */
public interface UserService {

    List<UserSummaryDto> getAllUsers();

    UserSummaryDto getUserById(Long id);

    UserSummaryDto updateUser(Long id, UserUpdateRequest request);

    UserSummaryDto toggleBlockStatus(Long id);
}
