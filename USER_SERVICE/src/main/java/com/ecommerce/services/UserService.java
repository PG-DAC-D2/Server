package com.ecommerce.services;

import com.ecommerce.entities.Merchant;

public interface UserService {
    Merchant deactivateMerchant(int merchantId);
}