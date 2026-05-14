package com.ssshyn.bidding.domain.auction.repository;

import com.ssshyn.bidding.domain.auction.entity.AuctionRoom;
import com.ssshyn.bidding.domain.auction.entity.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRoomRepository extends JpaRepository<AuctionRoom, Long> {

    List<AuctionRoom> findByStatus(AuctionStatus status);
}
