package com.example.auction_server.repository;

import com.example.auction_server.model.Product;
import com.example.auction_server.repository.repositoryImpl.ProductSearchRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductSearchRepositoryTest {
    @Autowired
    private ProductSearchRepositoryImpl productSearchRepository;
    @Autowired
    private ProductRepository productRepository;

    private Long saleId = 500L;
    private Long categoryId = 500L;
    private String searchTestProductName = "1";

    @BeforeEach
    public void generateTestProduct() {
        Product product = new Product();
        product.setSaleId(saleId);
        product.setProductName("testProductName1");
        product.setCategoryId(categoryId);
        product.setExplanation("testExplanation");
        product.setProductRegisterTime(LocalDateTime.now());
        product.setStartPrice(1000);
        product.setStartTime(LocalDateTime.now());
        product.setEndTime(LocalDateTime.now());
        product.setHighestPrice(1000000);
        productRepository.save(product);

        product = new Product();
        product.setSaleId(saleId);
        product.setProductName("1testProductName");
        product.setCategoryId(categoryId);
        product.setExplanation("testExplanation");
        product.setProductRegisterTime(LocalDateTime.now());
        product.setStartPrice(1000);
        product.setStartTime(LocalDateTime.now());
        product.setEndTime(LocalDateTime.now());
        product.setHighestPrice(1000000);
        productRepository.save(product);

        product = new Product();
        product.setSaleId(saleId);
        product.setProductName("testProductName");
        product.setCategoryId(categoryId);
        product.setExplanation("testExplanation");
        product.setProductRegisterTime(LocalDateTime.now());
        product.setStartPrice(1000);
        product.setStartTime(LocalDateTime.now());
        product.setEndTime(LocalDateTime.now());
        product.setHighestPrice(1000000);
        productRepository.save(product);
    }

    @Test
    void searchProducts() {
        List<Product> searchProducts = productSearchRepository.searchProducts(searchTestProductName, null, null,
                null, 1, 10);
        assertNotNull(searchProducts);
        assertEquals(searchProducts.size(), 2);
    }

}