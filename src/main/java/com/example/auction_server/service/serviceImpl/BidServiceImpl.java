package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.BidDTO;
import com.example.auction_server.mapper.BidMapper;
import com.example.auction_server.model.Bid;
import com.example.auction_server.service.BidService;
import com.example.auction_server.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BidServiceImpl implements BidService {
    private final BidMapper bidMapper;
    private final MessageService messageService;

    public BidServiceImpl(BidMapper bidMapper,
                          MessageService messageService) {
        this.bidMapper = bidMapper;
        this.messageService = messageService;
    }

    @Override
    public BidDTO registerBid(Long buyerUserId, Long productId, BidDTO bidDTO) {
        Bid bid = Bid.builder()
                .buyerUserId(buyerUserId)
                .productId(productId)
                .bidTime(LocalDateTime.now())
                .price(bidDTO.getPrice())
                .build();

        messageService.sendMessage(bid);

        return bidMapper.convertToDTO(bid);
    }

}
