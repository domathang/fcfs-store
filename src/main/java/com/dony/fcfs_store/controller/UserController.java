package com.dony.fcfs_store.controller;

import com.dony.fcfs_store.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final JwtUtil jwtUtil;

    @RequestMapping("/")
    public String hello(){
        return "hello";
    }

    @GetMapping("/token")
    public String token() {
        return jwtUtil.createToken(1);
    }
}
