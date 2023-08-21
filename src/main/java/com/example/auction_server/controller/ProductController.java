package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.service.serviceImpl.ProductServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final Logger logger = LogManager.getLogger(CategoryController.class);

    private final ProductServiceImpl productService;

    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @PostMapping
    @LoginCheck(types = {LoginCheck.LoginType.USER})
    public ResponseEntity<ProductDTO> registerProduct(Long loginId, @RequestBody @Validated(ProductDTO.register.class) ProductDTO productDTO) {
        logger.debug("상품을 등록합니다.");
        ProductDTO resultProductDTO = productService.registerProduct(loginId, productDTO);
        return ResponseEntity.ok(resultProductDTO);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> selectProduct(@PathVariable("productId") Long productId) {
        logger.debug("상품을 수정합니다.");
        ProductDTO resultProductDTO = productService.selectProduct(productId);
        return ResponseEntity.ok(resultProductDTO);
    }

}
