package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.ProductImageDTO;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.mapper.ProductMapper;
import com.example.auction_server.model.Product;
import com.example.auction_server.model.ProductImage;
import com.example.auction_server.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@DisplayName("ProductServiceImpl Unit 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    private Product convertedBeforeResponseProduct;
    private ProductDTO requestProductDTO;
    private List<ProductImageDTO> requestProductImageDTO;
    private ProductImage convertedBeforeResponseProductImage;

    @BeforeEach
    public void generateTestProduct() {
        convertedBeforeResponseProduct = Product.builder()
                .productId(1L)
                .saleId(1L)
                .productName("test Product Name")
                .categoryId(1L)
                .explanation("test Explanation")
                .productRegisterTime(LocalDateTime.now())
                .startPrice(1000)
                .startTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES))
                .endTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS))
                .highestPrice(100000)
                .productStatus(ProductStatus.PRODUCT_REGISTRATION)
                .build();

        requestProductImageDTO = List.of(ProductImageDTO.builder()
                .imageId(1L)
                .productId(1L)
                .imagePath("/testPath")
                .build());

        convertedBeforeResponseProductImage = ProductImage.builder()
                .imageId(1L)
                .productId(1L)
                .imagePath("/testPath")
                .build();

        requestProductDTO = ProductDTO.builder()
                .productId(1L)
                .saleId(1L)
                .productName("test Product Name")
                .categoryId(1L)
                .explanation("test Explanation")
                .productRegisterTime(LocalDateTime.now())
                .startPrice(1000)
                .startTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES))
                .endTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS))
                .highestPrice(100000)
                .productStatus(ProductStatus.PRODUCT_REGISTRATION)
                .imageDTOS(requestProductImageDTO)
                .build();
    }

    @Test
    void registerProduct() {
        //given
//        when(productMapper.convertToEntity(requestProductDTO)).thenReturn(convertedBeforeResponseProduct);

    }

    @Test
    void registerProductImage() {
    }

    @Test
    void validatorProduct() {
    }

    @Test
    void selectProduct() {
    }

    @Test
    void selectProductForUser() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void updateProductStatus() {
    }

    @Test
    void deleteProduct() {
    }

    @Test
    void deleteProductImage() {
    }

    @Test
    void findByKeyword() {
    }

    @Test
    void sortProducts() {
    }

    @Test
    void currentBid() {
    }
}