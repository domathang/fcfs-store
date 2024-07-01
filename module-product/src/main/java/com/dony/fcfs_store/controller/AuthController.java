package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.request.EmailRequestDto;
import com.dony.fcfs_store.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth")
    public void sendEmail(@RequestBody EmailRequestDto dto) {
        authService.sendEmail(dto.getEmail());
    }

    @GetMapping("/verify")
    public void authTokenVerify(@RequestParam String token) {
        authService.verify(token);
    }
}
