package com.sparta.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    private String username;
    private String contents;
    private String title;
    private String password;
}
