package com.ssshyn.bidding.domain.auction.dto;

import com.ssshyn.bidding.domain.auction.entity.AuctionStatus;

import java.time.LocalDateTime;

public record AuctionRoomResponse(
        Long id,
        String title,
        String description,
        Long startingPrice,
        Long currentPrice,
        AuctionStatus status,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
