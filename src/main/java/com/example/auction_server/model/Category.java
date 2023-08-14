package com.example.auction_server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity(name = "category")
@Getter
@Setter
@Table(name = "category")
@DynamicUpdate
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "bid_max_price")
    private int bidMaxPrice;

    @Column(name = "bid_min_price")
    private int bidMinPrice;
}
