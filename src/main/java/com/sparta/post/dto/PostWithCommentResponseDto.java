package com.sparta.post.dto;

import com.sparta.post.entity.Comment;
import com.sparta.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostWithCommentResponseDto {
    private Long id;
    private String username;
    private String contents;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private String title;
    private List<CommentResponseDto> commentList;

    public PostWithCommentResponseDto(Post post) {
        this.id = post.getId();
        this.username = post.getUser().getUsername();
        this.contents = post.getContents();
        this.createAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.title = post.getTitle();
        this.commentList = post.getCommentList().stream().map(comment ->
                        CommentResponseDto.builder()
                                .comment(comment.getComment())
                                .id(comment.getId())
                                .username(comment.getUser().getUsername())
                                .cratedAt(comment.getCreatedAt())
                                .modifiedAt(comment.getModifiedAt())
                                .build()
        ).toList();
    }
}
