package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.config.TestDatabaseConfig;
import com.ccommit.auction_server.config.TestElasticsearchConfig;
import com.ccommit.auction_server.config.testDataInitializer.TestDataInitializer;
import com.ccommit.auction_server.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestDatabaseConfig.class, TestElasticsearchConfig.class, TestDataInitializer.class})
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TestDataInitializer testDataInitializer;

    private final int DELETE_SUCCESS = 1;
    private Category savedCategory;

    @BeforeEach
    public void generateTestCategory() {
        //given
        savedCategory = testDataInitializer.getSavedCategory();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("카테고리 식별자로 카테고리 조회")
    void findByCategoryId() {
        //when
        Optional<Category> findCategory = categoryRepository.findByCategoryId(savedCategory.getCategoryId());

        //then
        assertNotNull(findCategory);
        assertEquals(savedCategory.getCategoryName(), findCategory.get().getCategoryName());
    }

    @Test
    @DisplayName("카테고리 식별자로 카테고리 삭제")
    void deleteByCategoryId() {
        Category deleteCategory = testDataInitializer.createCategory("deleteCategory", 1000);

        //when
        int deletedCategoryCount = categoryRepository.deleteByCategoryId(deleteCategory.getCategoryId());

        //then
        assertTrue(categoryRepository.findByCategoryId(deleteCategory.getCategoryId()).isEmpty());
        assertEquals(DELETE_SUCCESS, deletedCategoryCount);
    }

    @Test
    @DisplayName("카테고리명 중복 검사")
    void existsByCategoryName() {
        //when
        boolean existsCategoryName = categoryRepository.existsByCategoryName(savedCategory.getCategoryName());

        //then
        assertTrue(existsCategoryName);
    }

    @Test
    @DisplayName("유효한 카테고리 식별자 확인")
    void existsByCategoryId() {
        //when
        boolean existsCategoryId = categoryRepository.existsByCategoryId(savedCategory.getCategoryId());

        //then
        assertTrue(existsCategoryId);
    }
}