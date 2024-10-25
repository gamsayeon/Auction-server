package com.ccommit.auction_server.elasticsearchRepository;

import com.ccommit.auction_server.config.TestDatabaseConfig;
import com.ccommit.auction_server.model.elk.DocumentBid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import({TestDatabaseConfig.class})
@DisplayName("BidSelectRepository Unit 테스트")
@ExtendWith(MockitoExtension.class)
public class BidSelectRepositoryTest {
    @Mock
    private BidSelectRepository bidSelectRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private DocumentBid createDocumentBid(String id, Long bidId, Long buyerId, Long productId, LocalDateTime bidTime, int price) {
        DocumentBid documentBid = new DocumentBid();
        documentBid.setId(id);
        documentBid.setBidId(bidId);
        documentBid.setBuyerId(buyerId);
        documentBid.setProductId(productId);
        documentBid.setBidTime(bidTime);
        documentBid.setPrice(price);
        return documentBid;
    }

    @Test
    void findByBuyerId() {
        Long buyerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // given
        List<DocumentBid> expectedBids = Arrays.asList(
                createDocumentBid("1", 1L, buyerId, 100L, now, 5000),
                createDocumentBid("2", 2L, buyerId, 101L, now.plusHours(1), 7500)
        );

        // when
        when(bidSelectRepository.findByBuyerId(buyerId)).thenReturn(expectedBids);
        List<DocumentBid> actualBids = bidSelectRepository.findByBuyerId(buyerId);

        // then
        assertEquals(expectedBids.size(), actualBids.size());
        assertEquals(expectedBids, actualBids);
        verify(bidSelectRepository).findByBuyerId(buyerId);
    }

    @Test
    void findByBuyerIdAndProductId() {
        Long buyerId = 1L;
        Long productId = 100L;
        LocalDateTime now = LocalDateTime.now();

        // given
        List<DocumentBid> expectedBids = Arrays.asList(
                createDocumentBid("1", 1L, buyerId, productId, now, 5000)
        );

        // when
        when(bidSelectRepository.findByBuyerIdAndProductId(buyerId, productId)).thenReturn(expectedBids);
        List<DocumentBid> actualBids = bidSelectRepository.findByBuyerIdAndProductId(buyerId, productId);

        // then
        assertEquals(expectedBids.size(), actualBids.size());
        assertEquals(expectedBids, actualBids);
        verify(bidSelectRepository).findByBuyerIdAndProductId(buyerId, productId);
    }

    @Test
    void findByProductIdOrderByPriceDesc() {
        Long productId = 100L;
        LocalDateTime now = LocalDateTime.now();

        // given
        List<DocumentBid> expectedBids = Arrays.asList(
                createDocumentBid("1", 1L, 1L, productId, now, 10000),
                createDocumentBid("2", 2L, 2L, productId, now.plusHours(1), 7500),
                createDocumentBid("3", 3L, 3L, productId, now.plusHours(2), 5000)
        );

        // when
        when(bidSelectRepository.findByProductIdOrderByPriceDesc(productId)).thenReturn(expectedBids);
        List<DocumentBid> actualBids = bidSelectRepository.findByProductIdOrderByPriceDesc(productId);

        // then
        assertEquals(expectedBids.size(), actualBids.size());
        assertEquals(expectedBids, actualBids);
        verify(bidSelectRepository).findByProductIdOrderByPriceDesc(productId);

        // 가격 순서 확인
        for (int i = 1; i < actualBids.size(); i++) {
            assertTrue(actualBids.get(i - 1).getPrice() >= actualBids.get(i).getPrice());
        }
    }
}
