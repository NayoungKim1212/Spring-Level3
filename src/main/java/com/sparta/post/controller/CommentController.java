package com.sparta.post.controller;

import com.sparta.post.dto.CommentRequestDto;
import com.sparta.post.dto.CommentResponseDto;
import com.sparta.post.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.sparta.post.jwt.JwtUtil.AUTHORIZATION_HEADER;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    @PostMapping("/comment") // @RequestHeader(name = "Authorization") 상수로 정해주는게 유지보수 측면에서 아쉬움(Header name이 바뀌면 코드를 다 바꿔줘야함) // jwtUtil.AUTHORIZATION_HEADER로 받아오면 가능 // 근데 Controller에 JwtUtil을 주입받는게 별로임 // 찾아보니 import static 으로 상수를 가져올 수 있다. 대신 가독성이 떨어지는듯
    public ResponseEntity<CommentResponseDto> createComment(@RequestHeader(name = AUTHORIZATION_HEADER) String token,
                                                            @RequestBody CommentRequestDto requestDto){
        return commentService.createComment(token, requestDto);
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@RequestHeader(name = AUTHORIZATION_HEADER) String token,
                                                            @PathVariable Long id,
                                                            @RequestBody CommentRequestDto requestDto){
        return commentService.updateComment(token, id, requestDto);
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<String> deleteComment(@RequestHeader(name = AUTHORIZATION_HEADER) String token,
                                                @PathVariable Long id){
        return commentService.deleteComment(token, id);
    }
}
