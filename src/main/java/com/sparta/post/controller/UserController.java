package com.sparta.post.controller;

import com.sparta.post.dto.UserRequestDto;
import com.sparta.post.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    public ResponseEntity<?> signup(@RequestBody @Valid UserRequestDto requestDto) {
        userService.signup(requestDto);
        return new ResponseEntity<>("회원 가입 성공", HttpStatus.CREATED);
    }

}
