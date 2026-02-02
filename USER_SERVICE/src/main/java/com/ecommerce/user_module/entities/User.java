package com.ecommerce.user_module.entities;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends Base implements UserDetails {
	
	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 200)
	private String password;

	@Column(nullable = false, length = 100)
	private String firstname;

	@Column(nullable = false, length = 100)
	private String lastname;
	
	@Column(name = "dob", nullable = false)
	private LocalDate dob;
	
	@Column(length = 15)
	private String phone;

	@Column(length = 255)
	private String address;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return List.of(new SimpleGrantedAuthority(this.role.name()));
	}

	@Override
	public String getUsername() {

		return this.email;
	}

	@Override
	public boolean isEnabled() {
		return this.status == Status.ACTIVE;
	}
}