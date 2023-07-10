package com.sparta.post.service;

import com.sparta.post.dto.ErrorResponseDto;
import com.sparta.post.dto.LoginRequestDto;
import com.sparta.post.dto.UserRequestDto;
import com.sparta.post.entity.User;
import com.sparta.post.entity.UserRoleEnum;
import com.sparta.post.handler.UnauthorizedJwtException;
import com.sparta.post.jwt.JwtUtil;
import com.sparta.post.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    // 관리자 확인
    public final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    public ResponseEntity<ErrorResponseDto> signup(UserRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
      
        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 유저네임이 존재합니다.");
        }
        // 사용자 Role 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.getAdminToken()!=null) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        // 사용자 등록
        User user = new User(username, password, role);
        userRepository.save(user);
        ErrorResponseDto responseDto = ErrorResponseDto.builder()
                .status(201L)
                .error("회원가입 성공")
                .build();
        return  ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<ErrorResponseDto> login(LoginRequestDto requestDto, HttpServletResponse res) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw  new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        jwtUtil.addJwtToHeader(token, res);
        ErrorResponseDto responseDto = ErrorResponseDto.builder()
                .status(200L)
                .error("로그인 성공")
                .build();
        return ResponseEntity.ok(responseDto);
    }
    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(()
                -> new IllegalArgumentException("등록된 사용자가 없습니다."));
    }

    boolean isAdmin(User user) {
        System.out.println("권한 확인중");
        return user.getRole().equals(UserRoleEnum.ADMIN);
    }

    protected User getUserFromJwt(String tokenValue) {
        System.out.println("토큰 인증 및 사용자 정보 반환");
        String decodedToken = jwtUtil.decodingToken(tokenValue);
        String token = jwtUtil.substringToken(decodedToken);

        if (!jwtUtil.validateToken(token)) {
            throw new UnauthorizedJwtException();
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        System.out.println("username = " + username);

        return this.findUser(username);
    }
}
// protected : 같은 패키지 내의 다른 클래스에서 접근이 가능
// 다른 패키지의 자식 클래스에서도 접근이 가능
// 즉, 상속 관계에서 자식 클래스는 부모 클래스의 protected 멤버에 접근 가능