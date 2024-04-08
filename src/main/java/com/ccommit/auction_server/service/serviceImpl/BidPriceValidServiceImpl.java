package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.exception.InputMismatchException;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.Category;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.CategoryRepository;
import com.ccommit.auction_server.repository.ProductRepository;
import com.ccommit.auction_server.service.BidPriceValidService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidPriceValidServiceImpl implements BidPriceValidService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BidRepository bidRepository;

    private static final Logger logger = LogManager.getLogger(BidPriceValidServiceImpl.class);

    @Override
    public void validBidPrice(Long productId, int price) {
        Product product = productRepository.findByProductId(productId);
        Optional<Category> category = categoryRepository.findByCategoryId(product.getCategoryId());
        int minBidPriceUnit = category.get().getBidMinPrice();

        Bid bid = bidRepository.findTopByProductIdOrderByPriceDesc(productId);
        if (bid == null) {
            int startPrice = product.getStartPrice();
            if(price > product.getHighestPrice()){
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputMismatchException("BID_INPUT_MISMATCH", price);
            } else if (startPrice == price) {
                return;
            } else if (price - startPrice > minBidPriceUnit) {
                return;
            } else if (startPrice > price) {
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputMismatchException("BID_INPUT_MISMATCH", price);
            } else if (price - startPrice < minBidPriceUnit) {
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputMismatchException("BID_INPUT_MISMATCH", price);
            }
        } else {
            Integer currentPrice = bid.getPrice();
            System.out.println(currentPrice + " 비교값 : " + price);
            if(price > product.getHighestPrice()){
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputMismatchException("BID_INPUT_MISMATCH", price);
            } else if (price - currentPrice > minBidPriceUnit) {
                return;
            } else if (currentPrice >= price) {
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputMismatchException("BID_INPUT_MISMATCH", price);
            } else if (currentPrice + minBidPriceUnit > price) {
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputMismatchException("BID_INPUT_MISMATCH", price);
            }
        }
    }
}