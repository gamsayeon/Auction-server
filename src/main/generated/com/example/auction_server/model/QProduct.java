package com.example.auction_server.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = 1084341868L;

    public static final QProduct product = new QProduct("product");

    public final NumberPath<Long> categoryId = createNumber("categoryId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final StringPath explanation = createString("explanation");

    public final NumberPath<Integer> highestPrice = createNumber("highestPrice", Integer.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final StringPath productName = createString("productName");

    public final DateTimePath<java.time.LocalDateTime> productRegisterTime = createDateTime("productRegisterTime", java.time.LocalDateTime.class);

    public final EnumPath<com.example.auction_server.enums.ProductStatus> productStatus = createEnum("productStatus", com.example.auction_server.enums.ProductStatus.class);

    public final NumberPath<Long> saleUserId = createNumber("saleUserId", Long.class);

    public final NumberPath<Integer> startPrice = createNumber("startPrice", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public QProduct(String variable) {
        super(Product.class, forVariable(variable));
    }

    public QProduct(Path<? extends Product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProduct(PathMetadata metadata) {
        super(Product.class, metadata);
    }

}

