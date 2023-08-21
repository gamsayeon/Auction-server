package com.example.auction_server.dto;

import com.example.auction_server.enums.ProductStatus;
import com.example.auction_server.validation.annotation.RegisterProductValidation;
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
@RegisterProductValidation(groups = {ProductDTO.register.class})
public class ProductDTO {
    private Long productId;

    private Long saleUserId;

    @NotBlank(groups = {register.class})
    private String productName;

    @NotBlank(groups = {register.class})
    private Long categoryId;

    @Lob
    @NotBlank(groups = {register.class})
    private String explanation;

    private LocalDateTime productRegisterTime;

    @NotBlank(groups = {register.class})
    private int startPrice;

    @NotBlank(groups = {register.class})
    private LocalDateTime startTime;

    @NotBlank(groups = {register.class})
    private LocalDateTime endTime;

    @NotBlank(groups = {register.class})
    private int highestPrice;

    private ProductStatus productStatus;

    private List<ProductImageDTO> imageDTOS;

    public interface register {
    }

}
