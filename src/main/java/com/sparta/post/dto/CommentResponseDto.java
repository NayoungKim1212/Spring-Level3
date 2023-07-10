package com.sparta.post.dto;

import com.sparta.post.entity.Comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Builder
@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private String username;
    private LocalDateTime cratedAt;
    private LocalDateTime modifiedAt;


}
