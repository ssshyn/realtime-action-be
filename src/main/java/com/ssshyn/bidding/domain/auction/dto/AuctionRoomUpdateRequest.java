package com.ssshyn.bidding.domain.auction.dto;

import java.time.LocalDateTime;

public record AuctionRoomUpdateRequest(
        String title,
        String description,
        Long startingPrice,
        LocalDateTime startAt,
        LocalDateTime endAt
) {}
