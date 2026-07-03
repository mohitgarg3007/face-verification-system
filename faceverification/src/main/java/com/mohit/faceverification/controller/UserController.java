package com.mohit.faceverification.controller;

import com.mohit.faceverification.dto.RegisterResponse;
import com.mohit.faceverification.dto.VerifyResponse;
import com.mohit.faceverification.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {

        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse>
    registerUser(
            @RequestParam String name,
            @RequestParam MultipartFile image)
            throws Exception {

        return ResponseEntity.ok(
                userService.registerUser(
                        name,
                        image));
    }
    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verifyUser(
            @RequestParam Long userId,
            @RequestParam MultipartFile image)
            throws Exception {

        return ResponseEntity.ok(
                userService.verifyUser(userId, image));
    }
}