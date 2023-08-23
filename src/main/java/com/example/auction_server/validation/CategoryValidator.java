package com.example.auction_server.validation;

import com.example.auction_server.exception.DuplicateException;
import com.example.auction_server.repository.CategoryRepository;
import com.example.auction_server.validation.annotation.CategoryValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CategoryValidator implements ConstraintValidator<CategoryValidation, String> {
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LogManager.getLogger(CategoryValidator.class);


    public CategoryValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean isValid(String categoryName, ConstraintValidatorContext context) {
        boolean isDuplicateCategory = categoryRepository.existsByCategoryName(categoryName);

        if (isDuplicateCategory) {
            logger.warn("중복된 category 입니다.");
            throw new DuplicateException("ERR_2003", categoryName);
        }

        return true;
    }
}
