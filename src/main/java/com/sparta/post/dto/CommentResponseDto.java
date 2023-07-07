package com.sparta.post.dto;

import com.sparta.post.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private String username;
    private LocalDateTime cratedAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.username = comment.getUser().getUsername();
        this.cratedAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
