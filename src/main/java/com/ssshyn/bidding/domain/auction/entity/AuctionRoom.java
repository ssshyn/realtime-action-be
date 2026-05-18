package com.ssshyn.bidding.domain.auction.entity;

import com.ssshyn.bidding.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auction_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Long startingPrice;

    @Column(nullable = false)
    private Long currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @OneToMany(mappedBy = "auctionRoom", fetch = FetchType.LAZY)
    private List<Bid> bids = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bid_id")
    private Bid highestBid;

    @Builder
    public AuctionRoom(String title, String description, Long startingPrice, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.status = AuctionStatus.SCHEDULED;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void update(String title, String description, Long startingPrice, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void changeStatus(AuctionStatus status) {
        this.status = status;
    }

    public void updateHighestBid(Bid bid) {
        this.highestBid = bid;
        this.currentPrice = bid.getBidPrice();
    }
}
