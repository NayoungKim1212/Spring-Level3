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
import org.springframework.transaction.annotation.Transactional;

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
                new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 포스트 입니다."));
        Comment comment = new Comment(requestDto, user, post);
        commentRepository.save(comment);
        return new ResponseEntity<>(new CommentResponseDto(comment), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CommentResponseDto> updateComment(Long id, String tokenValue, CommentRequestDto requestDto) {
        String token = authentication(tokenValue);
        String username = getUsernameFromToken(token);
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 입니다."));
        usernameMatch(username, comment.getUser().getUsername());
        comment.update(requestDto);
        return new ResponseEntity<>(new CommentResponseDto(comment), HttpStatus.OK);
    }

    private String authentication(String tokenValue) {
        System.out.println("토큰 인증 및 반환");
        String decodedToken = jwtUtil.decodingToken(tokenValue);
        String token = jwtUtil.substringToken(decodedToken);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("인증되지 않은 토큰입니다.");
        }
        return token;
    }

    private void usernameMatch(String loginUsername, String commentUsername) {
        System.out.println("로그인한 유저와 선택한 댓글의 작성자가 일치하는지 확인");
        if (!loginUsername.equals(commentUsername)){
            throw new IllegalArgumentException("작성자가 일치하지 않습니다.");
        }
    }

    private String getUsernameFromToken(String token) {
        System.out.println("토큰에서 username 추출");
        Claims info = jwtUtil.getUserInfoFromToken(token);
        return info.getSubject();
    }
}
