package com.example.auction_server.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchProductDTO {
    private int page;

    private int pageSize;

    private int totalItems;

    private int totalPages;

    private List<ProductDTO> productDTOs;
}
