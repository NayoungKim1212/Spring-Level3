package com.sparta.post.service;

import com.sparta.post.dto.PostRequestDto;
import com.sparta.post.dto.PostResponseDto;
import com.sparta.post.entity.Post;
import com.sparta.post.entity.User;
import com.sparta.post.entity.UserRoleEnum;
import com.sparta.post.jwt.JwtUtil;
import com.sparta.post.repository.PostRepository;
import com.sparta.post.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public ResponseEntity<PostResponseDto> createPost(String tokenValue, PostRequestDto requestDto) {
        Claims info = authentication(tokenValue); // 토큰 인증 및 사용자 정보 반환
        String username = info.getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        Post post = new Post(requestDto, user);
        postRepository.save(post);
        return new ResponseEntity<>(new PostResponseDto(post), HttpStatus.OK);
    }

    public List<PostResponseDto> getPosts() {
        return postRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostResponseDto::new)
                .toList();
    }

    public PostResponseDto getPost(Long id) {
        return new PostResponseDto(findPost(id));
    }

    @Transactional
    public ResponseEntity<PostResponseDto> updatePost(String tokenValue, Long id, PostRequestDto requestDto) {
        Claims info = authentication(tokenValue);
        Post post = findPost(id);
        if(hasRoleAdmin(info)){ // 관리자 권한인지 확인
            post.update(requestDto);
            return new ResponseEntity<>(new PostResponseDto(post), HttpStatus.OK);
        }
        String username = info.getSubject();
        usernameMatch(username, post.getUser().getUsername());
        post.update(requestDto);
        return new ResponseEntity<>(new PostResponseDto(post), HttpStatus.OK);
    }

    public ResponseEntity<?> deletePost(String tokenValue, Long id) {
        Claims info = authentication(tokenValue);
        Post post = findPost(id);
        if(hasRoleAdmin(info)){
            postRepository.delete(post);
            return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제 되었습니다.");
        }
        String username = info.getSubject();
        usernameMatch(username, post.getUser().getUsername());
        postRepository.delete(post);
        return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제 되었습니다.");
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
        if (info.get(jwtUtil.AUTHORIZATION_KEY).equals(UserRoleEnum.ADMIN.name())) {
            return true;
        }
        return false;
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("헤당 게시물은 존재하지 않습니다.")
        );
    }

    private void usernameMatch(String loginUsername, String postUsername) {
        if (!loginUsername.equals(postUsername)){
            throw new IllegalArgumentException("잘못된 사용자입니다.");
        }
    }
}


