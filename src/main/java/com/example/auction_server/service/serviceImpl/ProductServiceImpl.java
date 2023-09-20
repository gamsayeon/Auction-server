package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.ProductImageDTO;
import com.example.auction_server.dto.SearchProductDTO;
import com.example.auction_server.enums.ProductSortOrder;
import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.exception.*;
import com.example.auction_server.mapper.ProductImageMapper;
import com.example.auction_server.mapper.ProductMapper;
import com.example.auction_server.model.Product;
import com.example.auction_server.model.ProductImage;
import com.example.auction_server.repository.*;
import com.example.auction_server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final int TIME_COMPARE = 0;
    private final int DELETE_FAIL = 0;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Override
    @Transactional
    public ProductDTO registerProduct(Long saleId, ProductDTO productDTO) {
        this.validatorProduct(productDTO);

        Product product = productMapper.convertToEntity(productDTO);
        product.setSaleId(saleId);
        product.setProductRegisterTime(LocalDateTime.now());
        product.setProductStatus(ProductStatus.PRODUCT_REGISTRATION);
        Product resultProduct = productRepository.save(product);

        if (resultProduct != null) {
            ProductDTO resultProductDTO = productMapper.convertToDTO(resultProduct);
            try {
                if (!productDTO.getImageDTOS().isEmpty()) {
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
            throw new AddException("PRODUCT_1", product);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<ProductImageDTO> registerProductImage(ProductDTO productDTO, Long productId) {
        List<ProductImage> resultProductImages = new ArrayList<>();
        List<ProductImage> productImages = productImageMapper.convertToEntity(productDTO);
        for (ProductImage productImage : productImages) {
            if (productImage.getImagePath() != "") {
                productImage.setProductId(productId);
                ProductImage resultProductImage = productImageRepository.save(productImage);

                if (resultProductImage == null) {
                    logger.warn("이미지 등록에 실패 했습니다.");
                    throw new AddException("PRODUCT_IMAGE_1", productImage);
                } else resultProductImages.add(resultProductImage);
            } else {
                logger.warn("이미지 등록에 실패 했습니다.");
                throw new AddException("PRODUCT_IMAGE_1", productImage);
            }
        }
        return productImageMapper.convertToDTO(resultProductImages);
    }

    public void validatorProduct(ProductDTO productDTO) {
        if (!categoryRepository.existsByCategoryId(productDTO.getCategoryId())) {
            logger.warn("해당 카테고리를 찾지 못했습니다.");
            throw new NotMatchingException("CATEGORY_4", productDTO.getCategoryId());
        }

        if (productDTO.getStartTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) {  //경매 시작시간을 과거로 입력
            logger.warn("경매 시작시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("PRODUCT_7", productDTO);
        } else if (productDTO.getEndTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) { //경매 마감시간을 과거로 입력
            logger.warn("경매 마감시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("PRODUCT_7", productDTO);
        } else if (productDTO.getStartTime().compareTo(productDTO.getEndTime()) > TIME_COMPARE) {   //경매 마감시간을 경매 시작시간보다 과거로 입력
            logger.warn("경매 시작시간을 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("PRODUCT_7", productDTO);
        } else if (productDTO.getStartPrice() >= productDTO.getHighestPrice()) {    // 경매 시작가가 즉시구매가보다 작거나 같을때
            logger.warn("경매 시작가가 즉시구매가와 같거나 큽니다. 다시 입력해주세요.");
            throw new InputSettingException("PRODUCT_8", productDTO);
        }
    }

    @Override
    public ProductDTO selectProduct(Long productId) {
        Product product = productRepository.findByProductId(productId);
        if (product != null) {
            switch (product.getProductStatus()) {
                case PRODUCT_REGISTRATION:
                case AUCTION_PROCEEDING:
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
            throw new NotMatchingException("CATEGORY_5", productId);
        }
    }

    @Override
    public List<ProductDTO> selectProductForUser(Long saleId) {
        List<Product> products = productRepository.findBySaleId(saleId);
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
            throw new NotMatchingException("CATEGORY_5", saleId);
        }
    }


    @Override
    @Transactional
    public ProductDTO updateProduct(Long saleId, Long productId, ProductDTO productDTO) {
        Product resultProduct = productRepository.findByProductId(productId);

        if (resultProduct.getProductStatus() != ProductStatus.PRODUCT_REGISTRATION) {
            logger.warn("해당 상품은 경매가 시작되여 수정이 불가능합니다.");
            throw new UpdateException("PRODUCT_2", resultProduct.getProductStatus());
        }

        if (resultProduct.getSaleId() == saleId) {
            this.validatorProduct(productDTO);

            Product product = productMapper.convertToEntity(productDTO);
            product.setProductId(resultProduct.getProductId());
            product.setSaleId(resultProduct.getSaleId());
            product.setProductRegisterTime(resultProduct.getProductRegisterTime());
            resultProduct = productRepository.save(resultProduct);
            if (resultProduct == null) {
                logger.warn("상품을 수정하지 못했습니다.");
                throw new UpdateException("PRODUCT_3", "retry");
            }

            ProductDTO resultProductDTO = productMapper.convertToDTO(resultProduct);
            this.deleteProductImage(productId);
            List<ProductImageDTO> resultProductImageDTOs = this.registerProductImage(productDTO, productId);

            resultProductDTO.setImageDTOS(resultProductImageDTOs);
            logger.info("상품을 수정했습니다.");
            return resultProductDTO;
        } else {
            logger.warn("권한이 없어 해당 상품을 수정하지 못합니다.");
            throw new UserAccessDeniedException("PRODUCT_6", saleId);
        }
    }

    @Override
    @Transactional
    public void updateProductStatus() {
        List<Product> resultProducts = productRepository.findByProductStatus(ProductStatus.PRODUCT_REGISTRATION);
        for (Product product : resultProducts) {
            if (product.getStartTime().compareTo(LocalDateTime.now()) <= 0) {  //경매 시작시간이 현재시간과 비교해서 과거인지 확인
                product.setProductStatus(ProductStatus.AUCTION_PROCEEDING);
                Product resultProduct = productRepository.save(product);
                if (resultProduct == null) {
                    logger.warn("경매 시작 상태로 수정하지 못했습니다.");
                    throw new UpdateException("PRODUCT_5", product.getProductId());
                } else {
                    logger.info("경매 상태를 AUCTION_PROCEEDING 로 성공적으로 바꿨습니다.");
                    String recipientEmail = userRepository.findEmailById(resultProduct.getSaleId());
                    emailService.notifyAuction(recipientEmail, "경매 시작",
                            resultProduct.getProductName() + "의 경매가 시작되었습니다.");
                }
            }
        }
        resultProducts = productRepository.findByProductStatus(ProductStatus.AUCTION_PROCEEDING);
        for (Product product : resultProducts) {
            if (product.getEndTime().compareTo(LocalDateTime.now()) <= 0) {   //경매 마감시간이 현재시간과 비교해서 과거인지 확인
                product.setProductStatus(ProductStatus.AUCTION_END);
                Product resultProduct = productRepository.save(product);
                if (resultProduct == null) {
                    logger.warn("경매 마감 상태로 수정하지 못했습니다.");
                    throw new UpdateException("PRODUCT_5", product.getProductId());
                } else {
                    logger.info("경매 상태를 AUCTION_END 로 성공적으로 바꿨습니다.");
                    String recipientEmail = userRepository.findEmailById(resultProduct.getSaleId());
                    emailService.notifyAuction(recipientEmail, "경매 종료",
                            resultProduct.getProductName() + "의 경매가 종료되었습니다.");
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long saleId, Long productId) {
        Product resultProduct = productRepository.findByProductId(productId);

        if (resultProduct.getProductStatus() != ProductStatus.PRODUCT_REGISTRATION) {
            logger.warn("해당 상품은 경매가 시작되여 삭제가 불가능합니다.");
            throw new UpdateException("PRODUCT_2", resultProduct.getProductStatus());
        } else if (saleId == resultProduct.getSaleId()) {
            this.deleteProductImage(productId);
            int resultDelete = productRepository.deleteBySaleIdAndProductId(saleId, productId);
            if (resultDelete == DELETE_FAIL) {
                logger.warn("상품을 삭제 하지 못했습니다.");
                throw new DeleteException("COMMON_4", productId);
            } else {
                logger.info("상품을 삭제 했습니다.");
            }
        } else {
            logger.warn("권한이 없습니다.");
            throw new UserAccessDeniedException("COMMON_3", saleId);
        }
    }

    public void deleteProductImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        if (!productImages.isEmpty()) {
            if (productImageRepository.deleteAllByProductId(productId) != productImages.size()) {
                logger.warn("이미지를 삭제 하지 못했습니다.");
                throw new DeleteException("PRODUCT_IMAGE_2", productId);
            } else {
                logger.info("이미지를 정상적으로 삭제했습니다.");
            }
        }
    }

    @Override
    public SearchProductDTO findByKeyword(String productName, Long saleId, Long categoryId,
                                          String explanation, int page, int pageSize, ProductSortOrder sortOrder) {
        List<Product> products = productRepository.searchProducts(productName, saleId, categoryId, explanation, page, pageSize);
        products = this.sortProducts(sortOrder, products);

        List<ProductDTO> productDTOs = new ArrayList<>();
        for (Product product : products) {
            productDTOs.add(productMapper.convertToDTO(product));
        }
        int totalItems = productRepository.countBySearchProducts(productName, saleId, categoryId, explanation);
        int totalPages = totalItems / pageSize;
        if (totalItems % pageSize != 0) {
            totalPages++;
        }
        SearchProductDTO searchProductDTD = SearchProductDTO.builder()
                .page(page)
                .pageSize(pageSize)
                .totalItems(totalItems)
                .totalPages(totalPages)
                .productDTOs(productDTOs)
                .build();
        return searchProductDTD;
    }

    public List<Product> sortProducts(ProductSortOrder sortOrder, List<Product> products) {
        switch (sortOrder) {
            case BIDDER_COUNT_DESC:             //입찰자가 많은 순
                Collections.sort(products, (p1, p2) -> {
                    Long productId1 = p1.getProductId();
                    Long productId2 = p2.getProductId();

                    if (bidRepository.countByProductId(productId1) > bidRepository.countByProductId(productId2)) {
                        return -1; // p1이 p2보다 작으면 음수 값 반환
                    } else if (bidRepository.countByProductId(productId1) < bidRepository.countByProductId(productId2)) {
                        return 1; // p1이 p2보다 크면 양수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case HIGHEST_PRICE_DESC:     // 최고 즉시 구매가 순
                Collections.sort(products, (p1, p2) -> {
                    double highestPrice1 = p1.getHighestPrice();
                    double highestPrice2 = p2.getHighestPrice();

                    if (highestPrice1 < highestPrice2) {
                        return -1; // p1이 p2보다 작으면 음수 값 반환
                    } else if (highestPrice1 > highestPrice2) {
                        return 1; // p1이 p2보다 크면 양수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case HIGHEST_PRICE_ASC:     // 최저 즉시 구매가 순
                Collections.sort(products, (p1, p2) -> {
                    double highestPrice1 = p1.getHighestPrice();
                    double highestPrice2 = p2.getHighestPrice();

                    if (highestPrice1 < highestPrice2) {
                        return 1; // 역순: p1이 p2보다 작으면 양수 값 반환
                    } else if (highestPrice1 > highestPrice2) {
                        return -1; // 역순: p1이 p2보다 크면 음수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case BID_PRICE_DESC:             //최고 입찰가 순
                Collections.sort(products, (p1, p2) -> {
                    Integer maxPriceProductId1 = this.currentBid(p1.getProductId());
                    Integer maxPriceProductId2 = this.currentBid(p2.getProductId());

                    if (maxPriceProductId1 > maxPriceProductId2) {
                        return -1; // p1이 p2보다 작으면 음수 값 반환
                    } else if (maxPriceProductId1 < maxPriceProductId2) {
                        return 1; // p1이 p2보다 크면 양수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case BID_PRICE_ASC:             //최저 입찰가 순
                Collections.sort(products, (p1, p2) -> {
                    Integer maxPriceProductId1 = this.currentBid(p1.getProductId());
                    Integer maxPriceProductId2 = this.currentBid(p2.getProductId());

                    if (maxPriceProductId1 < maxPriceProductId2) {
                        return -1; // p1이 p2보다 작으면 음수 값 반환
                    } else if (maxPriceProductId1 > maxPriceProductId2) {
                        return 1; // p1이 p2보다 크면 양수 값 반환
                    } else {
                        return 0; // p1과 p2가 같으면 0 반환
                    }
                });
                break;
            case NEWEST_FIRST:                  //등록일 최신 순
                Collections.sort(products, (p1, p2) -> {
                    LocalDateTime registerTime1 = p1.getProductRegisterTime();
                    LocalDateTime registerTime2 = p2.getProductRegisterTime();

                    return registerTime1.compareTo(registerTime2);
                });
                break;
            case OLDEST_FIRST:                  //등록일 과거 순
                Collections.sort(products, (p1, p2) -> {
                    LocalDateTime registerTime1 = p1.getProductRegisterTime();
                    LocalDateTime registerTime2 = p2.getProductRegisterTime();

                    return registerTime2.compareTo(registerTime1);
                });
                break;
        }
        return products;
    }

    public Integer currentBid(Long productId) {
        Integer maxPriceProductId = bidRepository.findMaxPriceByProductId(productId);
        if (maxPriceProductId == null)
            maxPriceProductId = productRepository.findByProductId(productId).getStartPrice();
        return maxPriceProductId;
    }

}
