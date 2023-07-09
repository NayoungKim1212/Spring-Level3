package com.sparta.post.service;

import com.sparta.post.dto.*;
import com.sparta.post.entity.Comment;
import com.sparta.post.entity.Post;
import com.sparta.post.entity.User;
import com.sparta.post.jwt.JwtUtil;
import com.sparta.post.repository.PostRepository;
import com.sparta.post.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;

    public ResponseEntity<PostResponseDto> createPost(String token, PostRequestDto requestDto) {
        User user = userService.getUserFromJwt(token);// 토큰 인증 및 사용자 정보 반환
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

    public PostWithCommentResponseDto getPost(Long id) {
        Post post = findPost(id);
        List<CommentResponseDto> commentResponseDtoList =
                post.getCommentList()
                        .stream()
                        .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                        .map(CommentResponseDto::new)
                        .toList();
        return new PostWithCommentResponseDto(post, commentResponseDtoList);
    }

    @Transactional
    public ResponseEntity<PostResponseDto> updatePost(String token, Long id, PostRequestDto requestDto) {
        User user = userService.getUserFromJwt(token);
        Post post = findPost(id);

        if (!userService.isAdmin(user)) { // 관리자 권한인지 확인
            if (!user.getUsername().equals(post.getUser().getUsername())) {
                throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
            }
        }
        post.update(requestDto);

        return new ResponseEntity<>(new PostResponseDto(post), HttpStatus.OK);
    }


    public ResponseEntity<ErrorResponseDto> deletePost(String token, Long id) {
        User user = userService.getUserFromJwt(token);
        Post post = findPost(id);

        if (!userService.isAdmin(user)) { // 관리자가 아닌 경우
            if (!user.getUsername().equals(post.getUser().getUsername())) { // 작성자 미동일
                throw new IllegalArgumentException("작성자만 삭제할 수 있습니다");
            }
        }
        postRepository.delete(post);

        ErrorResponseDto responseDto = ErrorResponseDto.builder()
                .status(200L)
                .error("삭제 성공")
                .build();
        return ResponseEntity.ok(responseDto);

    }


    protected Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("헤당 게시물은 존재하지 않습니다.")
        );
    }

//    private void usernameMatch(String loginUsername, String postUsername) {
//        if (!loginUsername.equals(postUsername)) {
//            throw new IllegalArgumentException("잘못된 사용자입니다.");
//        }
//    }
}

