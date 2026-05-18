package com.ssshyn.bidding.domain.user.dto;

public record LoginRequest(
        String loginId,
        String password
) {}
