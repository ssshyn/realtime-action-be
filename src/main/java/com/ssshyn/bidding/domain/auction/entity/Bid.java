package com.ssshyn.bidding.domain.auction.entity;

import com.ssshyn.bidding.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bid")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_room_id", nullable = false)
    private AuctionRoom auctionRoom;

    @Column(nullable = false)
    private Long bidPrice;

    @Builder
    public Bid(AuctionRoom auctionRoom, Long bidPrice) {
        this.auctionRoom = auctionRoom;
        this.bidPrice = bidPrice;
    }
}
