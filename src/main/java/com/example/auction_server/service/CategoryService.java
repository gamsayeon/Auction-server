package com.example.auction_server.service;

import com.example.auction_server.dto.CategoryDTO;

public interface CategoryService {
    CategoryDTO registerCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);

    CategoryDTO selectCategory(Long categoryId);

    String deleteCategory(Long categoryId);
}
