package com.example.auction_server.mapper;

import com.example.auction_server.dto.ProductCommentDTO;
import com.example.auction_server.model.ProductComment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductCommentMapper {
    private final ModelMapper modelMapper;

    public ProductCommentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ProductComment convertToEntity(ProductCommentDTO productCommentDTO) {
        ProductComment productComment = modelMapper.map(productCommentDTO, ProductComment.class);
        return productComment;
    }

    public ProductCommentDTO convertToDTO(ProductComment productComment) {
        ProductCommentDTO productCommentDTO = modelMapper.map(productComment, ProductCommentDTO.class);
        return productCommentDTO;
    }
}
