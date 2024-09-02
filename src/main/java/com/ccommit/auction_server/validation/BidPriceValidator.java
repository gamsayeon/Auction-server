package com.ccommit.auction_server.validation;

import com.ccommit.auction_server.exception.InputMismatchException;
import com.ccommit.auction_server.exception.NotMatchingException;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.BidValidationErrorDetails;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.model.ProductCategoryHighestBid;
import com.ccommit.auction_server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidPriceValidator {
    private final ProductRepository productRepository;
    private static final Logger logger = LogManager.getLogger(BidPriceValidator.class);

    public void validBidPrice(Long productId, int newBidPrice) {
        ProductCategoryHighestBid productCategoryHighestBid = productRepository.findByProductIdWithCategoryAndHighestBid(productId)
                .orElseThrow(() -> new NotMatchingException("PRODUCT_NOT_MATCH_ID","Product not found with id: " + productId));

        Product product = productCategoryHighestBid.getProduct();
        int categoryMinimumBidPrice = productCategoryHighestBid.getCategory().getBidMinPrice();

        int productHighestPrice = productCategoryHighestBid.getProduct().getHighestPrice();
        Bid currentHighestBid = productCategoryHighestBid.getHighestBid() == null ? null : productCategoryHighestBid.getHighestBid();

        if (currentHighestBid == null) {
            int startingPrice = product.getStartPrice();
            if (newBidPrice > productHighestPrice || newBidPrice < startingPrice ||
                    newBidPrice - startingPrice < categoryMinimumBidPrice) {
                logAndThrowException(newBidPrice, startingPrice, categoryMinimumBidPrice);
            }
        } else {
            int currentHighestBidPrice = currentHighestBid.getPrice();

            if (newBidPrice > productHighestPrice || newBidPrice <= currentHighestBidPrice ||
                    newBidPrice - currentHighestBidPrice < categoryMinimumBidPrice) {
                logAndThrowException(newBidPrice, currentHighestBidPrice, categoryMinimumBidPrice);
            }
        }
    }

    private void logAndThrowException(int newBidPrice, int currentHighestBidPrice, int categoryMinimumBidPrice) {
        BidValidationErrorDetails errorDetails = new BidValidationErrorDetails(newBidPrice, currentHighestBidPrice, categoryMinimumBidPrice);
        logger.warn("입찰 가격을 잘못 입력하였습니다. {}", errorDetails);
        throw new InputMismatchException("BID_INPUT_MISMATCH", errorDetails);
    }
}