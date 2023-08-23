package com.example.auction_server.validation;

import com.example.auction_server.dto.CategoryDTO;
import com.example.auction_server.exception.DuplicateException;
import com.example.auction_server.exception.InputSettingException;
import com.example.auction_server.repository.CategoryRepository;
import com.example.auction_server.validation.annotation.UniqueCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniqueCategoryValidator implements ConstraintValidator<UniqueCategory, CategoryDTO> {
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LogManager.getLogger(UniqueCategoryValidator.class);


    public UniqueCategoryValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void initialize(UniqueCategory constraintAnnotation) {
    }

    @Override
    public boolean isValid(CategoryDTO categoryDTO, ConstraintValidatorContext context) {
        if (categoryDTO == null) {
            return false;
        }

        boolean isDuplicateCategory = categoryRepository.existsByCategoryName(categoryDTO.getCategoryName());

        if (isDuplicateCategory) {
            logger.warn("중복된 category 입니다.");
            throw new DuplicateException("ERR_2003", categoryDTO);
        } else if (categoryDTO.getBidMinPrice() >= categoryDTO.getBidMaxPrice()) {
            logger.warn("금액을 잘못설정했습니다.");
            throw new InputSettingException("ERR_10000", categoryDTO);
        }

        return true;
    }
}
