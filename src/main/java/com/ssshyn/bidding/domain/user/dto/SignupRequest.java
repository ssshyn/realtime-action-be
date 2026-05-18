package com.ssshyn.bidding.domain.user.dto;

public record SignupRequest(
        String loginId,
        String password,
        String nickname
) {}
