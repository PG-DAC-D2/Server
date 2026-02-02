package com.ecommerce.user_module.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.dtos.UpdateCustomerDTO;
import com.ecommerce.user_module.services.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/customers")
@Validated
@Slf4j
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody @Valid UserReqDTO dto) {
        return ResponseEntity.ok(customerService.registerCustomer(dto));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateCustomer(@RequestBody @Valid UpdateCustomerDTO dto) {
        UpdateCustomerDTO updated = customerService.updateCustomer(dto);
        return ResponseEntity.ok(updated);
    }
    
}
