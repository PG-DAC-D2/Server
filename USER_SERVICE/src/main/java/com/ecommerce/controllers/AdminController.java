package com.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.models.AppResponse;
import com.ecommerce.services.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/welcome")
    public AppResponse<?> welcome() {
        return AppResponse.success("Welcome Admin!");
    }

    @DeleteMapping("/merchant/{id}")
    public AppResponse<?> deactivateMerchant(@PathVariable int id) {
        return AppResponse.success(userService.deactivateMerchant(id));
    }
}