package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.service.CustomerService;
import com.utility.JwtUtility;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@RestController
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtility jwtUtility;

    @Autowired
    CustomerService customerService;

    @PostMapping("/authenticate")
    public String createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
//        final UserDetails userDetails = customerService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtility.generateToken(customerService.getUsername());
        return jwt;
    }
}
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthRequest {
    String username;
    String password;
}

