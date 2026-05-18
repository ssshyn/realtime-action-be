package com.ssshyn.bidding.domain.user.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
