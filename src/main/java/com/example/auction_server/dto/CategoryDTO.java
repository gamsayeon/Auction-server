package com.example.auction_server.dto;

import com.example.auction_server.validation.annotation.CategoryValidation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @NotBlank
    @CategoryValidation
    private String categoryName;

    @Min(value = 1000)
    private int bidMaxPrice;

    @Min(value = 1000)
    private int bidMinPrice;

}
