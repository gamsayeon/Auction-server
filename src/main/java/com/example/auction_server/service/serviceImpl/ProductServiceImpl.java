package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.ProductImageDTO;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.exception.*;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final int TIME_COMPARE = 0;
    private final int DELETE_FAIL = 0;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;

    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper,
                              CategoryRepository categoryRepository, ProductImageMapper productImageMapper,
                              ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.productImageMapper = productImageMapper;
        this.productImageRepository = productImageRepository;
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
            try {
                if (productDTO.getImageDTOS() != null) {
                    List<ProductImageDTO> resultProductImages =
                            this.registerProductImage(productDTO, resultProduct.getProductId());
                    resultProductDTO.setImageDTOS(resultProductImages);
                }
            } catch (AddException e) {
                logger.warn("이미지 등록에 실패 했습니다.");
            }
            logger.info("상품을 정상적으로 등록했습니다.");
            return resultProductDTO;
        } else {
            logger.warn("상품등록에 실패했습니다. 다시시도해주세요.");
            throw new AddException("ERR_PRODUCT_1", product);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ProductImageDTO> registerProductImage(ProductDTO productDTO, Long productId) {
        List<ProductImage> resultProductImages = new ArrayList<>();
        List<ProductImage> productImages = productImageMapper.convertToEntity(productDTO);
        for (ProductImage productImage : productImages) {
            productImage.setProductId(productId);
            ProductImage resultProductImage = productImageRepository.save(productImage);
            if (resultProductImage == null) {
                logger.warn("이미지 등록에 실패 했습니다.");
                throw new AddException("ERR_PRODUCT_IMAGE_1", productImage);
            } else resultProductImages.add(resultProductImage);
        }
        return productImageMapper.convertToDTO(resultProductImages);
    }

    public void validatorProduct(ProductDTO productDTO) {
        if (!categoryRepository.existsByCategoryId(productDTO.getCategoryId())) {
            logger.warn("해당 카테고리를 찾지 못했습니다.");
            throw new NotMatchingException("ERR_CATEGORY_4", productDTO.getCategoryId());
        }

        if (productDTO.getStartTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) {  //경매 시작시간을 과거로 입력
            logger.warn("경매 시작시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_PRODUCT_7", productDTO);
        } else if (productDTO.getEndTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) { //경매 마감시간을 과거로 입력
            logger.warn("경매 마감시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_PRODUCT_7", productDTO);
        } else if (productDTO.getStartTime().compareTo(productDTO.getEndTime()) > TIME_COMPARE) {   //경매 마감시간을 경매 시작시간보다 과거로 입력
            logger.warn("경매 시작시간을 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_PRODUCT_7", productDTO);
        } else if (productDTO.getStartPrice() >= productDTO.getHighestPrice()) {    // 경매 시작가가 즉시구매가보다 작거나 같을때
            logger.warn("경매 시작가가 즉시구매가와 같거나 큽니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_PRODUCT_8", productDTO);
        }
    }

    @Override
    public ProductDTO selectProduct(Long productId) {
        Product product = productRepository.findByProductId(productId);
        if (product != null) {
            switch (product.getProductStatus()) {
                case PRODUCT_REGISTRATION:
                case AUCTION_STARTS:
                    ProductDTO resultProductDTO = productMapper.convertToDTO(product);
                    List<ProductImage> productImages = productImageRepository.findByProductId(productId);
                    if (!productImages.isEmpty()) {
                        List<ProductImageDTO> resultProductImageDTOs = productImageMapper.convertToDTO(productImages);
                        resultProductDTO.setImageDTOS(resultProductImageDTOs);
                        logger.info("이미지를 정상적으로 조회했습니다.");
                    }
                    logger.info("상품을 정상적으로 조회했습니다.");
                    return resultProductDTO;
                default:
                    logger.warn("");
                    throw new LoginRequiredException("");
            }
        } else {
            logger.warn("해당 상품을 찾지 못했습니다.");
            throw new NotMatchingException("ERR_CATEGORY_5", productId);
        }
    }

    @Override
    public List<ProductDTO> selectProductForUser(Long saleUserId) {
        List<Product> products = productRepository.findBySaleUserId(saleUserId);
        if (!products.isEmpty()) {
            List<ProductDTO> resultProductDTOs = new ArrayList<>();
            for (Product product : products) {
                ProductDTO resultProductDTO = productMapper.convertToDTO(product);
                List<ProductImage> productImages = productImageRepository.findByProductId(product.getProductId());
                if (!productImages.isEmpty()) {
                    List<ProductImageDTO> resultProductImageDTOs = productImageMapper.convertToDTO(productImages);
                    resultProductDTO.setImageDTOS(resultProductImageDTOs);
                    logger.info("이미지를 정상적으로 조회했습니다.");
                }
                resultProductDTOs.add(resultProductDTO);
            }
            logger.info("상품을 정상적으로 조회했습니다.");
            return resultProductDTOs;
        } else {
            logger.warn("해당 상품을 찾지 못했습니다.");
            throw new NotMatchingException("ERR_CATEGORY_5", saleUserId);
        }
    }


    @Override
    @Transactional
    public ProductDTO updateProduct(Long saleUserId, Long productId, ProductDTO productDTO) {
        Product resultProduct = productRepository.findByProductId(productId);

        if (resultProduct.getProductStatus() != ProductStatus.PRODUCT_REGISTRATION) {
            logger.warn("해당 상품은 경매가 시작되여 수정이 불가능합니다.");
            throw new UpdateException("ERR_PRODUCT_2", resultProduct.getProductStatus());
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
                throw new UpdateException("ERR_PRODUCT_3", "retry");
            }

            ProductDTO resultProductDTO = productMapper.convertToDTO(resultProduct);
            this.deleteProductImage(productId);
            List<ProductImageDTO> resultProductImageDTOs = this.registerProductImage(productDTO, productId);

            resultProductDTO.setImageDTOS(resultProductImageDTOs);
            logger.info("상품을 수정했습니다.");
            return resultProductDTO;
        } else {
            logger.warn("권한이 없어 해당 상품을 수정하지 못합니다.");
            throw new UserAccessDeniedException("ERR_PRODUCT_6", saleUserId);
        }
    }

    @Override
    @Transactional
    public void updateProductStatus() {
        List<Product> resultProducts = productRepository.findByProductStatus(ProductStatus.PRODUCT_REGISTRATION);
        for (Product product : resultProducts) {
            if (product.getStartTime().compareTo(LocalDateTime.now()) < 0) {  //경매 시작시간이 현재시간과 비교해서 과거인지 확인
                product.setProductStatus(ProductStatus.AUCTION_STARTS);
                Product resultProduct = productRepository.save(product);
                if (resultProduct == null) {
                    logger.warn("경매 시작 상태로 수정하지 못했습니다.");
                    throw new UpdateException("ERR_PRODUCT_5", product.getProductId());
                } else {
                    logger.info("경매 상태를 AUCTION_STARTS 로 성공적으로 바꿨습니다.");
                }
            }
            if (product.getEndTime().compareTo(LocalDateTime.now()) < 0) {   //경매 마감시간이 현재시간과 비교해서 과거인지 확인
                product.setProductStatus(ProductStatus.AUCTION_END);
                Product resultProduct = productRepository.save(product);
                if (resultProduct == null) {
                    logger.warn("경매 마감 상태로 수정하지 못했습니다.");
                    throw new UpdateException("ERR_PRODUCT_5", product.getProductId());
                } else {
                    logger.info("경매 상태를 AUCTION_END 로 성공적으로 바꿨습니다.");
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long saleUserId, Long productId) {
        Product resultProduct = productRepository.findByProductId(productId);

        if (resultProduct.getProductStatus() != ProductStatus.PRODUCT_REGISTRATION) {
            logger.warn("해당 상품은 경매가 시작되여 삭제가 불가능합니다.");
            throw new UpdateException("ERR_PRODUCT_2", resultProduct.getProductStatus());
        } else if (saleUserId == resultProduct.getSaleUserId()) {
            this.deleteProductImage(productId);
            int resultDelete = productRepository.deleteBySaleUserIdAndProductId(saleUserId, productId);
            if (resultDelete == DELETE_FAIL) {
                logger.warn("상품을 삭제 하지 못했습니다.");
                throw new DeleteException("ERR_COMMON_4", productId);
            } else {
                logger.info("상품을 삭제 했습니다.");
            }
        } else {
            logger.warn("권한이 없습니다.");
            throw new UserAccessDeniedException("ERR_COMMON_3", saleUserId);
        }
    }

    public void deleteProductImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        if (!productImages.isEmpty()) {
            if (productImageRepository.deleteAllByProductId(productId) != productImages.size()) {
                logger.warn("이미지를 삭제 하지 못했습니다.");
                throw new DeleteException("ERR_PRODUCT_IMAGE_2", productId);
            } else {
                logger.info("이미지를 정상적으로 삭제했습니다.");
            }
        }
    }

}
