package com.ccommit.auction_server.model;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BidValidationErrorDetails {
    private int newBidPrice;
    private int currentHighestBidPrice;
    private int categoryMinimumBidPrice;

}
