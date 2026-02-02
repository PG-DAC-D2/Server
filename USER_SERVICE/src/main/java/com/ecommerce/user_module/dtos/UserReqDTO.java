package com.ecommerce.user_module.dtos;

import java.time.LocalDate;

import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserReqDTO {

	private String firstname;
	private String lastname;
	@NotBlank
	private String email;
	@NotBlank
	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z]).{5,20})", message = "Invalid password format")
	private String password;
	@NotBlank
	private String phone;
	private String address;

	@NotNull
	@PastOrPresent
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dob;
	private Role role;
	private Status status;
}
