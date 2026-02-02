package com.ecommerce.user_module.dtos;

import java.time.LocalDate;

import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.Status;

import lombok.Data;

@Data
public class UserRespDTO {
	private Long id;
	private String firstname;
	private String lastname;
	private Role role;

	private LocalDate dob;
	private String email;
	private String phone;
	private String address;
	private Status status;
}
