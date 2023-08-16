package com.example.auction_server.service;

import com.example.auction_server.dto.CategoryDTO;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryService {
    CategoryDTO registerCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
    boolean checkDuplicationCategoryName(String categoryName);
    String deleteCategory(Long categoryId);
}
