package com.sparta.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "소문자와 숫자를 포함한 4~10자 이내로 작성해주세요")
    @NotBlank
    private String username;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[`~!@#$%^&*()])[a-zA-Z0-9`~!@#$%^&*()]{8,15}$", message = "대소문자와 숫자, 특수문자를 포함한 8~15자 이내로 작성해주세요")
    @NotBlank
    private String password;
    private boolean admin = false;
    private String adminToken = "";
}
