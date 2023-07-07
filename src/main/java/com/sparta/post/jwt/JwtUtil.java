package com.sparta.post.jwt;

import com.sparta.post.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Getter
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성(JWT 생성)
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX + // "bearer "을 앞에 붙여줌
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID) // -> Username 대신 user_id 가 들어가는게 더 좋을듯(정보 노출 줄일려고) 비슷한가?
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한 claim(key, value)
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간 ((현재시간) + 만료시간)
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public void addJwtToHeader(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // 앞에 붙여둔 "bearer "을 인코딩하면 공백이 +로 인코딩됨, + -> %20으로 바꿔줌 // 공백은 %20으로 바꿔주는게 URL 인코딩의 약속이다.

            res.setHeader(AUTHORIZATION_HEADER, token);

        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }

    public String decodingToken(String tokenValue) {
        try {
            return URLDecoder.decode(tokenValue,"utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // JWT 토큰 substring(Cookie에 들어있던 JWT 토큰을 Substring)
    public String substringToken(String tokenValue) {
        if(StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            // 공백인지 null인지 확인(공백과 null이 아니여야함)
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    // 토큰 검증(JWT 검증)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // token의 위변조 검증
            return true; // setSigningKey(key) : 암호화할 때 사용한 키
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error(("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다."));
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기(JWT에서 사용자 정보 가져오기)
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
