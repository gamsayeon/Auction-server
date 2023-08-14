package com.example.auction_server.service;

import com.example.auction_server.dto.CategoryDTO;
import com.example.auction_server.model.Category;

public interface CategoryService {
    CategoryDTO registerCategory(CategoryDTO categoryDTO);

//    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

    boolean checkDuplicationCategoryName(String categoryName);

    String deleteCategory(Long categoryId);
}
