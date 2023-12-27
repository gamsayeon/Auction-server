package com.ccommit.auction_server.elasticsearchRepository.repositoryImpl;

import com.ccommit.auction_server.elasticsearchRepository.ProductSearchRepository;
import com.ccommit.auction_server.model.ELK.DocumentProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


@Repository
@RequiredArgsConstructor
public class ProductSearchRepositoryImplElk implements ProductSearchRepository {
    private final ElasticsearchOperations operations;

    @Override
    public Page<DocumentProduct> searchProducts(String productName, Long saleId, Long categoryId, String explanation, Pageable pageable) {
        CriteriaQuery query = createConditionCriteriaQuery(productName, saleId, categoryId, explanation);

        SearchHits<DocumentProduct> searchHits = operations.search(query, DocumentProduct.class);

        return SearchHitSupport
                .searchPageFor(searchHits, pageable)
                .map(SearchHit::getContent);
    }

    public CriteriaQuery createConditionCriteriaQuery(String productName, Long saleId, Long categoryId, String explanation) {
        Criteria baseCriteria = new Criteria();

        if (StringUtils.hasText(productName)) {
            baseCriteria = baseCriteria.and("product_name").is(productName);
        }

        if (saleId != null) {
            baseCriteria = baseCriteria.and("sale_id").is(saleId);
        }

        if (categoryId != null) {
            baseCriteria = baseCriteria.and("category_id").is(categoryId);
        }

        if (StringUtils.hasText(explanation)) {
            baseCriteria = baseCriteria.and("explanation").is(explanation);
        }
        CriteriaQuery query = new CriteriaQuery(baseCriteria);

        return query;
    }
}
