package com.ecommerce.user_module.services;

import com.ecommerce.user_module.dtos.UpdateCustomerDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;

public interface CustomerService {
    UserRespDTO registerCustomer(UserReqDTO dto);
    UpdateCustomerDTO getCustomerById(Long customerId);
    UpdateCustomerDTO updateCustomer(UpdateCustomerDTO dto);
}