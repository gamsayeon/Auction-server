package com.example.auction_server.mapper;

import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.model.Bid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BidMapper {
    private final ModelMapper modelMapper;

    public BidMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Bid convertToEntity(BidDTO bidDTO) {
        Bid bid = modelMapper.map(bidDTO, Bid.class);
        return bid;
    }

    public BidDTO convertToDTO(Bid bid) {
        BidDTO bidDTO = modelMapper.map(bid, BidDTO.class);
        return bidDTO;
    }
}
