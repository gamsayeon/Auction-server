package com.example.auction_server.dto;

import com.example.auction_server.enums.ProductStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;

    private Long saleId;

    @NotBlank
    private String productName;

    @NotNull
    private Long categoryId;

    @Lob
    @NotBlank
    private String explanation;

    private LocalDateTime productRegisterTime;

    @Min(value = 1000)
    private int startPrice;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @Min(value = 1000)
    private int highestPrice;

    private ProductStatus productStatus;

    private List<ProductImageDTO> imageDTOS;

}
