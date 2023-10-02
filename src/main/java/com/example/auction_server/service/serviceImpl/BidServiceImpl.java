package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.exception.BidFailedNotStartException;
import com.example.auction_server.mapper.BidMapper;
import com.example.auction_server.model.Bid;
import com.example.auction_server.model.Product;
import com.example.auction_server.repository.ProductRepository;
import com.example.auction_server.service.BidService;
import com.example.auction_server.service.MessageQueueService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidMapper bidMapper;
    private final MessageQueueService messageQueueService;
    private final ProductRepository productRepository;
    private final BidPriceValidServiceImpl bidPriceValidService;
    private static final Logger logger = LogManager.getLogger(BidServiceImpl.class);

    @Override
    public BidDTO registerBid(Long buyerId, Long productId, BidDTO bidDTO) {
        Product product = productRepository.findByProductId(productId);
        bidPriceValidService.validBidPrice(productId, bidDTO.getPrice());

        if (product.getProductStatus() != ProductStatus.AUCTION_PROCEEDING) {
            logger.warn("경매가 시작되지 않았습니다.");
            throw new BidFailedNotStartException("BID_FAILED_NOT_START");
        } else {
            Bid bid = Bid.builder()
                    .buyerId(buyerId)
                    .productId(productId)
                    .bidTime(LocalDateTime.now())
                    .price(bidDTO.getPrice())
                    .build();

            messageQueueService.enqueueMassage(bid);

            return bidMapper.convertToDTO(bid);
        }
    }
}
