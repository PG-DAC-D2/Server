package com.ecommerce.user_module.services;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.user_module.custom_exceptions.ResourceNotFoundException;
import com.ecommerce.user_module.dtos.ApiResponse;
import com.ecommerce.user_module.dtos.UpdateUserDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;
import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.Status;
import com.ecommerce.user_module.entities.User;
import com.ecommerce.user_module.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public List<UserRespDTO> getAllUsers() {
		return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserRespDTO.class)).toList();
	}

	@Override
	public UserRespDTO getUserDetails(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid user id !!!!!!!"));

		return modelMapper.map(user, UserRespDTO.class);
	}

	@Override
	public ApiResponse encryptPassword() {

		List<User> users = userRepository.findAll();

		users.forEach(user -> user.setPassword(passwordEncoder.encode(user.getPassword())));
		return new ApiResponse("Password encrypted", "Success");
	}

	@Override
	public UserRespDTO addUser(UserReqDTO dto) {

		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email already exists");
		}

		User user = modelMapper.map(dto, User.class);

		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		User savedUser = userRepository.save(user);

		return modelMapper.map(savedUser, UserRespDTO.class);
	}

	@Override
	public UserRespDTO updateUser(Long userId, UpdateUserDTO dto) {

		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		modelMapper.map(dto, user);

		User updatedUser = userRepository.save(user);

		return modelMapper.map(updatedUser, UserRespDTO.class);
	}

	@Override
	public void updateUserStatus(Long userId, Status status) {

		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		user.setStatus(status);
		userRepository.save(user);
	}

	@Override
	public List<UserRespDTO> getUsersByRole(Role role) {

		return userRepository.findByRole(role).stream().map(user -> modelMapper.map(user, UserRespDTO.class)).toList();
	}
}
