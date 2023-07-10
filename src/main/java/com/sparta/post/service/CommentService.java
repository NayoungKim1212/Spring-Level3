package com.sparta.post.service;

import com.sparta.post.dto.CommentRequestDto;
import com.sparta.post.dto.CommentResponseDto;
import com.sparta.post.dto.ErrorResponseDto;
import com.sparta.post.entity.Comment;
import com.sparta.post.entity.Post;
import com.sparta.post.entity.User;
import com.sparta.post.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    public ResponseEntity<CommentResponseDto> createComment(String token, Long postId, CommentRequestDto requestDto) {
        User currentuser = userService.getUserFromJwt(token); // 토큰을 사용하여 사용자 정보 가져오기
        Post currentpost = postService.findPost(postId); // 해당 게시물 찾기

        Comment comment = Comment.builder()
                .comment(requestDto.getComment())
                .post(currentpost)
                .user(currentuser)
                .build();

        commentRepository.save(comment);

        CommentResponseDto responseDto = CommentResponseDto.builder()
                .comment(comment.getComment())
                .id(comment.getId())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();

        return ResponseEntity.status(200).body(responseDto);
    }

    @Transactional
    public ResponseEntity<CommentResponseDto> updateComment(String token, Long id, Long postId, CommentRequestDto requestDto) {
        User currentuser = userService.getUserFromJwt(token); // 토큰 인증 및 사용자 정보 반환
        Comment comment = findComment(id, postId);


        if (!userService.isAdmin(currentuser)) { // 관리자 권한인지 확인
            if (!comment.getUser().getUsername().equals(currentuser.getUsername())) {
                throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
            }
        }

        comment.update(requestDto);
        return new ResponseEntity<>(CommentResponseDto.builder()
                .comment(comment.getComment())
                .id(comment.getId())
                .username(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build(), HttpStatus.OK);
    }

    public ResponseEntity<ErrorResponseDto> deleteComment(String token, Long id, Long postId) {
        User currentuser = userService.getUserFromJwt(token);
        Comment comment = findComment(id, postId);

        if (!userService.isAdmin(currentuser)) { // 관리자 권한인지 확인
            if (!comment.getUser().getUsername().equals(currentuser.getUsername())) {
                throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
            }
        }

        commentRepository.delete(comment);
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .status(201L)
                .error("삭제성공")
                .build();
        return ResponseEntity.ok(errorResponseDto);
    }



    private Comment findComment(Long id, Long postId) {
        System.out.println("댓글 찾기");
        return commentRepository.findByPostIdAndId(postId, id).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 댓글 입니다."));
    }

}
