package com.sparta.post.controller;

import com.sparta.post.dto.ErrorResponseDto;
import com.sparta.post.dto.LoginRequestDto;
import com.sparta.post.dto.UserRequestDto;
import com.sparta.post.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ErrorResponseDto> signup(@RequestBody @Valid UserRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse res) {
        return userService.login(requestDto, res);
    }
}
