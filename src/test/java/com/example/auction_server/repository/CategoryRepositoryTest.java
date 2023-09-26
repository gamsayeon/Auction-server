package com.example.auction_server.repository;

import com.example.auction_server.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Category generateTestCategory() {
        Category category = new Category();
        category.setCategoryName("testCategoryName");
        category.setBidMinPrice(1000);
        return category;
    }

    @Test
    void findByCategoryId() {
        Category category = this.generateTestCategory();
        Category savedCategory = categoryRepository.save(category);

        Optional<Category> findCategory = categoryRepository.findByCategoryId(savedCategory.getCategoryId());

        assertNotNull(findCategory);
        assertEquals(findCategory.get().getCategoryName(), category.getCategoryName());
    }

    @Test
    void deleteByCategoryId() {
        Category category = this.generateTestCategory();
        Category savedCategory = categoryRepository.save(category);

        int deleteCategory = categoryRepository.deleteByCategoryId(savedCategory.getCategoryId());

        assertNotNull(deleteCategory);
        assertEquals(categoryRepository.findAll().size(), 0);
        assertEquals(deleteCategory, 1);
    }

    @Test
    void existsByCategoryName() {
        Category category = this.generateTestCategory();
        categoryRepository.save(category);

        Boolean existsCategoryName = categoryRepository.existsByCategoryName(category.getCategoryName());

        assertNotNull(existsCategoryName);
        assertEquals(existsCategoryName, true);
    }

    @Test
    void existsByCategoryId() {
        Category category = this.generateTestCategory();
        Category savedCategory = categoryRepository.save(category);

        Boolean existsCategoryId = categoryRepository.existsByCategoryId(savedCategory.getCategoryId());

        assertNotNull(existsCategoryId);
        assertEquals(existsCategoryId, true);
    }
}