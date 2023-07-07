package com.sparta.post.service;

import com.sparta.post.dto.CommentRequestDto;
import com.sparta.post.dto.CommentResponseDto;
import com.sparta.post.entity.Comment;
import com.sparta.post.entity.Post;
import com.sparta.post.entity.User;
import com.sparta.post.entity.UserRoleEnum;
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
        Claims info = authentication(tokenValue); // 토큰 인증 및 사용자 정보 반환
        String username = info.getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        Post post = postRepository.findById(requestDto.getPostId()).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 포스트 입니다."));
        Comment comment = new Comment(requestDto, user, post);
        commentRepository.save(comment);
        return new ResponseEntity<>(new CommentResponseDto(comment), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<CommentResponseDto> updateComment(String tokenValue, Long id, CommentRequestDto requestDto) {
        Claims info = authentication(tokenValue); // 토큰 인증 및 사용자 정보 반환
        Comment comment = findComment(id);
        if(hasRoleAdmin(info)){ // 관리자 권한인지 확인
            comment.update(requestDto);
            return new ResponseEntity<>(new CommentResponseDto(comment), HttpStatus.OK);
        }
        String username = info.getSubject(); // jwt토큰에서 사용자의 식별자 값을 추출하여 username 변수에 할당
        usernameMatch(username, comment.getUser().getUsername());
        comment.update(requestDto);
        return new ResponseEntity<>(new CommentResponseDto(comment), HttpStatus.OK);
    }

    public ResponseEntity<String> deleteComment(String tokenValue, Long id) {
        Claims info = authentication(tokenValue);
        Comment comment = findComment(id);
        if(hasRoleAdmin(info)){
            commentRepository.delete(comment);
            return new ResponseEntity<>("댓글이 삭제 되었습니다.",HttpStatus.OK);
        }
        String username = info.getSubject();
        usernameMatch(username, comment.getUser().getUsername());
        commentRepository.delete(comment);
        return new ResponseEntity<>("댓글이 삭제 되었습니다.",HttpStatus.OK);
    }

    private Claims authentication(String tokenValue) {
        System.out.println("토큰 인증 및 사용자 정보 반환");
        String decodedToken = jwtUtil.decodingToken(tokenValue);
        String token = jwtUtil.substringToken(decodedToken);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("인증되지 않은 토큰입니다.");
        }
        return jwtUtil.getUserInfoFromToken(token);
    }

    private boolean hasRoleAdmin(Claims info) {
        System.out.println("권한 확인중");
        if (info.get(jwtUtil.AUTHORIZATION_KEY).equals(UserRoleEnum.ADMIN.name())) { // UserRoleEnum.ADMIN.name = "ADMIN"
            return true;
        }
        return false;
    }

    private Comment findComment(Long id) {
        System.out.println("댓글 찾기");
        return commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 댓글 입니다."));
    }

    private void usernameMatch(String loginUsername, String commentUsername) {
        System.out.println("로그인한 유저와 선택한 댓글의 작성자가 일치하는지 확인");
        if (!loginUsername.equals(commentUsername)){
            throw new IllegalArgumentException("작성자가 일치하지 않습니다.");
        }
    }
}
