package com.sparta.post.controller;

import com.sparta.post.dto.PostRequestDto;
import com.sparta.post.dto.PostResponseDto;
import com.sparta.post.dto.PostWithCommentResponseDto;
import com.sparta.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<PostResponseDto> createPost(@RequestHeader("Authorization") String token,
                                        @RequestBody PostRequestDto requestDto) {
        return postService.createPost(token, requestDto);
    }

    // 전체 게시글 조회
    @GetMapping("/post")
    public List<PostWithCommentResponseDto> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/post/{id}")
    public PostWithCommentResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@RequestHeader("Authorization") String token,
                                                      @PathVariable Long id,
                                                      @RequestBody PostRequestDto requestDto) {
         return postService.updatePost(token, id, requestDto);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id) {
        return postService.deletePost(token, id);
    }
}
