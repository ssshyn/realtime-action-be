package com.ssshyn.bidding.domain.auction.mapper;

import com.ssshyn.bidding.domain.auction.dto.AuctionRoomResponse;
import com.ssshyn.bidding.domain.auction.entity.AuctionRoom;

import java.util.List;

public class AuctionRoomMapper {

    private AuctionRoomMapper() {}

    public static AuctionRoomResponse toResponse(AuctionRoom room) {
        return new AuctionRoomResponse(
                room.getId(),
                room.getTitle(),
                room.getDescription(),
                room.getStartingPrice(),
                room.getCurrentPrice(),
                room.getStatus(),
                room.getStartAt(),
                room.getEndAt(),
                room.getCreatedAt(),
                room.getCreatedBy(),
                room.getUpdatedAt(),
                room.getUpdatedBy()
        );
    }

    public static List<AuctionRoomResponse> toResponseList(List<AuctionRoom> rooms) {
        return rooms.stream()
                .map(AuctionRoomMapper::toResponse)
                .toList();
    }
}
