package com.ecommerce.user_module.dtos;

import java.time.LocalDate;

import com.ecommerce.user_module.entities.Status;

import lombok.Data;

@Data
public class UpdateUserDTO {

	private String firstname;
	private String lastname;
	private String phone;
	private String address;

	private LocalDate dob;
	private Status status;
}
