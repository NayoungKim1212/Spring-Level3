package com.sparta.post.dto;

import lombok.Builder;
import lombok.Getter;

// Error message, 상태 코드 담당
@Getter
public class ErrorResponseDto {
    Long status;
    String error;

    @Builder
    public ErrorResponseDto(Long status, String error) {
        this.status = status;
        this.error = error;
    }

}
