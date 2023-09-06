package com.example.auction_server.dto;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private Long imageId;

    private Long productId;

    @Lob
    private String imagePath;
}
