package com.ssshyn.bidding.global.websocket;

public enum MessageType {
    BID_UPDATED,      // 새 입찰
    STATUS_CHANGED,   // 경매 상태 변경
    AUCTION_ENDED     // 경매 종료
}
