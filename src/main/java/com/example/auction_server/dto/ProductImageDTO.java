package com.example.auction_server.dto;

import jakarta.persistence.Lob;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {
    private Long imageId;

    private Long productId;

    @Lob
    private String imagePath;
}
