package com.ecommerce.user_module.services;

import com.ecommerce.user_module.dtos.UpdateAdminDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;

public interface AdminService {
    UserRespDTO registerAdmin(UserReqDTO dto);
    UpdateAdminDTO getAdminById(Long adminId);
    UpdateAdminDTO updateAdmin(UpdateAdminDTO dto);
}
