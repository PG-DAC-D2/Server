package com.ecommerce.user_module.services;

import java.util.List;

import com.ecommerce.user_module.dtos.ApiResponse;
import com.ecommerce.user_module.dtos.UpdateUserDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;
import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.Status;

public interface UserService {
	List<UserRespDTO> getAllUsers();

	UserRespDTO getUserDetails(Long userId);

	ApiResponse encryptPassword();

	UserRespDTO addUser(UserReqDTO dto);

	UserRespDTO updateUser(Long userId, UpdateUserDTO dto);

	void updateUserStatus(Long userId, Status status);

	List<UserRespDTO> getUsersByRole(Role role);
}
