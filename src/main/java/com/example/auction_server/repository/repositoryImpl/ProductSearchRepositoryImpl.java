package com.example.auction_server.repository.repositoryImpl;

import com.example.auction_server.model.Product;
import com.example.auction_server.model.QProduct;
import com.example.auction_server.repository.ProductSearchRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductSearchRepositoryImpl implements ProductSearchRepository {
    private EntityManager entityManager;

    public ProductSearchRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Product> searchProducts(String productName, Long saleUserId, Long categoryId, String explanation) {
        JPAQuery<Product> query = new JPAQuery<>(entityManager);

        QProduct product = QProduct.product;

        BooleanExpression whereClause = null;

        if (productName != null) {
            if (whereClause == null) {
                whereClause = product.productName.contains(productName);
            } else {
                whereClause = whereClause.and(product.productName.contains(productName));
            }
        }

        if (saleUserId != null) {
            if (whereClause == null) {
                whereClause = product.saleUserId.eq(saleUserId);
            } else {
                whereClause = whereClause.and(product.saleUserId.eq(saleUserId));
            }
        }

        if (categoryId != null) {
            if (whereClause == null) {
                whereClause = product.categoryId.eq(categoryId);
            } else {
                whereClause = whereClause.and(product.categoryId.eq(categoryId));
            }
        }

        if (explanation != null) {
            if (whereClause == null) {
                whereClause = product.explanation.contains(explanation);
            } else {
                whereClause = whereClause.and(product.explanation.contains(explanation));
            }
        }
        System.out.println(query.select(product).from(product).where(whereClause));
        return query.select(product).from(product).where(whereClause).fetch();
    }
}
