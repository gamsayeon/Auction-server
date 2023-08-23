package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.CategoryDTO;
import com.example.auction_server.exception.*;
import com.example.auction_server.mapper.CategoryMapper;
import com.example.auction_server.model.Category;
import com.example.auction_server.repository.CategoryRepository;
import com.example.auction_server.service.CategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final int DELETE_SUCCESS = 1;
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);


    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryDTO registerCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.convertToEntity(categoryDTO);

        Category resultCategory = categoryRepository.save(category);
        if (resultCategory != null) {
            CategoryDTO resultCategoryDTO = categoryMapper.convertToDTO(resultCategory);
            if (resultCategoryDTO != null) {
                logger.info(resultCategoryDTO.getCategoryName() + "을 카테고리 등록에 성공했습니다.");
                return resultCategoryDTO;
            } else {
                logger.warn("매핑에 실패했습니다.");
                throw new AddException("ERR_1001", categoryDTO);
            }
        } else {
            logger.warn("category 등록 오류. 재시도 해주세요.");
            throw new AddException("ERR_1002", categoryDTO);
        }
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = categoryMapper.convertToEntity(categoryDTO);
        Optional<Category> optionalCategory = categoryRepository.findByCategoryId(categoryId);
        if (optionalCategory.isEmpty()) {
            logger.warn("해당하는 카테고리는 찾지 못했습니다.");
            throw new NotMatchingException("ERR_4003", categoryId);
        } else {
            category.setCategoryId(optionalCategory.get().getCategoryId());
            Category resultCategory = categoryRepository.save(category);
            if (resultCategory == null) {
                logger.warn("카테고리를 수정하지 못했습니다.");
                throw new AddException("ERR_1003", categoryDTO);
            } else {
                CategoryDTO resultCategoryDTO = categoryMapper.convertToDTO(resultCategory);
                logger.info(resultCategoryDTO.getCategoryName() + "을 카테고리 수정에 성공했습니다.");
                return resultCategoryDTO;
            }
        }
    }

    @Override
    public CategoryDTO selectCategory(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryId(categoryId);
        if (optionalCategory.isEmpty()) {
            logger.warn("해당하는 카테고리는 찾지 못했습니다.");
            throw new NotMatchingException("ERR_4003", categoryId);
        } else {
            CategoryDTO resultCategoryDTO = categoryMapper.convertToDTO(optionalCategory.get());
            logger.info("카테고리 조회에 성공했습니다.");
            return resultCategoryDTO;
        }
    }

    @Override
    @Transactional
    public String deleteCategory(Long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryId(categoryId);
        if (categoryRepository.deleteByCategoryId(categoryId) == DELETE_SUCCESS) {
            String categoryName = optionalCategory.get().getCategoryName();
            logger.info(categoryName + "을 카테고리 삭제에 성공했습니다.");
            return categoryName;
        } else {
            logger.warn("category 실패 오류. 재시도 해주세요.");
            throw new DeleteException("ERR_11000", categoryId);
        }
    }

}
