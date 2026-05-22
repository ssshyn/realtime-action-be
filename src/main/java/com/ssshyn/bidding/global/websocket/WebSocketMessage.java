package com.ssshyn.bidding.global.websocket;

import java.time.LocalDateTime;

public record WebSocketMessage<T>(
        MessageType type,
        T data,
        LocalDateTime publishedAt
) {
    public static <T> WebSocketMessage<T> of(MessageType type, T data) {
        return new WebSocketMessage<>(type, data, LocalDateTime.now());
    }
}
