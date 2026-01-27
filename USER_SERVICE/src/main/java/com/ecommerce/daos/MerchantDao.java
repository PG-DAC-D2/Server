package com.ecommerce.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecommerce.entities.Merchant;

public interface MerchantDao extends JpaRepository<Merchant, Integer> {
    Merchant findByEmail(String email);
}