package com.ecommerce.user_module.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.user_module.dtos.UpdateAdminDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;
import com.ecommerce.user_module.entities.Admin;
import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.Status;
import com.ecommerce.user_module.entities.User;
import com.ecommerce.user_module.repository.AdminRepository;
import com.ecommerce.user_module.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRespDTO registerAdmin(UserReqDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = modelMapper.map(dto, User.class);
        user.setRole(Role.ROLE_ADMIN);
        user.setStatus(Status.ACTIVE);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        Admin admin = new Admin();
        admin.setUser(savedUser);
        adminRepository.save(admin);

        return modelMapper.map(savedUser, UserRespDTO.class);
    }

    @Override
    public UpdateAdminDTO getAdminById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
            .orElseGet(() -> 
                adminRepository.findByUserId(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"))
            );

        UpdateAdminDTO dto = new UpdateAdminDTO();
        dto.setAdminId(admin.getId());
        
        User user = admin.getUser();
        dto.setEmail(user.getEmail());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setDob(user.getDob());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        
        return dto;
    }

    @Override
    public UpdateAdminDTO updateAdmin(UpdateAdminDTO dto) {
        Admin admin = adminRepository.findById(dto.getAdminId())
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Update user fields
        User user = admin.getUser();
        modelMapper.map(dto, user);

        Admin updatedAdmin = adminRepository.save(admin);

        // Map back to DTO
        UpdateAdminDTO response = modelMapper.map(updatedAdmin, UpdateAdminDTO.class);
        response.setAdminId(updatedAdmin.getId());

        return response;
    }
}
