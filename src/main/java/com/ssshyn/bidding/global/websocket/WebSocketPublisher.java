package com.ssshyn.bidding.global.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    private static final String AUCTION_TOPIC = "/topic/auction/";

    /**
     * 특정 경매방 구독자 전체에게 메시지 발행
     * 구독 경로: /topic/auction/{roomId}
     */
    public <T> void publishToAuction(Long roomId, WebSocketMessage<T> message) {
        messagingTemplate.convertAndSend(AUCTION_TOPIC + roomId, message);
    }

    /**
     * 임의 destination으로 메시지 발행
     */
    public <T> void publish(String destination, WebSocketMessage<T> message) {
        messagingTemplate.convertAndSend(destination, message);
    }
}
