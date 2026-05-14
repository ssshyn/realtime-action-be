package com.ssshyn.bidding.domain.auction.mapper;

import com.ssshyn.bidding.domain.auction.dto.AuctionRoomCreateRequest;
import com.ssshyn.bidding.domain.auction.entity.AuctionRoom;

public class AuctionRoomFactory {

    private AuctionRoomFactory() {}

    public static AuctionRoom from(AuctionRoomCreateRequest request) {
        return AuctionRoom.builder()
                .title(request.title())
                .description(request.description())
                .startingPrice(request.startingPrice())
                .startAt(request.startAt())
                .endAt(request.endAt())
                .build();
    }
}
