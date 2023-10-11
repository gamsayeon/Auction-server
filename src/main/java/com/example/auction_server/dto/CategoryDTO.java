package com.example.auction_server.dto;

import com.example.auction_server.validation.annotation.isExistCategoryValidation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @NotBlank
    @isExistCategoryValidation
    private String categoryName;

    @Min(value = 1000)
    private int bidMinPrice;

}
