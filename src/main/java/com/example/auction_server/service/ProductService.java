package com.example.auction_server.service;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.SearchProductDTO;
import com.example.auction_server.enums.ProductSortOrder;

import java.util.List;

public interface ProductService {
    ProductDTO registerProduct(Long id, ProductDTO productDTO);

    ProductDTO selectProduct(Long productId);

    List<ProductDTO> selectProductForUser(Long saleId);

    ProductDTO updateProduct(Long saleId, Long productId, ProductDTO productDTO);

    void deleteProduct(Long saleId, Long productId);

    void updateProductStatus();

    SearchProductDTO findByKeyword(String productName, Long saleId, Long categoryId,
                                   String explanation, int page, int pageSize, ProductSortOrder sortOrder);
}
