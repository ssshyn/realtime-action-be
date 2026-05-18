package com.ssshyn.bidding.domain.auction.repository;

import com.ssshyn.bidding.domain.auction.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByAuctionRoomIdOrderByBidPriceDesc(Long auctionRoomId);
}
