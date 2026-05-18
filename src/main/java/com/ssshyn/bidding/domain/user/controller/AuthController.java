package com.ssshyn.bidding.domain.user.controller;

import com.ssshyn.bidding.domain.user.dto.AuthResponse;
import com.ssshyn.bidding.domain.user.dto.LoginRequest;
import com.ssshyn.bidding.domain.user.dto.SignupRequest;
import com.ssshyn.bidding.domain.user.service.AuthService;
import com.ssshyn.bidding.global.exception.ErrorCode;
import com.ssshyn.bidding.global.exception.ErrorException;
import com.ssshyn.bidding.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "회원가입/로그인/로그아웃/회원탈퇴 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequest request) {
        authService.signup(request);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        authService.logout(resolveToken(request));
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/withdraw")
    public void withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.withdraw(userDetails.getLoginId());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        throw new ErrorException(ErrorCode.NO_TOKEN);
    }
}
