package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.config.JwtUtil;
import com.dony.fcfs_store.dto.EmailRequestDto;
import com.dony.fcfs_store.dto.UserRequestDto;
import com.dony.fcfs_store.dto.UserResponseDto;
import com.dony.fcfs_store.repository.UserRepository;
import com.dony.fcfs_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final JwtUtil jwtUtil;

    private final UserService userService;

    @GetMapping("/token")
    public String token() {
        return jwtUtil.createToken(1);
    }

    @PostMapping("/email")
    public void sendEmail(@RequestBody EmailRequestDto emailRequestDto) {
        // TODO
    }

    @GetMapping("/user/{id}")
    public UserResponseDto getMyPage(@PathVariable Integer id) throws Exception{
        return userService.myPage(id);
    }

    @PostMapping("/user")
    public void createUser(@RequestBody UserRequestDto userDto) throws Exception{
        userService.createUser(userDto);
    }
}
