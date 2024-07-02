package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.dto.request.UpdatePasswordDto;
import com.dony.fcfs_store.dto.request.UserRequestDto;
import com.dony.fcfs_store.dto.response.UserResponseDto;
import com.dony.fcfs_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/my")
    public UserResponseDto getMyPage() {
        return userService.getMyPage();
    }

    @PostMapping("/user")
    public void signup(@RequestBody UserRequestDto userDto) {
        userService.signup(userDto);
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
