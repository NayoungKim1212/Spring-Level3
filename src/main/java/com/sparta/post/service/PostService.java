package com.sparta.post.service;

import com.sparta.post.dto.PostRequestDto;
import com.sparta.post.dto.PostResponseDto;
import com.sparta.post.entity.Post;
import com.sparta.post.entity.User;
import com.sparta.post.jwt.JwtUtil;
import com.sparta.post.repository.PostRepository;
import com.sparta.post.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public ResponseEntity<PostResponseDto> createPost(String tokenValue, PostRequestDto requestDto) {
        String token = authentication(tokenValue);
        String username = getUsernameFromJwt(token); // 보안, 확장성 // jwt이 노출됐을때 위험
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("없는 사용자 입니다."));
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
        Post post = findPost(id);
        String token = authentication(tokenValue);
        String username = getUsernameFromJwt(token);
        usernameMatch(username, post.getUser().getUsername());
        post.update(requestDto);
        return new ResponseEntity<>(new PostResponseDto(post), HttpStatus.OK);
    }

    public ResponseEntity<?> deletePost(String tokenValue, Long id) {
        Post post = findPost(id);
        String token = authentication(tokenValue);
        String username = getUsernameFromJwt(token);
        usernameMatch(username, post.getUser().getUsername());
        postRepository.delete(post);
        return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제 되었습니다.");
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("헤당 게시물은 존재하지 않습니다.") // 500(서버문제), exceptionHandle(심화) -> status. 400(클라이언트문제) bad.request
        );
    }

    private void usernameMatch(String loginUsername, String postUsername) {
        if (!loginUsername.equals(postUsername)){
            throw new IllegalArgumentException("잘못된 사용자입니다.");
        }
    }

    private String authentication(String tokenValue) { // 유효한 토큰인지 확인하고 토큰 반환
        String decodedToken = jwtUtil.decodingToken(tokenValue);
        String token = jwtUtil.substringToken(decodedToken);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token Error");
        }
        return token;
    }
    private String getUsernameFromJwt(String token) { // 토큰에서 사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        return username;
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(()
        -> new IllegalArgumentException("등록된 사용자가 없습니다."));
    }
}


