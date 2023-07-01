package com.sparta.post.service;

import com.sparta.post.dto.PostRequestDto;
import com.sparta.post.dto.PostResponseDto;
import com.sparta.post.entity.Post;
import com.sparta.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostResponseDto createPost(PostRequestDto requestDto) {
        String username = getUsername();
        Post post = new Post(requestDto, username);
        postRepository.save(post);
        return new PostResponseDto(post);
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
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
        Post post = findPost(id);
        String username = getUsername();
        if (username.equals(post.getUsername())) {
            post.update(requestDto);
            return new PostResponseDto(post);
        } else {
            throw new IllegalArgumentException("잘못된 사용자입니다.");
        }
    }

    public void deletePost(Long id) {
        Post post = findPost(id);
        String username = getUsername();
        if (username.equals(post.getUsername())) {
            postRepository.delete(post);
        } else {
            throw new IllegalArgumentException("잘못된 사용자입니다.");
        }
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("헤당 게시물은 존재하지 않습니다.")
        );
    }

    public String getUsername() {
        System.out.println("이름 받아오는중");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // SecurityContextHolder내부에 SecurityContext내부에 있는 Authentication을 받아옴
        System.out.println("authentication.getName() = " + authentication.getName());
        if (authentication != null) {
            return authentication.getName(); // Authentication에서 이름을 받아온다.
        }
        return null;
    }
}

//    private boolean isTokenValid(String token, Post post) {
//
//        String username = getUsernameFromJwt(token);
//
//        if (username.equals(post.getUsername())) {
//            return true;
//        }
//        return false;
//    }
//
//
//    private String getUsernameFromJwt(String tokenValue) {
//
//        String token = jwtUtil.substringToken(tokenValue);
//
//        if (!jwtUtil.validateToken(tokenValue)) {
//
//            throw new IllegalArgumentException("Token Error");
//        }
//
//        // 토큰에서 사용자 정보 가져오기
//        Claims info = jwtUtil.getUserInfoFromToken(token);
//        String username = info.getSubject();
//        return username;
//    }


