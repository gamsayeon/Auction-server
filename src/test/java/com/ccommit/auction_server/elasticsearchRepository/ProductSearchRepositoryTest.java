package com.ccommit.auction_server.elasticsearchRepository;

import com.ccommit.auction_server.config.TestDatabaseConfig;
import com.ccommit.auction_server.enums.ProductSortOrder;
import com.ccommit.auction_server.model.elk.DocumentProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@DisplayName("ProductSearchRepository Unit 테스트")
@Import({TestDatabaseConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductSearchRepositoryTest {
    @Mock
    private ProductSearchRepository productSearchRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private DocumentProduct createDocumentProduct(Long productId, Long saleId, String productName, Long categoryId, String explanation) {
        DocumentProduct documentProduct = new DocumentProduct();
        documentProduct.setProductId(productId);
        documentProduct.setSaleId(saleId);
        documentProduct.setProductName(productName);
        documentProduct.setCategoryId(categoryId);
        documentProduct.setExplanation(explanation);
        return documentProduct;
    }

    @Test
    @DisplayName("최신 등록순을 상품 검색")
    void searchProducts() {
        // given
        String productName = "testProduct";
        Long saleId = 1L;
        Long categoryId = 2L;
        String explanation = "sample explanation";
        Pageable pageable = Pageable.ofSize(10);  // Pageable 객체를 생성합니다.
        ProductSortOrder sortOrder = ProductSortOrder.NEWEST_FIRST;

        List<DocumentProduct> productList = new ArrayList<>();
        DocumentProduct product = new DocumentProduct();
        product.setProductId(1L);
        productList.add(product);

        Page<DocumentProduct> expectedPage = new PageImpl<>(productList);

        when(productSearchRepository.searchProducts(productName, saleId, categoryId, explanation, pageable, sortOrder))
                .thenReturn(expectedPage);

        // when
        Page<DocumentProduct> actualPage = productSearchRepository.searchProducts(productName, saleId, categoryId, explanation, pageable, sortOrder);

        // then
        assertNotNull(actualPage);
        assertEquals(expectedPage.getTotalElements(), actualPage.getTotalElements());
        assertEquals(expectedPage.getContent().size(), actualPage.getContent().size());
        assertEquals(expectedPage.getContent().get(0).getProductName(), actualPage.getContent().get(0).getProductName());
        verify(productSearchRepository).searchProducts(productName, saleId, categoryId, explanation, pageable, sortOrder);
    }
}