package com.example.auction_server.repository;

import com.example.auction_server.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private String TEST_CATEGORY_NAME = "testCategoryName";
    private int DELETE_SUCCESS = 1;
    private Long SAVED_CATEGORY_ID;

    @BeforeEach
    public void generateTestCategory() {
        Category category = new Category();
        category.setCategoryName(TEST_CATEGORY_NAME);
        category.setBidMinPrice(1000);

        SAVED_CATEGORY_ID = categoryRepository.save(category).getCategoryId();
    }

    @Test
    @DisplayName("카테고리 식별자로 카테고리 조회")
    void findByCategoryId() {
        Optional<Category> findCategory = categoryRepository.findByCategoryId(SAVED_CATEGORY_ID);

        assertNotNull(findCategory);
        assertEquals(TEST_CATEGORY_NAME, findCategory.get().getCategoryName());
    }

    @Test
    @DisplayName("카테고리 식별자로 카테고리 삭제")
    void deleteByCategoryId() {
        int deleteCategory = categoryRepository.deleteByCategoryId(SAVED_CATEGORY_ID);

        assertTrue(categoryRepository.findByCategoryId(SAVED_CATEGORY_ID).isEmpty());
        assertEquals(DELETE_SUCCESS, deleteCategory);
    }

    @Test
    @DisplayName("카테고리명 중복 검사")
    void existsByCategoryName() {
        Boolean existsCategoryName = categoryRepository.existsByCategoryName(TEST_CATEGORY_NAME);

        assertEquals(true, existsCategoryName);
    }

    @Test
    @DisplayName("유효한 카테고리 식별자 확인")
    void existsByCategoryId() {
        Boolean existsCategoryId = categoryRepository.existsByCategoryId(SAVED_CATEGORY_ID);

        assertEquals(true, existsCategoryId);
    }
}