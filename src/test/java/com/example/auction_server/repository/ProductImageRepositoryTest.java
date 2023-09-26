package com.example.auction_server.repository;

import com.example.auction_server.model.ProductImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductImageRepositoryTest {
    @Autowired
    private ProductImageRepository productImageRepository;

    private Long productId = 500L;

    @BeforeEach
    public void generateTestProduct() {
        ProductImage productImage = new ProductImage();
        productImage.setProductId(productId);
        productImage.setImagePath("testImagePath");
        productImageRepository.save(productImage);

        productImage = new ProductImage();
        productImage.setProductId(productId);
        productImage.setImagePath("testImagePath1");
        productImageRepository.save(productImage);

        productImage = new ProductImage();
        productImage.setProductId(productId);
        productImage.setImagePath("testImagePath2");
        productImageRepository.save(productImage);
    }

    @Test
    void findByProductId() {
        List<ProductImage> findProductImages = productImageRepository.findByProductId(productId);
        assertNotNull(findProductImages);
        assertEquals(findProductImages.size(), 3);
    }

    @Test
    void deleteAllByProductId() {
        int deleteProductImage = productImageRepository.deleteAllByProductId(productId);
        assertNotNull(deleteProductImage);
        assertEquals(deleteProductImage, 3);
        assertEquals(productImageRepository.findAll().size(), 0);
    }
}