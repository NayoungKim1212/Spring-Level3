package com.sparta.post.entity;

public enum UserRoleEnum { // enum: 상수의 집합, 명명된 값의 집합(제한된 선택지를 나타냄)
    // 제한된 선책지: USER, ADMIN
    // 생각해보니 지금 과제에서는 굳이 필요가 없다.
    USER(Authority.USER),
    ADMIN(Authority.ADMIN);
    private final String authority;
    UserRoleEnum(String authority) {
        this.authority = authority;
    }
    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
