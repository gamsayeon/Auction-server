package com.example.auction_server.repository;

import com.example.auction_server.model.Bid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class BidRepositoryTest {
    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Long buyerId = 500L;
    private Long productId = 500L;

    @BeforeEach
    public void generateTestBid() {
        Bid bid = new Bid();
        bid.setBuyerId(buyerId);
        bid.setProductId(productId);
        bid.setBidTime(LocalDateTime.now());
        bid.setPrice(10000);
        bidRepository.save(bid);

        bid = new Bid();
        bid.setBuyerId(buyerId);
        bid.setProductId(productId);
        bid.setBidTime(LocalDateTime.now());
        bid.setPrice(20000);
        bidRepository.save(bid);
    }

    @Test
    void findMaxPriceByProductId() {
        Integer findMaxBid = bidRepository.findMaxPriceByProductId(buyerId);
        assertNotNull(findMaxBid);
        assertEquals(20000, findMaxBid);
    }

    @Test
    void countByProductId() {
        Long findCountBid = bidRepository.countByProductId(productId);
        assertNotNull(findCountBid);
        assertEquals(2, findCountBid);
    }
}