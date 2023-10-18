package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.exception.BidFailedNotStartException;
import com.example.auction_server.exception.NotMatchingException;
import com.example.auction_server.exception.NullDataException;
import com.example.auction_server.exception.UserAccessDeniedException;
import com.example.auction_server.mapper.BidMapper;
import com.example.auction_server.model.Bid;
import com.example.auction_server.model.Product;
import com.example.auction_server.repository.BidRepository;
import com.example.auction_server.repository.ProductRepository;
import com.example.auction_server.service.BidService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidMapper bidMapper;
    private final RabbitMQService rabbitMQService;
    private final BidRepository bidRepository;
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
            Bid bid = bidMapper.convertToEntity(bidDTO, productId, buyerId);

            rabbitMQService.enqueueMassage(bid);

            return bidMapper.convertToDTO(bid);
        }
    }

    @Override
    public List<BidDTO> selectBidByBuyerId(Long buyerId, Long productId) {
        List<Bid> bids;
        if (productId == null) {
            bids = bidRepository.findByBuyerId(buyerId);
        } else {
            bids = bidRepository.findByBuyerIdAndProductId(buyerId, productId);
        }
        if (bids.isEmpty()) {
            if (!productRepository.existsByProductId(productId)) {
                logger.warn("해당하는 상품을 찾지 못했습니다.");
                throw new NotMatchingException("PRODUCT_NOT_MATCH_ID", productId);
            }
            logger.warn("아직 경매 이력이 없습니다.");
            throw new NullDataException("BID_NULL_DATA");
        }

        return bidMapper.convertToDTOList(bids);
    }

    @Override
    public List<BidDTO> selectBidBySaleId(Long saleId, Long productId) {
        Product product = productRepository.findByProductId(productId);
        if (product == null) {
            logger.warn("해당하는 상품을 찾지 못했습니다.");
            throw new NotMatchingException("PRODUCT_NOT_MATCH_ID", productId);
        }
        if (product.getSaleId() == saleId) {
            List<Bid> bids = bidRepository.findByProductId(productId);
            if (bids.isEmpty()) {
                logger.warn("아직 경매 이력이 없습니다.");
                throw new NullDataException("BID_NULL_DATA");
            } else return bidMapper.convertToDTOList(bids);
        } else {
            logger.warn("해당상품의 경매 이력의 조회 권한이 없습니다.");
            throw new UserAccessDeniedException("COMMON_ACCESS_DENIED", productId);
        }
    }
}