package com.example.auction_server.validation;

import com.example.auction_server.exception.DuplicateException;
import com.example.auction_server.repository.CategoryRepository;
import com.example.auction_server.validation.annotation.isExistCategoryValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class isExistCategoryValidator implements ConstraintValidator<isExistCategoryValidation, String> {
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LogManager.getLogger(isExistCategoryValidator.class);


    public isExistCategoryValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean isValid(String categoryName, ConstraintValidatorContext context) {
        boolean isDuplicateCategory = categoryRepository.existsByCategoryName(categoryName);

        if (isDuplicateCategory) {
            logger.warn("중복된 category 입니다.");
            throw new DuplicateException("CATEGORY_1", categoryName);
        }

        return true;
    }
}
