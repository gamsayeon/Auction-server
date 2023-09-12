package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.exception.NotStartBidException;
import com.example.auction_server.mapper.BidMapper;
import com.example.auction_server.model.Bid;
import com.example.auction_server.model.Product;
import com.example.auction_server.repository.ProductRepository;
import com.example.auction_server.service.BidService;
import com.example.auction_server.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidMapper bidMapper;
    private final MessageService messageService;
    private final ProductRepository productRepository;
    private static final Logger logger = LogManager.getLogger(BidServiceImpl.class);

    @Override
    public BidDTO registerBid(Long buyerUserId, Long productId, BidDTO bidDTO) {
        Product product = productRepository.findByProductId(productId);
        if (product.getProductStatus() != ProductStatus.AUCTION_STARTS) {
            logger.warn("경매가 시작되지 않았습니다.");
            throw new NotStartBidException("BID_3");
        } else {
            Bid bid = Bid.builder()
                    .buyerUserId(buyerUserId)
                    .productId(productId)
                    .bidTime(LocalDateTime.now())
                    .price(bidDTO.getPrice())
                    .build();

            messageService.sendMessage(bid);

            return bidMapper.convertToDTO(bid);
        }
    }
}
