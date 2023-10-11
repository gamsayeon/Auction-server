package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.CategoryDTO;
import com.example.auction_server.dto.CategoryUpdateDTO;
import com.example.auction_server.mapper.CategoryMapper;
import com.example.auction_server.model.Category;
import com.example.auction_server.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("CategoryServiceImpl Unit 테스트")
@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    private String TEST_CATEGORY_NAME = "testCategoryName";
    private int TEST_BID_MIN_PRICE = 1000;
    private CategoryDTO requestCategoryDTO;
    private CategoryUpdateDTO categoryUpdateDTO;
    private Category convertedBeforeResponseCategory;
    private final int DELETE_SUCCESS = 1;

    @BeforeEach
    public void generateTestCategory() {
        requestCategoryDTO = CategoryDTO.builder()
                .categoryName(TEST_CATEGORY_NAME)
                .bidMinPrice(TEST_BID_MIN_PRICE)
                .build();

        categoryUpdateDTO = CategoryUpdateDTO.builder()
                .categoryName(TEST_CATEGORY_NAME)
                .bidMinPrice(TEST_BID_MIN_PRICE)
                .build();

        convertedBeforeResponseCategory = Category.builder()
                .categoryId(1L)
                .categoryName(TEST_CATEGORY_NAME)
                .bidMinPrice(TEST_BID_MIN_PRICE)
                .build();
    }

    @Test
    @DisplayName("카테고리 등록 성공 테스트")
    void registerCategory() {
        //given
        when(categoryMapper.convertToEntity(requestCategoryDTO)).thenReturn(convertedBeforeResponseCategory);
        when(categoryRepository.save(convertedBeforeResponseCategory)).thenReturn(convertedBeforeResponseCategory);
        when(categoryMapper.convertToDTO(convertedBeforeResponseCategory)).thenReturn(requestCategoryDTO);

        //when
        CategoryDTO result = categoryService.registerCategory(requestCategoryDTO);

        //then
        assertNotNull(result);
        assertEquals(TEST_CATEGORY_NAME, result.getCategoryName());
        assertEquals(TEST_BID_MIN_PRICE, result.getBidMinPrice());
    }

    @Test
    @DisplayName("카테고리 업데이트 성공 테스트")
    void updateCategory() {
        //given
        Category categoryUpdate = Category.builder()
                .categoryId(1L)
                .categoryName("update" + TEST_CATEGORY_NAME)
                .bidMinPrice(TEST_BID_MIN_PRICE + 1000)
                .build();

        when(categoryMapper.convertToEntity(categoryUpdateDTO)).thenReturn(categoryUpdate);
        when(categoryRepository.findByCategoryId(convertedBeforeResponseCategory.getCategoryId())).thenReturn(Optional.of(convertedBeforeResponseCategory));
        when(categoryRepository.save(categoryUpdate)).thenReturn(categoryUpdate);

        requestCategoryDTO.setCategoryName(categoryUpdate.getCategoryName());
        requestCategoryDTO.setBidMinPrice(categoryUpdate.getBidMinPrice());
        when(categoryMapper.convertToDTO(categoryUpdate)).thenReturn(requestCategoryDTO);

        //when
        CategoryDTO result = categoryService.updateCategory(categoryUpdateDTO, convertedBeforeResponseCategory.getCategoryId());

        //then
        assertEquals(result.getCategoryName(), requestCategoryDTO.getCategoryName());
    }

    @Test
    @DisplayName("카테고리 조회 성공 테스트")
    void selectCategory() {
        //given
        when(categoryRepository.findByCategoryId(convertedBeforeResponseCategory.getCategoryId())).thenReturn(Optional.of(convertedBeforeResponseCategory));
        when(categoryMapper.convertToDTO(convertedBeforeResponseCategory)).thenReturn(requestCategoryDTO);

        //when
        CategoryDTO result = categoryService.selectCategory(convertedBeforeResponseCategory.getCategoryId());

        //then
        assertEquals(requestCategoryDTO.getCategoryName(), result.getCategoryName());
    }

    @Test
    @DisplayName("카테고리 삭제 성공 테스트")
    void deleteCategory() {
        //given
        when(categoryRepository.findByCategoryId(convertedBeforeResponseCategory.getCategoryId())).thenReturn(Optional.of(convertedBeforeResponseCategory));
        when(categoryRepository.deleteByCategoryId(convertedBeforeResponseCategory.getCategoryId())).thenReturn(DELETE_SUCCESS);

        //when
        String result = categoryService.deleteCategory(convertedBeforeResponseCategory.getCategoryId());

        //then
        assertEquals(requestCategoryDTO.getCategoryName(), result);
        assertFalse(categoryRepository.existsByCategoryId(convertedBeforeResponseCategory.getCategoryId()));
    }
}