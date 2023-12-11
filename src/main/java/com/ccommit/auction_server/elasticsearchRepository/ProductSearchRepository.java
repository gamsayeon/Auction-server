package com.ccommit.auction_server.elasticsearchRepository;

import com.ccommit.auction_server.model.DocumentProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository {
    Page<DocumentProduct> searchProducts(String productName, Long saleId, Long categoryId, String explanation, Pageable pageable);
}