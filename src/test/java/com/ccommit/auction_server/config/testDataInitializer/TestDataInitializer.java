package com.ccommit.auction_server.config.testDataInitializer;

import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.model.*;
import com.ccommit.auction_server.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.LocalDateTime;

@Getter
@TestConfiguration
public class TestDataInitializer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    private final String TEST_USER_ID = "testUserId";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "testPassword";
    private final String TEST_CATEGORY_NAME = "testCategoryName";
    private final int TEST_BID_MIN_PRICE = 1000;
    private final int TEST_BID_PRICE = 3000;
    private final int IMAGE_COUNT = 3;

    private User savedUser;
    private Category savedCategory;
    private Product savedProduct;
    private Bid savedBid;

    @PostConstruct
    public void initializeTestData() {
        savedUser = createUser(TEST_USER_ID, TEST_PASSWORD, "Test User", TEST_EMAIL);
        savedCategory = createCategory(TEST_CATEGORY_NAME, TEST_BID_MIN_PRICE);
        savedProduct = createProduct(savedUser.getId(), savedCategory.getCategoryId(), "testProductName", "testExplanation");
        createProductImages(savedProduct.getProductId(), IMAGE_COUNT);
        savedBid = createBid(savedProduct.getProductId(), savedUser.getId(), TEST_BID_PRICE);
    }

    private User createUser(String userId, String password, String name, String email) {
        User user = User.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    public Category createCategory(String categoryName, int bidMinPrice) {
        Category category = Category.builder()
                .categoryName(categoryName)
                .bidMinPrice(bidMinPrice)
                .build();
        return categoryRepository.save(category);
    }

    public Product createProduct(Long saleId, Long categoryId, String productName, String explanation) {
        Product product = Product.builder()
                .saleId(saleId)
                .productName(productName)
                .categoryId(categoryId)
                .explanation(explanation)
                .productRegisterTime(LocalDateTime.now())
                .startPrice(1000)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(1)) // 종료 시간 조정
                .highestPrice(1000000)
                .productStatus(ProductStatus.PRODUCT_REGISTRATION)
                .build();
        return productRepository.save(product);
    }

    private void createProductImages(Long productId, int imageCount) {
        for (int i = 0; i < imageCount; i++) {
            ProductImage productImage = ProductImage.builder()
                    .productId(productId)
                    .imagePath("testImagePath" + i)
                    .build();
            productImageRepository.save(productImage);
        }
    }

    private Bid createBid(Long productId, Long buyerId, int price) {
        Bid bid = Bid.builder()
                .productId(productId)
                .buyerId(buyerId)
                .price(price)
                .build();
        return bidRepository.save(bid);
    }
}
