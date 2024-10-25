package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.config.TestDatabaseConfig;
import com.ccommit.auction_server.config.TestElasticsearchConfig;
import com.ccommit.auction_server.config.testDataInitializer.TestDataInitializer;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BidRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestDatabaseConfig.class, TestElasticsearchConfig.class, TestDataInitializer.class})
class BidRepositoryTest {
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private TestDataInitializer testDataInitializer;

    private Bid savedBid;
    private Product savedProduct;
    private User savedUser;

    @BeforeEach
    void setup() {
        //given
        savedUser = testDataInitializer.getSavedUser();
        savedProduct = testDataInitializer.getSavedProduct();
        savedBid = testDataInitializer.getSavedBid();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test findTopByProductIdOrderByPriceDesc")
    void testFindTopByProductIdOrderByPriceDesc() {
        //when
        Bid foundBid = bidRepository.findTopByProductIdOrderByPriceDesc(savedProduct.getProductId());

        //then
        assertNotNull(foundBid);
        assertEquals(savedBid.getPrice(), foundBid.getPrice());
    }

    @Test
    @DisplayName("Test countByProductId")
    void testCountByProductId() {
        //when
        Long count = bidRepository.countByProductId(savedProduct.getProductId());

        //then
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Test findByBuyerId")
    void testFindByBuyerId() {
        //when
        List<Bid> bids = bidRepository.findByBuyerId(savedUser.getId());

        //then
        assertNotNull(bids);
        assertFalse(bids.isEmpty());
        assertEquals(savedUser.getId(), bids.get(0).getBuyerId());
    }

    @Test
    @DisplayName("Test findByBuyerIdAndProductId")
    void testFindByBuyerIdAndProductId() {
        //when
        List<Bid> bids = bidRepository.findByBuyerIdAndProductId(savedUser.getId(), savedProduct.getProductId());

        //then
        assertNotNull(bids);
        assertFalse(bids.isEmpty());
        assertEquals(savedUser.getId(), bids.get(0).getBuyerId());
        assertEquals(savedProduct.getProductId(), bids.get(0).getProductId());
    }
}
