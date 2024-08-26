package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.BidDTO;
import com.ccommit.auction_server.elasticsearchRepository.BidSelectRepository;
import com.ccommit.auction_server.exception.NotMatchingException;
import com.ccommit.auction_server.mapper.BidMapper;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.elk.DocumentBid;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.ProductRepository;
import com.ccommit.auction_server.service.BidService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidMapper bidMapper;
    private final ProductRepository productRepository;
    private final BidSelectRepository bidSelectRepository;
    private final BidRepository bidRepository;
    private static final Logger logger = LogManager.getLogger(BidServiceImpl.class);

    @Override
    public List<BidDTO> selectBidByUserId(Long buyerId, Long productId) {
        List<DocumentBid> bids;
        if (productId == null) {
            bids = bidSelectRepository.findByBuyerId(buyerId);
        } else if (!productRepository.existsByProductId(productId)) {
            logger.warn("해당하는 상품을 찾지 못했습니다.");
            throw new NotMatchingException("PRODUCT_NOT_MATCH_ID", productId);
        } else {
            bids = bidSelectRepository.findByBuyerIdAndProductId(buyerId, productId);
        }

        return bidMapper.convertToSelectBidDTOList(bids);
    }

    @Override
    public List<BidDTO> selectBidByProduct(Long productId) {
        if (!productRepository.existsByProductId(productId)) {
            logger.warn("해당하는 상품을 찾지 못했습니다.");
            throw new NotMatchingException("PRODUCT_NOT_MATCH_ID", productId);
        }
        List<DocumentBid> bids = bidSelectRepository.findByProductIdOrderByPriceDesc(productId);

        return bidMapper.convertToSelectBidDTOList(bids);
    }

    @Override
    public Bid saveBid(Bid bid) {
        return bidRepository.save(bid);
    }
}