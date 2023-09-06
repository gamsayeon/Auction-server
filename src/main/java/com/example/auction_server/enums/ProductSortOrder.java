package com.example.auction_server.enums;

public enum ProductSortOrder {
    BIDDER_COUNT_DESC,          // 입찰자가 많은 순
    HIGHEST_PRICE_HIGH_TO_LOW,  // 즉시 가격 높은 순
    HIGHEST_PRICE_LOW_TO_HIGH,  // 즉시 가격 낮은 순
    PRICE_HIGH_TO_LOW,          // 가격 높은 순
    PRICE_LOW_TO_HIGH,          // 가격 낮은 순
    NEWEST_FIRST,               // 등록일 최신 순
    OLDEST_FIRST                // 등록일 과거 순
}
