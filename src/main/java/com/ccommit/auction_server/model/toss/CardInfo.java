package com.ccommit.auction_server.model.toss;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
    private boolean noInterest;
    private int spreadOut;
    private String cardAuthorizationNo;
    private String cardMethodType;
    private String cardUserType;
    private String cardNumber;
    private String cardBinNumber;
    private String cardNum4Print;
    private String salesCheckLinkUrl;
    private String cardCompanyName;
    private int cardCompanyCode;
}
