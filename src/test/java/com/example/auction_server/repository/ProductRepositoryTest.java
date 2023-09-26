package com.example.auction_server.repository;

import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    private Long saleId = 500L;
    private Long categoryId = 500L;

    public Product generateTestProduct() {
        Product product = new Product();
        product.setSaleId(saleId);
        product.setProductName("tstProductName");
        product.setCategoryId(categoryId);
        product.setExplanation("testExplanation");
        product.setProductRegisterTime(LocalDateTime.now());
        product.setStartPrice(1000);
        product.setStartTime(LocalDateTime.now());
        product.setEndTime(LocalDateTime.now());
        product.setHighestPrice(1000000);
        return product;
    }

    @Test
    void findByProductId() {
        Product product = this.generateTestProduct();
        Product savedProduct = productRepository.save(product);

        Product findProduct = productRepository.findByProductId(savedProduct.getProductId());

        assertNotNull(findProduct);
        assertEquals(product.getProductRegisterTime(), findProduct.getProductRegisterTime());
    }

    @Test
    void findBySaleId() {
        Product product = this.generateTestProduct();
        productRepository.save(product);

        List<Product> findProducts = productRepository.findBySaleId(saleId);

        assertNotNull(findProducts);
        for (Product findProduct : findProducts) {
            assertEquals(product.getProductRegisterTime(), findProduct.getProductRegisterTime());
        }
    }

    @Test
    void deleteBySaleIdAndProductId() {
        Product product = this.generateTestProduct();
        Product savedProduct = productRepository.save(product);

        int deleteProduct = productRepository.deleteBySaleIdAndProductId(saleId, savedProduct.getProductId());

        assertNotNull(deleteProduct);
        assertEquals(productRepository.findAll().size(), 0);
        assertEquals(deleteProduct, 1);
    }

    @Test
    void findByProductStatus() {
        Product product = this.generateTestProduct();
        productRepository.save(product);
        productRepository.save(product);

        List<Product> findProducts = productRepository.findByProductStatus(ProductStatus.PRODUCT_REGISTRATION);

        assertNotNull(findProducts);
        for (Product findProduct : findProducts) {
            assertEquals(product.getProductRegisterTime(), findProduct.getProductRegisterTime());
        }
    }
}