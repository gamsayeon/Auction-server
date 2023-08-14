package com.example.auction_server.service;

import com.example.auction_server.dto.CategoryDTO;

public interface CategoryService {
    CategoryDTO registerCategory(CategoryDTO categoryDTO);
    boolean checkDuplicationCategoryName(String categoryName);
    String deleteCategory(Long categoryId);
}
