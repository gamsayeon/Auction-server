package com.example.auction_server.validation;

import com.example.auction_server.dto.ProductDTO;
import com.example.auction_server.exception.InputSettingException;
import com.example.auction_server.exception.NotMatchingException;
import com.example.auction_server.repository.CategoryRepository;
import com.example.auction_server.repository.ProductRepository;
import com.example.auction_server.validation.annotation.RegisterProductValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class RegisterProductValidator implements ConstraintValidator<RegisterProductValidation, ProductDTO> {

    private final int TIME_COMPARE = 0;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LogManager.getLogger(UniqueCategoryValidator.class);


    public RegisterProductValidator(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void initialize(RegisterProductValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(ProductDTO productDTO, ConstraintValidatorContext context) {
        if (productDTO == null) {
            return false;
        }

        if (!categoryRepository.existsByCategoryId(productDTO.getCategoryId())) {
            logger.warn("해당 카테고리를 찾지 못했습니다.");
            throw new NotMatchingException("ERR_4003", productDTO.getCategoryId());
        }

        if (productDTO.getStartTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) {  //경매 시작시간을 과거로 입력
            logger.warn("경매 시작시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_10001", productDTO);
        } else if (productDTO.getEndTime().compareTo(LocalDateTime.now()) < TIME_COMPARE) { //경매 마감시간을 과거로 입력
            logger.warn("경매 마감시간을 과거 시간으로 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_10001", productDTO);
        } else if (productDTO.getStartTime().compareTo(productDTO.getEndTime()) > TIME_COMPARE) {   //경매 마감시간을 경매 시작시간보다 과거로 입력
            logger.warn("경매 시작시간을 잘못 입력하셨습니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_10001", productDTO);
        } else if (productDTO.getStartPrice() >= productDTO.getHighestPrice()) {    // 경매 시작가가 즉시구매가보다 작거나 같을때
            logger.warn("경매 시작가가 즉시구매가와 같거나 큽니다. 다시 입력해주세요.");
            throw new InputSettingException("ERR_10002", productDTO);
        }

        return true;
    }

}
