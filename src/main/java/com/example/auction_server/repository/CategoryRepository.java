package com.example.auction_server.repository;

import com.example.auction_server.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryId(Long categoryId);
    boolean existsByCategoryNameAndCategoryIdNot(String categoryName, Long categoryId);

    int deleteByCategoryId(Long categoryId);

    boolean existsByCategoryName(String categoryName);

    boolean existsByCategoryId(Long categoryId);
}
