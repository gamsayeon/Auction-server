package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.ProductCommentDTO;
import com.example.auction_server.exception.AddException;
import com.example.auction_server.mapper.ProductCommentMapper;
import com.example.auction_server.model.ProductComment;
import com.example.auction_server.repository.ProductCommentRepository;
import com.example.auction_server.service.ProductCommentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductCommentServiceImpl implements ProductCommentService {

    private final Logger logger = LogManager.getLogger(ProductCommentServiceImpl.class);

    private final ProductCommentRepository productCommentRepository;
    private final ProductCommentMapper productCommentMapper;

    public ProductCommentServiceImpl(ProductCommentRepository productCommentRepository, ProductCommentMapper productCommentMapper) {
        this.productCommentRepository = productCommentRepository;
        this.productCommentMapper = productCommentMapper;
    }

    @Override
    public ProductCommentDTO registerProduct(Long userId, Long productId, ProductCommentDTO productCommentDTO) {
        ProductComment productComment = productCommentMapper.convertToEntity(productCommentDTO);
        productComment.setProductId(productId);
        productComment.setUserId(userId);
        productComment.setCreateTime(LocalDateTime.now());

        ProductComment resultProductComment = productCommentRepository.save(productComment);
        if (resultProductComment != null) {
            ProductCommentDTO resultProductCommentDTO = productCommentMapper.convertToDTO(resultProductComment);
            logger.info("댓글을 등록했습니다.");
            return resultProductCommentDTO;
        } else {
            logger.warn("댓글을 등록하지 못했습니다.");
            throw new AddException("PRODUCT_COMMENT_1", productCommentDTO);
        }
    }
}
