package com.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.daos.MerchantDao;
import com.ecommerce.entities.Merchant;
import com.ecommerce.entities.UserStatus;
import com.ecommerce.exceptions.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MerchantDao merchantDao;

    @Override
    public Merchant deactivateMerchant(int merchantId) {
        Merchant merchant = merchantDao.findById(merchantId)
            .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        merchant.setStatus(UserStatus.INACTIVE);
        return merchantDao.save(merchant);
    }
}