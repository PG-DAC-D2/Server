package com.ecommerce.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.entities.Customer;

public interface CustomerDao extends JpaRepository<Customer, Integer> {
    Customer findByEmail(String email);
}