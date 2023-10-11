package com.example.auction_server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCommentDTO {
    private Long commentId;

    private Long parentCommentId;

    private Long productId;

    private Long userId;

    @NotBlank
    private String comment;

    private LocalDateTime createTime;
}
