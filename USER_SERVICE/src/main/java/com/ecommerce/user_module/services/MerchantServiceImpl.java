package com.ecommerce.user_module.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.user_module.dtos.UpdateMerchantDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;
import com.ecommerce.user_module.entities.Merchant;
import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.Status;
import com.ecommerce.user_module.entities.User;
import com.ecommerce.user_module.repository.MerchantRepository;
import com.ecommerce.user_module.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRespDTO registerMerchant(UserReqDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = modelMapper.map(dto, User.class);
        user.setRole(Role.ROLE_MERCHANT);
        user.setStatus(Status.ACTIVE);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        Merchant merchant = new Merchant();
        merchant.setUser(savedUser);
        merchant.setStoreName(user.getFirstname()+ " " + user.getLastname() + " store");
        // Set storeName if available, for now null
        merchantRepository.save(merchant);

        return modelMapper.map(savedUser, UserRespDTO.class);
    }

    @Override
    public UpdateMerchantDTO getMerchantById(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseGet(() -> 
                merchantRepository.findByUserId(merchantId)
                    .orElseThrow(() -> new RuntimeException("Merchant not found"))
            );

        UpdateMerchantDTO dto = new UpdateMerchantDTO();
        dto.setMerchantId(merchant.getId());
        dto.setStoreName(merchant.getStoreName());
        
        User user = merchant.getUser();
        dto.setEmail(user.getEmail());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setDob(user.getDob());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setStatus(user.getStatus().toString());
        
        return dto;
    }

    @Override
    public void softDeleteMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new RuntimeException("Merchant not found"));
        merchant.getUser().setStatus(Status.BLOCKED);
        merchantRepository.save(merchant);
    }

    @Override
    public void restoreMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(() -> new RuntimeException("Merchant not found"));
        merchant.getUser().setStatus(Status.ACTIVE);
        merchantRepository.save(merchant);
    }

    @Override
    public UpdateMerchantDTO updateMerchant(UpdateMerchantDTO dto) {
        Merchant merchant = merchantRepository.findById(dto.getMerchantId())
            .orElseThrow(() -> new RuntimeException("Merchant not found"));

        // Update merchant fields
        merchant.setStoreName(dto.getStoreName());

        // Update user fields using ModelMapper
        User user = merchant.getUser();
        modelMapper.map(dto, user);

        Merchant updatedMerchant = merchantRepository.save(merchant);

        // Map back to DTO
        UpdateMerchantDTO response = modelMapper.map(updatedMerchant, UpdateMerchantDTO.class);
        response.setMerchantId(updatedMerchant.getId()); // Ensure merchantId is set

        return response;
    }

    @Override
    public List<UpdateMerchantDTO> getAllMerchants() {
        return merchantRepository.findAll().stream()
            .map(merchant -> {
                UpdateMerchantDTO dto = new UpdateMerchantDTO();
                dto.setMerchantId(merchant.getId());
                dto.setStoreName(merchant.getStoreName());

                User user = merchant.getUser();
                dto.setEmail(user.getEmail());
                dto.setFirstname(user.getFirstname());
                dto.setLastname(user.getLastname());
                dto.setDob(user.getDob());
                dto.setPhone(user.getPhone());
                dto.setAddress(user.getAddress());
                dto.setStatus(user.getStatus().toString());

                return dto;
            })
            .collect(Collectors.toList());
    }
}