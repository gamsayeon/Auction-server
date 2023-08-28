package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.ProductImageDTO;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.exception.*;
import com.example.auction_server.mapper.ProductMapper;
import com.example.auction_server.model.Product;
import com.example.auction_server.repository.CategoryRepository;
import com.example.auction_server.repository.ProductRepository;
import com.example.auction_server.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final int TIME_COMPARE = 0;
    private final int DELETE_FAIL = 0;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductImageServiceImpl productImageService;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              CategoryRepository categoryRepository, ProductImageServiceImpl productImageService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.productImageService = productImageService;
    }

    @Override
    @Transactional
    public ProductDTO registerProduct(Long saleUserId, ProductDTO productDTO) {
        this.validatorProduct(productDTO);

        Product product = productMapper.convertToEntity(productDTO);

        product.setSaleUserId(saleUserId);
        product.setProductRegisterTime(LocalDateTime.now());
        product.setProductStatus(ProductStatus.PRODUCT_REGISTRATION);
        Product resultProduct = productRepository.save(product);

        if (resultProduct != null) {
            ProductDTO resultProductDTO = productMapper.convertToDTO(resultProduct);
            if (productDTO.getImageDTOS() != null) {
                List<ProductImageDTO> resultProductImages =
                        productImageService.registerProductImage(productDTO, resultProduct.getProductId());
                resultProductDTO.setImageDTOS(resultProductImages);
            }
            return resultProductDTO;
        } else {
            logger.warn("상품등록에 실패했습니다. 다시시도해주세요.");
            throw new AddException("ERR_1004", product);
        }
    }

    public void validatorProduct(ProductDTO productDTO) {
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
    }


    @Override
    public ProductDTO selectProduct(Long productId) {
        Product product = productRepository.findByProductId(productId);
        if (product != null) {
            ProductDTO resultProductDTO = productMapper.convertToDTO(product);
            resultProductDTO.setImageDTOS(productImageService.selectProductImage(productId));
            return resultProductDTO;
        } else {
            logger.warn("해당 상품을 찾지 못했습니다.");
            throw new NotMatchingException("ERR_4004", productId);
        }
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long saleUserId, Long productId, ProductDTO productDTO) {
        Product resultProduct = productRepository.findByProductId(productId);

        if (resultProduct.getProductStatus() == ProductStatus.AUCTION_STARTS) {
            logger.warn("해당 상품은 경매가 시작되여 수정이 불가능합니다.");
            throw new UpdateException("ERR_5004", resultProduct.getProductStatus());
        }

        if (resultProduct.getSaleUserId() == saleUserId) {
            this.validatorProduct(productDTO);

            Product product = productMapper.convertToEntity(productDTO);
            product.setProductId(resultProduct.getProductId());
            product.setSaleUserId(resultProduct.getSaleUserId());
            product.setProductRegisterTime(resultProduct.getProductRegisterTime());
            resultProduct = productRepository.save(resultProduct);
            if (resultProduct == null) {
                logger.warn("상품을 수정하지 못했습니다.");
                throw new UpdateException("ERR_5005", "retry");
            }

            ProductDTO resultProductDTO = productMapper.convertToDTO(resultProduct);
            productImageService.deleteProductImage(productId);
            List<ProductImageDTO> resultProductImageDTOs = productImageService.registerProductImage(productDTO, productId);

            resultProductDTO.setImageDTOS(resultProductImageDTOs);

            return resultProductDTO;
        } else {
            logger.warn("권한이 없어 해당 상품을 수정하지 못합니다.");
            throw new UserAccessDeniedException("ERR_9002", saleUserId);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long saleUserId, Long productId) {
        Product resultProduct = productRepository.findByProductId(productId);
        if (saleUserId == resultProduct.getSaleUserId()) {
            productImageService.deleteProductImage(productId);
            int resultDelete = productRepository.deleteBySaleUserIdAndProductId(saleUserId, productId);
            if (resultDelete == DELETE_FAIL) {
                logger.warn("상품을 삭제 하지 못했습니다.");
                throw new DeleteException("ERR_11000", productId);
            }
        } else {
            logger.warn("권한이 없습니다.");
            throw new UserAccessDeniedException("ERR_9001", saleUserId);
        }
    }
}
