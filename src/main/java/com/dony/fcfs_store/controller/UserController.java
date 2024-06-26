package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.request.LoginDto;
import com.dony.fcfs_store.dto.request.UpdatePasswordDto;
import com.dony.fcfs_store.dto.request.UserRequestDto;
import com.dony.fcfs_store.dto.response.TokenResponse;
import com.dony.fcfs_store.dto.response.UserResponseDto;
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

    @GetMapping("/user/my")
    public UserResponseDto getMyPage() {
        return userService.myPage();
    }

    @PostMapping("/user")
    public void createUser(@RequestBody UserRequestDto userDto) {
        userService.createUser(userDto);
    }

    @PatchMapping("/user/my")
    public void updateMyPage(@RequestBody UserRequestDto userDto) {
        userService.updateMyPage(userDto);
    }

    @PatchMapping("/user/my/password")
    public void updatePassword(@RequestBody UpdatePasswordDto dto) {
        userService.updatePassword(dto);
    }
}
