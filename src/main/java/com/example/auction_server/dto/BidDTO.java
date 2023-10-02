package com.example.auction_server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidDTO {
    private Long buyerId;

    private Long productId;

    private LocalDateTime bidTime;

    @Min(value = 1000)
    @NotNull
    private int price;
}
