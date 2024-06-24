package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.*;
import com.dony.fcfs_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginDto dto) {
        return userService.login(dto);
    }

    @DeleteMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
    }

    @GetMapping("/user/{id}")
    public UserResponseDto getMyPage(@PathVariable Integer id) {
        return userService.myPage(id);
    }

    @PostMapping("/user")
    public void createUser(@RequestBody UserRequestDto userDto) {
        userService.createUser(userDto);
    }
}
