package com.ecommerce.user_module.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "password")
public class AuthRequest {
	@NotBlank(message = "Email is required!")
	@Email(message = "Invalid Email format !")
	private String email;
	@NotBlank(message = "Password is required !")
	@Pattern(regexp = "((?=.*\\d)(?=.*[a-z]).{5,20})", message = "Invalid password format")
	private String password;
	
	private String userRole;
}