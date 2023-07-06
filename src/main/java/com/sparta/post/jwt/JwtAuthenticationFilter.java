//package com.sparta.post.jwt;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.post.dto.LoginRequestDto;
//import com.sparta.post.security.UserDetailsImpl;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.io.IOException;
//
//@Slf4j(topic = "로그인 및 JWT 생성")
//public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//    private final JwtUtil jwtUtil;
//
//
//    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//        setFilterProcessesUrl("/api/auth/login"); // 설정한 url 로 들어올때 작동함 default 값은 /login
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        log.info("로그인 시도");
//        try {
//            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class); // request 의 request body 부분을 LoginRequestDto 객체로 만들어줌
//            System.out.println(requestDto.getUsername());
//            System.out.println("requestDto.getPassword() = " + requestDto.getPassword());
//            return getAuthenticationManager().authenticate( // manager 가 인증처리하는 메서드
//                    new UsernamePasswordAuthenticationToken( // manager 가 인증에 사용할 토큰을 주는 과정
//                            requestDto.getUsername(),
//                            requestDto.getPassword(),
//                            null
//                    )
//            );
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    // AuthenticationManager 가 인증에 성공하면 자동으로 호출되는 메서드
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        log.info("로그인 성공 및 JWT 생성");
//        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername(); // Authentication(인증된 사용자)의 Principal(사용자를 식별, UserDetails)에서 Username 가져오기
//
//        String token = jwtUtil.createToken(username); // Username 을 포함하느 토큰 만들기
//        // 쿠키에 안담고 토큰만 보내기 // 헤더에 보내기
//        response.setHeader(jwtUtil.AUTHORIZATION_HEADER, token);
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write("로그인 성공");
//        response.setStatus(200);
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//        log.info("로그인 실패");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write("로그인 실패");
//        response.setStatus(401); // 인증이되지 않았다.
//    }
//}