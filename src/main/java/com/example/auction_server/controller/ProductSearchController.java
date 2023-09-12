package com.example.auction_server.controller;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.enums.ProductSortOrder;
import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.service.serviceImpl.ProductServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products/search")
@Log4j2
public class ProductSearchController {
    private final ProductServiceImpl productService;

    private final Logger logger = LogManager.getLogger(ProductSearchController.class);

    public ProductSearchController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductDTO>>> searchProduct(@RequestParam(value = "productName", required = false) String productName,
                                                                         @RequestParam(value = "saleUserId", required = false) Long saleUserId,
                                                                         @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                                         @RequestParam(value = "explanation", required = false) String explanation,
                                                                         @RequestParam(value = "sortOrder", defaultValue = "BIDDER_COUNT_DESC", required = false) ProductSortOrder sortOrder,
                                                                         HttpServletRequest request) {
        logger.debug("상품을 검색합니다.");
        CommonResponse<List<ProductDTO>> response = new CommonResponse<>("SUCCESS", "상품검색에 성공했습니다.",
                request.getRequestURI(), productService.findByKeyword(productName, saleUserId, categoryId, explanation, sortOrder));
        return ResponseEntity.ok(response);
    }
}
