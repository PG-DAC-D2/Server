package com.ecommerce.user_module.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.user_module.dtos.UpdateCustomerDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UserRespDTO;
import com.ecommerce.user_module.entities.Customer;
import com.ecommerce.user_module.entities.Role;
import com.ecommerce.user_module.entities.Status;
import com.ecommerce.user_module.entities.User;
import com.ecommerce.user_module.repository.CustomerRepository;
import com.ecommerce.user_module.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRespDTO registerCustomer(UserReqDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = modelMapper.map(dto, User.class);
        user.setRole(Role.ROLE_CUSTOMER);
        user.setStatus(Status.ACTIVE);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        Customer customer = new Customer();
        customer.setUser(savedUser);
        // Set accountNumber if needed, for now null
        customerRepository.save(customer);

        return modelMapper.map(savedUser, UserRespDTO.class);
    }

    @Override
    public UpdateCustomerDTO getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseGet(() -> 
                customerRepository.findByUserId(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"))
            );

        UpdateCustomerDTO dto = new UpdateCustomerDTO();
        dto.setCustomerId(customer.getId());
        dto.setAccountNumber(customer.getAccountNumber());
        
        User user = customer.getUser();
        dto.setEmail(user.getEmail());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setDob(user.getDob());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        
        return dto;
    }

    @Override
    public UpdateCustomerDTO updateCustomer(UpdateCustomerDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Update customer fields
        customer.setAccountNumber(dto.getAccountNumber());

        // Update user fields using ModelMapper
        User user = customer.getUser();
        modelMapper.map(dto, user);

        Customer updatedCustomer = customerRepository.save(customer);

        // Map back to DTO
        UpdateCustomerDTO response = modelMapper.map(updatedCustomer, UpdateCustomerDTO.class);
        response.setCustomerId(updatedCustomer.getId()); // Ensure customerId is set

        return response;
    }
}