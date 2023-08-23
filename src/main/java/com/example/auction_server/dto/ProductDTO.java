package com.example.auction_server.dto;

import com.example.auction_server.enums.ProductStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;

    private Long saleUserId;

    @NotBlank
    private String productName;

    @NotBlank
    private Long categoryId;

    @Lob
    @NotBlank
    private String explanation;

    private LocalDateTime productRegisterTime;

    @NotBlank
    private int startPrice;

    @NotBlank
    private LocalDateTime startTime;

    @NotBlank
    private LocalDateTime endTime;

    @NotBlank
    private int highestPrice;

    private ProductStatus productStatus;

    private List<ProductImageDTO> imageDTOS;


}
