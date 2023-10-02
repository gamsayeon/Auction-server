package com.example.auction_server.repository;

import com.example.auction_server.model.Product;
import com.example.auction_server.repository.repositoryImpl.ProductSearchRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductSearchRepositoryTest {
    @Autowired
    private ProductSearchRepositoryImpl productSearchRepository;
    @Autowired
    private ProductRepository productRepository;
    private Long TEST_SALE_ID = 500L;
    private Long TEST_CATEGORY_ID = 500L;
    private int PRODUCT_COUNT = 5;
    private String TEST_EXPLANATION = "testExplanation";

    @BeforeEach
    public void generateTestProduct() {
        for (int i = 0; i < PRODUCT_COUNT; i++) {
            Product product = new Product();
            product.setSaleId(TEST_SALE_ID + i);
            product.setProductName("testProductName" + i);
            product.setCategoryId(TEST_CATEGORY_ID);
            product.setExplanation(TEST_EXPLANATION);
            product.setProductRegisterTime(LocalDateTime.now());
            product.setStartPrice(1000);
            product.setStartTime(LocalDateTime.now());
            product.setEndTime(LocalDateTime.now());
            product.setHighestPrice(1000000);
            productRepository.save(product);
        }
    }

    @Test
    @DisplayName("다양한 상품 검색 테스트")
    void searchProducts() {
        //productName search
        List<Product> searchProducts = productSearchRepository.searchProducts("1", null, null,
                null, 1, 10);

        assertNotNull(searchProducts);
        assertEquals(1, searchProducts.size());
        for (int i = 0; i < searchProducts.size(); i++) {
            assertTrue(searchProducts.get(i).getProductName().contains("1"));
        }

        //saleId search
        searchProducts = productSearchRepository.searchProducts(null, TEST_SALE_ID, null,
                null, 1, 10);

        assertNotNull(searchProducts);
        assertEquals(1, searchProducts.size());
        for (int i = 0; i < searchProducts.size(); i++) {
            assertEquals(TEST_SALE_ID, searchProducts.get(i).getSaleId());
        }

        //categoryId search
        searchProducts = productSearchRepository.searchProducts(null, null, TEST_CATEGORY_ID,
                null, 1, 10);

        assertNotNull(searchProducts);
        assertEquals(PRODUCT_COUNT, searchProducts.size());
        for (int i = 0; i < searchProducts.size(); i++) {
            assertEquals(TEST_CATEGORY_ID, searchProducts.get(i).getCategoryId());
        }

        //explanation search
        searchProducts = productSearchRepository.searchProducts(null, null, null,
                TEST_EXPLANATION, 1, 10);

        assertNotNull(searchProducts);
        assertEquals(PRODUCT_COUNT, searchProducts.size());
        for (int i = 0; i < searchProducts.size(); i++) {
            assertEquals(TEST_EXPLANATION, searchProducts.get(i).getExplanation());
        }

    }

}