package com.sparta.post.controller;

import com.sparta.post.dto.CommentRequestDto;
import com.sparta.post.dto.CommentResponseDto;
import com.sparta.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    public ResponseEntity<CommentResponseDto> createComment(@RequestBody @Valid CommentRequestDto requestDto) {

    }
}
