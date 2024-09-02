package com.ccommit.auction_server.model;

import com.ccommit.auction_server.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩을 의미, 실제 이 객체가 필요시 로드 됨
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "product_register_time")
    private LocalDateTime productRegisterTime;

    @Column(name = "start_price")
    private int startPrice;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "highest_price")
    private int highestPrice;

    @Column(name = "product_status")
    private ProductStatus productStatus;
}