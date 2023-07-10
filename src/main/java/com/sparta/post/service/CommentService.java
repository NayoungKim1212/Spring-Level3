package com.sparta.post.service;

import com.sparta.post.dto.CommentRequestDto;
import com.sparta.post.dto.CommentResponseDto;
import com.sparta.post.entity.Comment;
import com.sparta.post.entity.Post;
import com.sparta.post.entity.User;
import com.sparta.post.entity.UserRoleEnum;
import com.sparta.post.handler.UnauthorizedJwtException;
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
                .cratedAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();

        return ResponseEntity.status(200).body(responseDto);
    }

    @Transactional
    public ResponseEntity<CommentResponseDto> updateComment(String token, Long postId, Long id, CommentRequestDto requestDto) {
        User currentuser = userService.getUserFromJwt(token); // 토큰 인증 및 사용자 정보 반환
        Comment comment = findComment(id, postId);


        if (!userService.isAdmin(currentuser)) { // 관리자 권한인지 확인
            if (!comment.getUser().getUsername().equals(currentuser.getUsername())) {
                throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
            }
        }
//        String username = info.getSubject(); // jwt토큰에서 사용자의 식별자 값을 추출하여 username 변수에 할당
//        usernameMatch(username, comment.getUser().getUsername());
        comment.update(requestDto);
        return new ResponseEntity<>(new CommentResponseDto(comment), HttpStatus.OK);
    }

    public ResponseEntity<String> deleteComment(String token, Long id, Long postId) {
        User currentuser = userService.getUserFromJwt(token);
        Comment comment = findComment(id, postId);

        if (!userService.isAdmin(currentuser)) { // 관리자 권한인지 확인
            if (!comment.getUser().getUsername().equals(currentuser.getUsername())) {
                throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
            }
        }
//        String username = info.getSubject();
//        usernameMatch(username, comment.getUser().getUsername());
        commentRepository.delete(comment);
        return new ResponseEntity<>("댓글이 삭제 되었습니다.", HttpStatus.OK);
    }

//    private Claims authentication(String tokenValue) {
//        System.out.println("토큰 인증 및 사용자 정보 반환");
//        String decodedToken = jwtUtil.decodingToken(tokenValue);
//        String token = jwtUtil.substringToken(decodedToken);
//        if (!jwtUtil.validateToken(token)) {
//            throw new UnauthorizedJwtException();
//        }
//        return jwtUtil.getUserInfoFromToken(token);
//    }

//    private boolean hasRoleAdmin(Claims info) {
//        System.out.println("권한 확인중");
//        if (info.get(jwtUtil.AUTHORIZATION_KEY).equals(UserRoleEnum.ADMIN.name())) { // UserRoleEnum.ADMIN.name = "ADMIN"
//            return true;
//        }
//        return false;
//    }

    private Comment findComment(Long id, Long postId) {
        System.out.println("댓글 찾기");
        return commentRepository.findByPostIdAndId(id, postId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 댓글 입니다."));
    }

    private void usernameMatch(String loginUsername, String commentUsername) {
        System.out.println("로그인한 유저와 선택한 댓글의 작성자가 일치하는지 확인");
        if (!loginUsername.equals(commentUsername)) {
            throw new IllegalArgumentException("작성자가 일치하지 않습니다.");
        }
    }
}
