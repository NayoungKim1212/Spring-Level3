package com.sparta.post.controller;

import com.sparta.post.dto.SignupRequestDto;
import com.sparta.post.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public String signup(SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return null;
    }

}
