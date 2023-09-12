package com.example.auction_server.mapper;

import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.model.Bid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BidMapper {
    private final ModelMapper modelMapper;

    public Bid convertToEntity(BidDTO bidDTO) {
        Bid bid = modelMapper.map(bidDTO, Bid.class);
        return bid;
    }

    public BidDTO convertToDTO(Bid bid) {
        BidDTO bidDTO = modelMapper.map(bid, BidDTO.class);
        return bidDTO;
    }
}
