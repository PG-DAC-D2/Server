package com.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.daos.*;
import com.ecommerce.entities.*;
import com.ecommerce.models.*;
import com.ecommerce.security.JwtUtil;

@RestController
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder encoder;

    @Autowired private CustomerDao customerDao;
    @Autowired private MerchantDao merchantDao;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome Admin!";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody Credentials cr) {
        Authentication auth =
            new UsernamePasswordAuthenticationToken(cr.getEmail(), cr.getPassword());

        auth = authManager.authenticate(auth);
        return ResponseEntity.ok(jwtUtil.createToken(auth));
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody RegisterRequest req) {
        Customer c = new Customer();
        c.setName(req.getName());
        c.setEmail(req.getEmail());
        c.setPassword(encoder.encode(req.getPassword()));
        customerDao.save(c);
        return ResponseEntity.ok(AppResponse.success("Customer Registered"));
    }

    @PostMapping("/register/merchant")
    public ResponseEntity<?> registerMerchant(@RequestBody RegisterRequest req) {
        Merchant m = new Merchant();
        m.setShopName(req.getName());
        m.setEmail(req.getEmail());
        m.setPassword(encoder.encode(req.getPassword()));
        merchantDao.save(m);
        return ResponseEntity.ok(AppResponse.success("Merchant Registered"));
    }
}