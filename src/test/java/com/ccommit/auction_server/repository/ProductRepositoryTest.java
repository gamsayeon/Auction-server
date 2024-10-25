package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.config.TestDatabaseConfig;
import com.ccommit.auction_server.config.TestElasticsearchConfig;
import com.ccommit.auction_server.config.testDataInitializer.TestDataInitializer;
import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestDatabaseConfig.class, TestElasticsearchConfig.class, TestDataInitializer.class})
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private TestDataInitializer testDataInitializer;

    private final int DELETE_SUCCESS = 1;
    private Product savedProduct;
    private User savedUser;

    @BeforeEach
    public void generateTestProduct() {
        //given
        savedUser = testDataInitializer.getSavedUser();
        savedProduct = testDataInitializer.getSavedProduct();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품 식별자로 상품 조회")
    void findByProductId() {
        //when
        Product findProduct = productRepository.findByProductId(savedProduct.getProductId());

        //then
        assertNotNull(findProduct);
        assertEquals(savedProduct.getProductId(), findProduct.getProductId());
    }

    @Test
    @DisplayName("판매자 식별자로 상품 조회")
    void findBySaleId() {
        //when
        List<Product> findProducts = productRepository.findBySaleId(savedUser.getId());

        //then
        assertNotNull(findProducts);
        for (Product findProduct : findProducts) {
            assertEquals(savedUser.getId(), findProduct.getSaleId());
        }
    }

    @Test
    @DisplayName("상품 식별자와 판매자 식별자로 상품 삭제")
    void deleteByProductId() {
        Product deleteProduct = testDataInitializer.createProduct(savedUser.getId(), savedProduct.getCategoryId(),
                "deleteProduct", "deleteExplanation");

        //when
        int deleteProductCount = productRepository.deleteByProductId(deleteProduct.getProductId());

        //then
        assertNull(productRepository.findByProductId(deleteProduct.getProductId()));
        assertEquals(DELETE_SUCCESS, deleteProductCount);
    }

    @Test
    @DisplayName("상품 상태를 기반으로 한 상품 조회")
    void findByProductStatus() {
        //when
        List<Product> findProducts = productRepository.findByProductStatus(ProductStatus.PRODUCT_REGISTRATION, ProductStatus.AUCTION_PROCEEDING);

        //then
        assertNotNull(findProducts);
        for (Product findProduct : findProducts) {
            assertEquals(ProductStatus.PRODUCT_REGISTRATION, findProduct.getProductStatus());
        }
    }

    @Test
    @DisplayName("상품 식별자로 유효한지")
    void existsByProductId() {
        //when
        boolean existsProduct = productRepository.existsByProductId(savedProduct.getProductId());

        //then
        assertTrue(existsProduct);
    }
}