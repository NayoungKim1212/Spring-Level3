package com.sparta.post.service;

import com.sparta.post.dto.LoginRequestDto;
import com.sparta.post.dto.UserRequestDto;
import com.sparta.post.entity.User;
import com.sparta.post.jwt.JwtUtil;
import com.sparta.post.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public ResponseEntity<String> signup(UserRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());
      
        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            return new ResponseEntity<>("중복된 사용자가 존재합니다.", HttpStatus.CONFLICT);
        }

        // 사용자 등록
        User user = new User(username, password);
        userRepository.save(user);
        return new ResponseEntity<>("회원 가입 성공", HttpStatus.CREATED);
    }

    public ResponseEntity<String> login(LoginRequestDto requestDto, HttpServletResponse res) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (!checkUsername.isPresent()) {
            return new ResponseEntity<>("등록된 사용자가 아닙니다.", HttpStatus.NOT_FOUND);
        }

        User user = checkUsername.get();

        if(!passwordEncoder.matches(password, user.getPassword())){
            return new ResponseEntity<>("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.createToken(user.getUsername());
        jwtUtil.addJwtToHeader(token, res);
        return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
    }
}
