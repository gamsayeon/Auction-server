package com.ccommit.auction_server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryHighestBid {
    private Product product;
    private Category category;
    private Bid highestBid;
}