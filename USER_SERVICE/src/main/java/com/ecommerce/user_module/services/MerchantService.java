package com.ecommerce.user_module.services;

import java.util.List;
import com.ecommerce.user_module.dtos.UpdateMerchantDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;

public interface MerchantService {
    UserRespDTO registerMerchant(UserReqDTO dto);
    UpdateMerchantDTO getMerchantById(Long merchantId);
    void softDeleteMerchant(Long merchantId);
    UpdateMerchantDTO updateMerchant(UpdateMerchantDTO dto);
    void restoreMerchant(Long merchantId);
    List<UpdateMerchantDTO> getAllMerchants();
}