package com.sparta.post.controller;

import com.sparta.post.dto.PostRequestDto;
import com.sparta.post.dto.PostResponseDto;
import com.sparta.post.jwt.JwtUtil;
import com.sparta.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping("/post") // @RequestHeader("Authorization") String token, // 헤더에 "Authorization" 필드로 토큰을 전달받음 -> Spring Security로 인가, 인증을해서 authentication에서 name을 가겨온다.
    public ResponseEntity<?> createPost(@RequestBody PostRequestDto requestDto) {
        PostResponseDto responseDto = postService.createPost(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 전체 게시글 조회
    @GetMapping("/post")
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/post/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id,
                                                      @RequestBody PostRequestDto requestDto) {
         PostResponseDto responseDto = postService.updatePost(id, requestDto);
         return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제 되었습니다.");
    }
}
