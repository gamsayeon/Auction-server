package com.example.auction_server.repository;

import com.example.auction_server.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);
    Optional<Category> findByCategoryId(Long categoryId);
    int deleteByCategoryId(Long categoryId);
}
