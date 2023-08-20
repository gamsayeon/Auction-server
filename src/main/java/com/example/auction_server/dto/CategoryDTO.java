package com.example.auction_server.dto;

import com.example.auction_server.validation.annotation.UniqueCategory;
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
@UniqueCategory
public class CategoryDTO {
    @NotBlank
    private String categoryName;
    @Min(value = 1000)
    private int bidMaxPrice;
    @Min(value = 1000)
    private int bidMinPrice;

}
