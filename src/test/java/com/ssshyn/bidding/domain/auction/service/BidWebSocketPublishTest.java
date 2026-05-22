package com.ssshyn.bidding.domain.auction.service;

import com.ssshyn.bidding.domain.auction.dto.AuctionRoomCreateRequest;
import com.ssshyn.bidding.domain.auction.dto.AuctionRoomResponse;
import com.ssshyn.bidding.domain.auction.repository.AuctionRoomRepository;
import com.ssshyn.bidding.domain.auction.repository.BidRepository;
import com.ssshyn.bidding.global.exception.ErrorException;
import com.ssshyn.bidding.global.websocket.MessageType;
import com.ssshyn.bidding.global.websocket.WebSocketMessage;
import com.ssshyn.bidding.global.websocket.WebSocketPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class BidWebSocketPublishTest {

    @Autowired private AuctionRoomService auctionRoomService;
    @Autowired private AuctionRoomRepository auctionRoomRepository;
    @Autowired private BidRepository bidRepository;
    @Autowired private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean private WebSocketPublisher webSocketPublisher;

    private static final String AUCTION_HIGHEST_PREFIX = "auction:highest";
    private Long roomId;

    @BeforeEach
    void setUp() {
        AuctionRoomResponse response = auctionRoomService.create(
                new AuctionRoomCreateRequest("WebSocket 테스트 경매방", "테스트용", 1000L, null, null));
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
    @DisplayName("입찰 성공 시 BID_UPDATED 타입으로 브로드캐스팅된다")
    void bid_success_broadcastsBidUpdated() {
        auctionRoomService.bid(roomId, 2000L);

        verify(webSocketPublisher, times(1))
                .publishToAuction(eq(roomId), argThat(msg ->
                        msg.type() == MessageType.BID_UPDATED));
    }

    @Test
    @DisplayName("입찰 성공 시 브로드캐스팅 메시지에 갱신된 최고가가 담긴다")
    void bid_success_messageContainsUpdatedPrice() {
        long bidPrice = 3000L;
        auctionRoomService.bid(roomId, bidPrice);

        ArgumentCaptor<WebSocketMessage<AuctionRoomResponse>> captor =
                ArgumentCaptor.forClass(WebSocketMessage.class);
        verify(webSocketPublisher).publishToAuction(eq(roomId), captor.capture());

        WebSocketMessage<AuctionRoomResponse> message = captor.getValue();
        assertThat(message.type()).isEqualTo(MessageType.BID_UPDATED);
        assertThat(message.data().currentPrice()).isEqualTo(bidPrice);
        assertThat(message.publishedAt()).isNotNull();
    }

    @Test
    @DisplayName("최고가보다 낮은 금액으로 입찰 실패 시 브로드캐스팅되지 않는다")
    void bid_fail_lowerPrice_doesNotBroadcast() {
        auctionRoomService.bid(roomId, 5000L);
        clearInvocations(webSocketPublisher);

        assertThatThrownBy(() -> auctionRoomService.bid(roomId, 2000L))
                .isInstanceOf(ErrorException.class);

        verifyNoInteractions(webSocketPublisher);
    }

    @Test
    @DisplayName("연속 입찰 시 매 입찰마다 브로드캐스팅된다")
    void bid_multiple_broadcastsEachTime() {
        auctionRoomService.bid(roomId, 2000L);
        auctionRoomService.bid(roomId, 3000L);
        auctionRoomService.bid(roomId, 4000L);

        verify(webSocketPublisher, times(3))
                .publishToAuction(eq(roomId), any());
    }
}
