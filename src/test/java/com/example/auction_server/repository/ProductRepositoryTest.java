package com.example.auction_server.repository;

import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    private Long TEST_SALE_ID = 500L;
    private Long TEST_CATEGORY_ID = 500L;
    private int PRODUCT_COUNT = 3;
    private Product SAVED_PRODUCT;
    private int DELETE_SUCCESS = 1;

    @BeforeEach
    public void generateTestProduct() {
        for (int i = 0; i < PRODUCT_COUNT; i++) {
            Product product = new Product();
            product.setSaleId(TEST_SALE_ID);
            product.setProductName("testProductName");
            product.setCategoryId(TEST_CATEGORY_ID);
            product.setExplanation("testExplanation");
            product.setProductRegisterTime(LocalDateTime.now());
            product.setStartPrice(1000);
            product.setStartTime(LocalDateTime.now());
            product.setEndTime(LocalDateTime.now());
            product.setHighestPrice(1000000);
            product.setProductStatus(ProductStatus.PRODUCT_REGISTRATION);
            SAVED_PRODUCT = productRepository.save(product);
        }
    }

    @Test
    @DisplayName("상품 식별자로 상품 조회")
    void findByProductId() {
        Product findProduct = productRepository.findByProductId(SAVED_PRODUCT.getProductId());

        assertNotNull(findProduct);
        assertEquals(SAVED_PRODUCT.getProductId(), findProduct.getProductId());
    }

    @Test
    @DisplayName("판매자 식별자로 상품 조회")
    void findBySaleId() {
        List<Product> findProducts = productRepository.findBySaleId(TEST_SALE_ID);

        assertNotNull(findProducts);
        for (Product findProduct : findProducts) {
            assertEquals(TEST_SALE_ID, findProduct.getSaleId());
        }
    }

    @Test
    @DisplayName("상품 식별자와 판매자 식별자로 상품 삭제")
    void deleteBySaleIdAndProductId() {
        int deleteProduct = productRepository.deleteBySaleIdAndProductId(TEST_SALE_ID, SAVED_PRODUCT.getProductId());

        assertNull(productRepository.findByProductId(SAVED_PRODUCT.getProductId()));
        assertEquals(DELETE_SUCCESS, deleteProduct);
    }

    @Test
    @DisplayName("상품 상태를 기반으로 한 상품 조회")
    void findByProductStatus() {
        List<Product> findProducts = productRepository.findByProductStatus(ProductStatus.PRODUCT_REGISTRATION);

        assertNotNull(findProducts);
        for (Product findProduct : findProducts) {
            assertEquals(ProductStatus.PRODUCT_REGISTRATION, findProduct.getProductStatus());
        }
    }
}