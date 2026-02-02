package com.ecommerce.user_module.controller;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.user_module.custom_exceptions.InvalidInputException;
import com.ecommerce.user_module.dtos.AuthRequest;
import com.ecommerce.user_module.dtos.AuthResp;
import com.ecommerce.user_module.dtos.UpdateUserDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;
import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.User;
import com.ecommerce.user_module.security.JWTUtils;
import com.ecommerce.user_module.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final JWTUtils jwtUtils;


	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody @Valid AuthRequest dto) {

		try {
			Authentication fullyAuthenticated = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

			User userEntity = (User) fullyAuthenticated.getPrincipal();

			// normalize roles for comparison
			String requestedRole = dto.getUserRole() == null ? "" : dto.getUserRole().toUpperCase();
			String actualRole = userEntity.getRole().name();

			if (requestedRole.length() > 0 && !requestedRole.equals(actualRole)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Role mismatch");
			}

			return ResponseEntity.ok(
					new AuthResp("Login Successful", jwtUtils.generateToken(userEntity), actualRole, userEntity.getId()));
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
		}
	}

	
	
	@GetMapping
	public ResponseEntity<?> getAllUsers() {
		System.out.println("in get all users");
		List<UserRespDTO> users = userService.getAllUsers();
		if (users.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
		return ResponseEntity.ok(users);
	}

	@PostMapping
	public ResponseEntity<?> addUser(@RequestBody UserReqDTO dto) {
		return ResponseEntity.ok(userService.addUser(dto));
	}

	/* ---------- UPDATE USER ---------- */
	@PutMapping("/{id}")
	public ResponseEntity<UserRespDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO dto) {

		return ResponseEntity.ok(userService.updateUser(id, dto));
	}

	/* ---------- UPDATE STATUS ---------- */
//	@PatchMapping("/{id}/status")
//	public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestBody StatusUpdateDTO dto) {
//
//		userService.updateUserStatus(id, dto.getStatus());
//		return ResponseEntity.noContent().build();
//	}

	/* ---------- GET USERS BY ROLE ---------- */
	@GetMapping("/role/{role}")
	public ResponseEntity<List<UserRespDTO>> getUsersByRole(@PathVariable Role role) {

		return ResponseEntity.ok(userService.getUsersByRole(role));
	}

	@GetMapping("/{userId}")

	@Operation(description = "Get user details by id ")
	public ResponseEntity<?> getUserDetailsById(@PathVariable Long userId) {
		System.out.println("in get user dtls " + userId);

		return ResponseEntity.ok(userService.getUserDetails(userId));

	}

	@PatchMapping("/pwd-encryption")
	@Operation(description = "Encrypt Password of all users")
	public ResponseEntity<?> encryptUserPassword() {
		log.info("encrypting users password ");
		return ResponseEntity.ok(userService.encryptPassword());

	}

}
