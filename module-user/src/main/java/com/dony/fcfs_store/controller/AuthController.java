package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.request.EmailRequestDto;
import com.dony.fcfs_store.dto.request.LoginDto;
import com.dony.fcfs_store.dto.response.PassportResponse;
import com.dony.fcfs_store.dto.response.TokenDto;
import com.dony.fcfs_store.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth/email")
    public void sendAuthEmail(@RequestBody EmailRequestDto dto) {
        authService.sendEmail(dto.getEmail());
    }

    @GetMapping("/auth/verify")
    public void authTokenVerify(@RequestParam String token) {
        authService.verify(token);
    }

    @PostMapping("/auth/login")
    public TokenDto login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }

    @DeleteMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
    }

    @GetMapping("/auth/passport/{id}")
    public PassportResponse getPassportByAccessToken(@PathVariable Integer id) {
        return authService.getUserPassportByAccessToken(id);
    }

//    @GetMapping("/auth/test")
//    public void test(@RequestHeader("X-Passport") String passport) {
//        System.out.println(passport);
//    }
}
