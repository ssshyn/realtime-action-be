package com.ssshyn.bidding.domain.user.service;

import com.ssshyn.bidding.domain.user.dto.AuthResponse;
import com.ssshyn.bidding.domain.user.dto.LoginRequest;
import com.ssshyn.bidding.domain.user.dto.SignupRequest;
import com.ssshyn.bidding.domain.user.entity.Role;
import com.ssshyn.bidding.domain.user.entity.User;
import com.ssshyn.bidding.domain.user.repository.UserRepository;
import com.ssshyn.bidding.global.exception.ErrorCode;
import com.ssshyn.bidding.global.exception.ErrorException;
import com.ssshyn.bidding.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new ErrorException(ErrorCode.LOGIN_ID_DUPLICATED);
        }
        userRepository.save(User.builder()
                .loginId(request.loginId())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(Role.ROLE_USER)
                .build());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new ErrorException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ErrorException(ErrorCode.LOGIN_FAILED);
        }
        if (!user.isActive()) {
            throw new ErrorException(ErrorCode.USER_ALREADY_DEACTIVATED);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getLoginId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getLoginId());

        return new AuthResponse(accessToken, refreshToken);
    }

    public void logout(String accessToken) {
        String loginId = jwtTokenProvider.getLoginId(accessToken);
        jwtTokenProvider.blacklistAccessToken(accessToken);
        jwtTokenProvider.deleteRefreshToken(loginId);
    }

    @Transactional
    public void withdraw(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ErrorException(ErrorCode.USER_NOT_FOUND));
        user.withdraw();
    }
}
