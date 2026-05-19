package com.ssshyn.bidding.domain.auction.mapper;

import com.ssshyn.bidding.domain.auction.entity.AuctionRoom;
import com.ssshyn.bidding.domain.auction.entity.Bid;

public class BidFactory {
    private BidFactory() {}

    public static Bid from(AuctionRoom auctionRoom, Long bidPrice) {
        return Bid.builder()
                .auctionRoom(auctionRoom)
                .bidPrice(bidPrice)
                .build();
    }
}
