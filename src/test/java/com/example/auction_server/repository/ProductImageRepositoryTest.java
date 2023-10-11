package com.example.auction_server.repository;

import com.example.auction_server.model.ProductImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProductImageRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductImageRepositoryTest {
    @Autowired
    private ProductImageRepository productImageRepository;
    private Long TEST_PRODUCT_ID = 500L;
    private int IMAGE_COUNT = 3;

    @BeforeEach
    public void generateTestProduct() {
        for (int i = 0; i < IMAGE_COUNT; i++) {
            ProductImage productImage = ProductImage.builder()
                    .productId(TEST_PRODUCT_ID)
                    .imagePath("testImagePath" + i)
                    .build();

            productImageRepository.save(productImage);
        }
    }

    @Test
    @DisplayName("상품 식별자로 상품이미지 조회")
    void findByProductId() {
        List<ProductImage> findProductImages = productImageRepository.findByProductId(TEST_PRODUCT_ID);

        assertNotNull(findProductImages);
        assertEquals(IMAGE_COUNT, findProductImages.size());
    }

    @Test
    @DisplayName("상품 식별자로 상품이미지 삭제")
    void deleteAllByProductId() {
        int deleteProductImage = productImageRepository.deleteAllByProductId(TEST_PRODUCT_ID);

        assertEquals(IMAGE_COUNT, deleteProductImage);
        assertTrue(productImageRepository.findByProductId(TEST_PRODUCT_ID).isEmpty());
    }
}