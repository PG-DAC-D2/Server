package com.ecommerce.user_module.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecommerce.user_module.dtos.UpdateMerchantDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.services.MerchantService;

// import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/merchants")
@Validated
@Slf4j
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping("/register")
    public ResponseEntity<?> registerMerchant(@RequestBody @Valid UserReqDTO dto) {
        return ResponseEntity.ok(merchantService.registerMerchant(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAllMerchants() {
        return ResponseEntity.ok(merchantService.getAllMerchants());
    }

    @GetMapping("/{merchantId}")
    public ResponseEntity<?> getMerchantById(@PathVariable Long merchantId) {
        return ResponseEntity.ok(merchantService.getMerchantById(merchantId));
    }
    
    @PutMapping("/soft-delete/{merchantId}")
    public ResponseEntity<?> softDeleteMerchant(@PathVariable Long merchantId) {
        merchantService.softDeleteMerchant(merchantId);
        return ResponseEntity.ok("Merchant soft deleted successfully");
    }
    
    @PutMapping("/restore/{merchantId}")
    public ResponseEntity<?> restoreMerchant(@PathVariable Long merchantId) {
        merchantService.restoreMerchant(merchantId);
        return ResponseEntity.ok("Merchant restored successfully");
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateMerchant(@RequestBody @Valid UpdateMerchantDTO dto) {
        UpdateMerchantDTO updated = merchantService.updateMerchant(dto);
        return ResponseEntity.ok(updated);
    }
    

}