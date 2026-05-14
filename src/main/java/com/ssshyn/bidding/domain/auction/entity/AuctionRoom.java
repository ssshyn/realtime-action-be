package com.ssshyn.bidding.domain.auction.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auction_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionRoom {

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public AuctionRoom(String title, String description, Long startingPrice, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.status = AuctionStatus.SCHEDULED;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String description, Long startingPrice, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.startAt = startAt;
        this.endAt = endAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(AuctionStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}
