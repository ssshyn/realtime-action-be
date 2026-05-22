package com.ssshyn.bidding.domain.auction.repository;

import com.ssshyn.bidding.domain.auction.entity.AuctionRoom;
import com.ssshyn.bidding.domain.auction.entity.AuctionStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuctionRoomRepository extends JpaRepository<AuctionRoom, Long> {

    List<AuctionRoom> findByStatus(AuctionStatus status);

    @Query("SELECT r FROM AuctionRoom r LEFT JOIN FETCH r.highestBid WHERE r.id = :id")
    Optional<AuctionRoom> findByIdWithHighestBid(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE AuctionRoom r SET r.highestBid = null")
    void clearAllHighestBids();
}
