package com.example.auction_server.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductComment is a Querydsl query type for ProductComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductComment extends EntityPathBase<ProductComment> {

    private static final long serialVersionUID = -2039876013L;

    public static final QProductComment productComment = new QProductComment("productComment");

    public final StringPath comment = createString("comment");

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createTime = createDateTime("createTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> parentCommentId = createNumber("parentCommentId", Long.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QProductComment(String variable) {
        super(ProductComment.class, forVariable(variable));
    }

    public QProductComment(Path<? extends ProductComment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductComment(PathMetadata metadata) {
        super(ProductComment.class, metadata);
    }

}

