package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.mapper.BidMapper;
import com.example.auction_server.model.Bid;
import com.example.auction_server.model.Product;
import com.example.auction_server.repository.BidRepository;
import com.example.auction_server.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DisplayName("BidServiceImpl Unit 테스트")
@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {
    @InjectMocks
    private BidServiceImpl bidService;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RabbitMQService rabbitMQService;
    @Mock
    private BidPriceValidServiceImpl bidPriceValidService;
    @Mock
    private BidMapper bidMapper;
    private Product product;
    private Bid bid;
    private BidDTO bidDTO;
    private Long TEST_PRODUCT_ID = 1L;
    private Long TEST_BID_ID = 1L;
    private Long TEST_BUYER_ID = 1L;
    private Long TEST_SALE_ID = 10L;

    @BeforeEach
    private void generatedTestBid() {
        product = Product.builder()
                .productId(TEST_PRODUCT_ID)
                .saleId(TEST_SALE_ID)
                .productName("test product name")
                .productStatus(ProductStatus.AUCTION_PROCEEDING)
                .build();

        bid = Bid.builder()
                .bidId(TEST_BID_ID)
                .buyerId(TEST_BUYER_ID)
                .productId(TEST_PRODUCT_ID)
                .price(10000)
                .build();

        bidDTO = BidDTO.builder()
                .buyerId(TEST_BUYER_ID)
                .productId(TEST_PRODUCT_ID)
                .price(10000)
                .build();
    }

    @Test
    @DisplayName("입찰 등록 테스트")
    void registerBid() {
        //given
        when(productRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(product);
        when(bidMapper.convertToEntity(bidDTO, TEST_PRODUCT_ID, TEST_BUYER_ID)).thenReturn(bid);
        when(bidMapper.convertToDTO(bid)).thenReturn(bidDTO);

        //when
        BidDTO result = bidService.registerBid(TEST_BUYER_ID, TEST_PRODUCT_ID, bidDTO);

        //then
        assertEquals(bidDTO.getPrice(), result.getPrice());
    }

    @Test
    @DisplayName("입찰 이력 구매자 조회 테스트")
    void selectBidByBuyerId() {
        //given
        when(bidRepository.findByBuyerIdAndProductId(TEST_BUYER_ID, TEST_PRODUCT_ID)).thenReturn(List.of(bid));
        when(bidMapper.convertToDTOList(List.of(bid))).thenReturn(List.of(bidDTO));

        //when
        List<BidDTO> result = bidService.selectBidByBuyerId(TEST_BUYER_ID, TEST_PRODUCT_ID);

        //then
        for (BidDTO resultBidDTO : result) {
            assertNotNull(resultBidDTO);
            assertEquals(TEST_BUYER_ID, resultBidDTO.getBuyerId());
        }
    }

    @Test
    @DisplayName("입찰 이력 판매자 조회 테스트")
    void selectBidBySaleId() {
        //given
        when(productRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(product);
        when(bidRepository.findByProductId(TEST_PRODUCT_ID)).thenReturn(List.of(bid));
        when(bidMapper.convertToDTOList(List.of(bid))).thenReturn(List.of(bidDTO));

        //when
        List<BidDTO> result = bidService.selectBidBySaleId(TEST_SALE_ID, TEST_PRODUCT_ID);

        //then
        for (BidDTO resultBidDTO : result) {
            assertNotNull(resultBidDTO);
            assertEquals(TEST_PRODUCT_ID, resultBidDTO.getProductId());
        }
    }
}