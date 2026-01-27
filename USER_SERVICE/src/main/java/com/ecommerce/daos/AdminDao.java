package com.ecommerce.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.entities.Admin;

public interface AdminDao extends JpaRepository<Admin, Integer> {
    Admin findByEmail(String email);
}