package com.ssshyn.bidding.domain.auction.service;

import com.ssshyn.bidding.domain.auction.dto.AuctionRoomCreateRequest;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomResponse;
import com.ssshyn.bidding.domain.auction.entity.AuctionRoom;
import com.ssshyn.bidding.domain.auction.entity.Bid;
import com.ssshyn.bidding.domain.auction.repository.AuctionRoomRepository;
import com.ssshyn.bidding.domain.auction.repository.BidRepository;
import com.ssshyn.bidding.global.exception.ErrorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuctionRoomBidConcurrencyTest {

    @Autowired private AuctionRoomService auctionRoomService;
    @Autowired private AuctionRoomRepository auctionRoomRepository;
    @Autowired private BidRepository bidRepository;
    @Autowired private RedisTemplate<String, Object> redisTemplate;

    private static final String AUCTION_HIGHEST_PREFIX = "auction:highest";
    private static final long STARTING_PRICE = 1000L;
    private static final int TIMEOUT_SECONDS = 30;

    private Long roomId;

    @BeforeEach
    void setUp() {
        AuctionRoomCreateRequest request = new AuctionRoomCreateRequest(
                "동시성 테스트 경매방", "테스트용", STARTING_PRICE, null, null);
        AuctionRoomResponse response = auctionRoomService.create(request);
        roomId = response.id();
    }

    @AfterEach
    void tearDown() {
        auctionRoomRepository.clearAllHighestBids();
        bidRepository.deleteAll();
        auctionRoomRepository.deleteAll();
        redisTemplate.delete(AUCTION_HIGHEST_PREFIX + roomId);
    }

    @Test
    @DisplayName("여러 스레드가 서로 다른 금액으로 동시 입찰해도 최종 상태가 일관된다")
    void concurrentBid_differentPrices_finalStateIsConsistent() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            long bidPrice = STARTING_PRICE + (i * 100L); // 1100 ~ 2000
            executorService.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    auctionRoomService.bid(roomId, bidPrice);
                    successCount.incrementAndGet();
                } catch (ErrorException e) {
                    failCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        boolean completed = done.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        executorService.shutdown();

        assertThat(completed).as("30초 내에 모든 스레드가 완료되어야 한다").isTrue();

        AuctionRoom room = auctionRoomRepository.findByIdWithHighestBid(roomId).orElseThrow();
        List<Bid> bids = bidRepository.findByAuctionRoomIdOrderByBidPriceDesc(roomId);

        System.out.println("=== 동시 입찰 결과 (서로 다른 금액) ===");
        System.out.println("성공: " + successCount.get() + " / 실패: " + failCount.get());
        System.out.println("저장된 입찰 수: " + bids.size());
        System.out.println("최종 최고가: " + room.getCurrentPrice());

        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
        assertThat(bids.size()).isEqualTo(successCount.get());
        assertThat(room.getCurrentPrice()).isEqualTo(room.getHighestBid().getBidPrice());
        assertThat(room.getCurrentPrice()).isEqualTo(bids.get(0).getBidPrice());
    }

    @Test
    @DisplayName("현재 최고가보다 낮은 금액으로 동시 입찰하면 모두 실패한다")
    void concurrentBid_lowerPrice_allFail() throws InterruptedException {
        auctionRoomService.bid(roomId, 5000L);

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            long lowPrice = 2000L + (i * 10L);
            executorService.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    auctionRoomService.bid(roomId, lowPrice);
                } catch (ErrorException e) {
                    failCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        boolean completed = done.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        executorService.shutdown();

        assertThat(completed).as("30초 내에 모든 스레드가 완료되어야 한다").isTrue();

        AuctionRoom room = auctionRoomRepository.findByIdWithHighestBid(roomId).orElseThrow();

        System.out.println("=== 동시 입찰 결과 (낮은 금액) ===");
        System.out.println("실패 수: " + failCount.get() + " / 전체: " + threadCount);
        System.out.println("최종 최고가: " + room.getCurrentPrice());

        assertThat(failCount.get()).isEqualTo(threadCount);
        assertThat(room.getCurrentPrice()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("동일 금액으로 동시 입찰 시 분산락으로 순차 처리된다")
    void concurrentBid_samePrice_processedSequentially() throws InterruptedException {
        int threadCount = 10;
        long samePrice = 3000L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    auctionRoomService.bid(roomId, samePrice);
                    successCount.incrementAndGet();
                } catch (ErrorException e) {
                    failCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        boolean completed = done.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        executorService.shutdown();

        assertThat(completed).as("30초 내에 모든 스레드가 완료되어야 한다").isTrue();

        List<Bid> bids = bidRepository.findByAuctionRoomIdOrderByBidPriceDesc(roomId);
        AuctionRoom room = auctionRoomRepository.findByIdWithHighestBid(roomId).orElseThrow();

        System.out.println("=== 동시 입찰 결과 (동일 금액) ===");
        System.out.println("성공: " + successCount.get() + " / 실패: " + failCount.get());
        System.out.println("저장된 입찰 수: " + bids.size());

        assertThat(successCount.get() + failCount.get()).isEqualTo(threadCount);
        assertThat(room.getCurrentPrice()).isEqualTo(room.getHighestBid().getBidPrice());
        assertThat(bids.size()).isEqualTo(successCount.get());
    }
}
