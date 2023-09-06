package com.example.auction_server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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
