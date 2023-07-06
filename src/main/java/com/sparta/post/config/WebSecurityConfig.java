//package com.sparta.post.config;
//
//import com.sparta.post.jwt.JwtAuthenticationFilter;
//import com.sparta.post.jwt.JwtAuthorizationFilter;
//import com.sparta.post.jwt.JwtUtil;
//import com.sparta.post.security.UserDetailsServiceImpl;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity // Spring Security 지원을 가능하게 함
//public class WebSecurityConfig {
//
//    private final JwtUtil jwtUtil;
//    private final UserDetailsServiceImpl userDetailsService;
//    private final AuthenticationConfiguration authenticationConfiguration;
//
//    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
//        this.jwtUtil = jwtUtil;
//        this.userDetailsService = userDetailsService;
//        this.authenticationConfiguration = authenticationConfiguration;
//    }
//
//    @Bean // 수동등록
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception { // 인증필터
//        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
//        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
//        return filter;
//    }
//
//    @Bean
//    public JwtAuthorizationFilter jwtAuthorizationFilter() {
//        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        // CSRF 설정
//        http.csrf((csrf) -> csrf.disable());
//
//        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
//        http.sessionManagement((sessionManagement) ->
//                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        );
//
//        http.authorizeHttpRequests((authorizeHttpRequests) ->
//                authorizeHttpRequests
//                        .requestMatchers("/api/auth/**").permitAll() // '/api/auth/'로 시작하는 요청 모두 접근 허가
//                        .requestMatchers(HttpMethod.GET,"/api/post/**").permitAll() // GET 메서드 요청이면서 api/post/로 시작하는 요청은 모두 접근허가
//                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
//        );
//
//        // 필터 관리 // 만든 필터의 언제 사용할건지 위치 지정
//        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class); // 인가 -> 인증 ->
//        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}