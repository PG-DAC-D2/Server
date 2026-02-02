package com.ecommerce.user_module.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.Status;
import com.ecommerce.user_module.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	boolean existsByEmail(String em);

	Optional<User> findByEmailAndPassword(String email, String password);

	Optional<User> findByEmail(String email);

	long countByRoleAndStatus(Role role, Status status);

	List<User> findByRole(Role role);

}
