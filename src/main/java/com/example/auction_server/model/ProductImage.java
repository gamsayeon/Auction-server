package com.example.auction_server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "product_image")
@Getter
@Setter
@Table(name = "product_image")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "image_path")
    private String imagePath;
}
