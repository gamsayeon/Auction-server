package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.dto.ProductImageDTO;
import com.example.auction_server.exception.AddException;
import com.example.auction_server.exception.DeleteException;
import com.example.auction_server.mapper.ProductImageMapper;
import com.example.auction_server.model.ProductImage;
import com.example.auction_server.repository.ProductImageRepository;
import com.example.auction_server.service.ProductImageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;
    private static final Logger logger = LogManager.getLogger(ProductImageServiceImpl.class);

    public ProductImageServiceImpl(ProductImageMapper productImageMapper, ProductImageRepository productImageRepository) {
        this.productImageMapper = productImageMapper;
        this.productImageRepository = productImageRepository;
    }

    @Override
    @Transactional
    public List<ProductImageDTO> registerProductImage(ProductDTO productDTO, Long productId) {
        List<ProductImage> resultProductImages = new ArrayList<>();
        List<ProductImage> productImages = productImageMapper.convertToEntity(productDTO);
        for (ProductImage productImage : productImages) {
            productImage.setProductId(productId);
            ProductImage resultProductImage = productImageRepository.save(productImage);
            if (resultProductImage == null) {
                logger.warn("이미지 등록에 실패 했습니다.");
                throw new AddException("ERR_1005", productImage);
            } else resultProductImages.add(resultProductImage);
        }
        return productImageMapper.convertToDTO(resultProductImages);
    }

    @Override
    public List<ProductImageDTO> selectProductImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        if (!productImages.isEmpty()) {
            List<ProductImageDTO> resultProductImageDTOs = productImageMapper.convertToDTO(productImages);
            return resultProductImageDTOs;
        } else return null;
    }

    @Override
    @Transactional
    public void deleteProductImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        if (!productImages.isEmpty()) {
            if (productImageRepository.deleteAllByProductId(productId) != productImages.size()) {
                logger.warn("이미지를 삭제 하지 못했습니다.");
                throw new DeleteException("ERR_11001", productId);
            }
        }
    }

}
