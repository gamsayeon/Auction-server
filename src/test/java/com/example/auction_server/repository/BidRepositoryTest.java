package com.example.auction_server.repository;

import com.example.auction_server.model.Bid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BidRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BidRepositoryTest {
    @Autowired
    private BidRepository bidRepository;
    private Long TEST_BUYER_ID = 500L;
    private Long TEST_PRODUCT_ID = 500L;
    private int BID_COUNT = 2;
    private int TEST_MAX_PRICE = 10000 * BID_COUNT;

    @BeforeEach
    public void generateTestBid() {
        for (int i = 1; i <= BID_COUNT; i++) {
            Bid bid = Bid.builder()
                    .buyerId(TEST_BUYER_ID)
                    .productId(TEST_PRODUCT_ID)
                    .bidTime(LocalDateTime.now())
                    .price(10000 * i)
                    .build();

            bidRepository.save(bid);
        }
    }

    @Test
    @DisplayName("상품의 입찰 최댓값 조회")
    void findMaxPriceByProductId() {
        Integer findMaxBid = bidRepository.findMaxPriceByProductId(TEST_BUYER_ID);

        assertNotNull(findMaxBid);
        assertEquals(TEST_MAX_PRICE, findMaxBid);
    }

    @Test
    @DisplayName("상품의 입찰 갯수 조회")
    void countByProductId() {
        Long findCountBid = bidRepository.countByProductId(TEST_PRODUCT_ID);

        assertNotNull(findCountBid);
        assertEquals(BID_COUNT, findCountBid);
    }
}