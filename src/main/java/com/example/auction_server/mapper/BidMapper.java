package com.example.auction_server.mapper;

import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.model.Bid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BidMapper {
    private final ModelMapper modelMapper;

    public Bid convertToEntity(BidDTO bidDTO) {
        Bid bid = Bid.builder()
                .buyerId(bidDTO.getBuyerId())
                .productId(bidDTO.getProductId())
                .bidTime(LocalDateTime.now())
                .price(bidDTO.getPrice())
                .build();
        return bid;
    }

    public BidDTO convertToDTO(Bid bid) {
        BidDTO bidDTO = modelMapper.map(bid, BidDTO.class);
        return bidDTO;
    }
}
