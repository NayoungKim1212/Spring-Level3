package com.sparta.post.dto;

import com.sparta.post.entity.Comment;

import java.time.LocalDateTime;

public class CommentResponseDto {
    private Long id;
    private String username;
    private String contents;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto (Comment comment) {
        this.id = comment.getId();
        this.username = comment.getUser().getUsername();
        this.contents = comment.getComment();
        this.createAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();

    }
}
