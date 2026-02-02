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

import com.ecommerce.user_module.dtos.UpdateAdminDTO;
import com.ecommerce.user_module.dtos.UserReqDTO;
import com.ecommerce.user_module.services.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody @Valid UserReqDTO dto) {
        return ResponseEntity.ok(adminService.registerAdmin(dto));
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdminById(@PathVariable Long adminId) {
        return ResponseEntity.ok(adminService.getAdminById(adminId));
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateAdmin(@RequestBody @Valid UpdateAdminDTO dto) {
        UpdateAdminDTO updated = adminService.updateAdmin(dto);
        return ResponseEntity.ok(updated);
    }
}
