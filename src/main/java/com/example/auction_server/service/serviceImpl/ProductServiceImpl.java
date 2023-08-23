package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.ProductImageDTO;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.exception.AddException;
import com.example.auction_server.exception.InputSettingException;
import com.example.auction_server.exception.NotMatchingException;
import com.example.auction_server.mapper.ProductImageMapper;
import com.example.auction_server.mapper.ProductMapper;
import com.example.auction_server.model.Product;
import com.example.auction_server.model.ProductImage;
import com.example.auction_server.repository.CategoryRepository;
import com.example.auction_server.repository.ProductImageRepository;
import com.example.auction_server.repository.ProductRepository;
import com.example.auction_server.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final int TIME_COMPARE = 0;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              ProductImageRepository productImageRepository, ProductImageMapper productImageMapper,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productMapper = productMapper;
        this.productImageMapper = productImageMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ProductDTO registerProduct(Long saleUserId, ProductDTO productDTO) {
        Product product = productMapper.convertToEntity(productDTO);

        if (!categoryRepository.existsByCategoryId(productDTO.getCategoryId())) {
            logger.warn("해당 카테고리를 찾지 못했습니다.");
            throw new NotMatchingException("ERR_4003", productDTO.getCategoryId());
        }

        if (productDTO.getStartTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) {  //경매 시작시간을 과거로 입력
            logger.warn("경매 시작시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_10001", productDTO);
        } else if (productDTO.getEndTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) { //경매 마감시간을 과거로 입력
            logger.warn("경매 마감시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_10001", productDTO);
        } else if (productDTO.getStartTime().compareTo(productDTO.getEndTime()) > TIME_COMPARE) {   //경매 마감시간을 경매 시작시간보다 과거로 입력
            logger.warn("경매 시작시간을 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_10001", productDTO);
        } else if (productDTO.getStartPrice() >= productDTO.getHighestPrice()) {    // 경매 시작가가 즉시구매가보다 작거나 같을때
            logger.warn("경매 시작가가 즉시구매가와 같거나 큽니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_10002", productDTO);
        }

        product.setSaleUserId(saleUserId);
        product.setProductRegisterTime(LocalDateTime.now());
        product.setProductStatus(ProductStatus.PRODUCT_REGISTRATION);
        Product resultProduct = productRepository.save(product);

        if (resultProduct != null) {
            ProductDTO resultProductDTO = productMapper.convertToDTO(resultProduct);
            if (productDTO.getImageDTOS() != null) {
                List<ProductImage> resultProductImages = new ArrayList<>();
                List<ProductImage> productImages = productImageMapper.convertToEntity(productDTO);
                for (ProductImage productImage : productImages) {
                    ProductImage resultProductImage = productImageRepository.save(productImage);
                    if (resultProductImage == null) {
                        logger.warn("이미지 등록에 실패 했습니다.");
                        throw new AddException("ERR_1005", productImage);
                    }
                    resultProductImages.add(resultProductImage);
                }
                resultProductDTO.setImageDTOS(productImageMapper.convertToDTO(resultProductImages));
            }
            return resultProductDTO;
        } else {
            logger.warn("상품등록에 실패했습니다. 다시시도해주세요.");
            throw new AddException("ERR_1004", product);
        }
    }

    @Override
    public ProductDTO selectProduct(Long productId) {
        Product product = productRepository.findByProductId(productId);
        if (product != null) {
            ProductDTO resultProductDTO = productMapper.convertToDTO(product);
            List<ProductImage> productImages = productImageRepository.findByProductId(productId);
            if (!productImages.isEmpty()) {
                List<ProductImageDTO> resultProductImageDTOs = productImageMapper.convertToDTO(productImages);

                resultProductDTO.setImageDTOS(resultProductImageDTOs);
            }
            return resultProductDTO;
        } else {
            logger.warn("해당 상품을 찾지 못했습니다.");
            throw new NotMatchingException("ERR_4004", productId);
        }
    }

}
