package com.ssshyn.bidding.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // websocket 메세지 브로커 구성
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 특정 목적지로 들어오는 메세지를 구독 중인 클라이언트한테 브로드캐스트
        config.enableSimpleBroker("/topic");
        // 특정 목적지로 메시지를 보낼 수 있도록 함
        config.setApplicationDestinationPrefixes("/app");
    }

    // 클라이언트의 엔드포인트 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 엔드포인트 등록하고 SockJS를 사용할 수 있도록 함
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }
}
