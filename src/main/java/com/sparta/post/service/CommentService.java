package com.sparta.post.service;

import com.sparta.post.dto.CommentRequestDto;
import com.sparta.post.dto.CommentResponseDto;
import com.sparta.post.entity.Comment;
import com.sparta.post.entity.Post;
import com.sparta.post.entity.User;
import com.sparta.post.jwt.JwtUtil;
import com.sparta.post.repository.CommentRepository;
import com.sparta.post.repository.PostRepository;
import com.sparta.post.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ResponseEntity<CommentResponseDto> createComment(String tokenValue, CommentRequestDto requestDto) {
        String token = authentication(tokenValue);
        String username = getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("없는 사용자 입니다."));
        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(() ->
                new IllegalArgumentException("없는 포스트 입니다."));
        Comment comment = new Comment(requestDto, user, post);
        commentRepository.save(comment);
        return new ResponseEntity<>(new CommentResponseDto(comment), HttpStatus.OK);
    }

    private String authentication(String tokenValue) {
        String decodedToken = jwtUtil.decodingToken(tokenValue);
        String token = jwtUtil.substringToken(decodedToken);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("인증되지 않은 토큰입니다.");
        }
        return token;
    }

    private String getUsernameFromToken(String token) {
        Claims info = jwtUtil.getUserInfoFromToken(token);
        return info.getSubject();
    }
}
