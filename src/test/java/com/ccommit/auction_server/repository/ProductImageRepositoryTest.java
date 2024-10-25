package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.config.TestDatabaseConfig;
import com.ccommit.auction_server.config.TestElasticsearchConfig;
import com.ccommit.auction_server.config.testDataInitializer.TestDataInitializer;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.model.ProductImage;
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

import static org.junit.Assert.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductImageRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestDatabaseConfig.class, TestElasticsearchConfig.class, TestDataInitializer.class})
class ProductImageRepositoryTest {
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private TestDataInitializer testDataInitializer;

    private Product savedProduct;

    @BeforeEach
    public void generateTestProductImage() {
        //given
        savedProduct = testDataInitializer.getSavedProduct();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품 식별자로 상품이미지 조회")
    void findByProductId() {
        //when
        List<ProductImage> findProductImages = productImageRepository.findByProductId(savedProduct.getProductId());

        //then
        assertNotNull(findProductImages);
        assertEquals(testDataInitializer.getIMAGE_COUNT(), findProductImages.size());
    }

    @Test
    @DisplayName("상품 식별자로 상품이미지 삭제")
    void deleteAllByProductId() {
        //when
        int deleteProductImage = productImageRepository.deleteAllByProductId(savedProduct.getProductId());

        //then
        assertEquals(testDataInitializer.getIMAGE_COUNT(), deleteProductImage);
        assertTrue(productImageRepository.findByProductId(savedProduct.getProductId()).isEmpty());
    }
}