package com.example.auction_server.validation;

import com.example.auction_server.exception.DuplicateException;
import com.example.auction_server.repository.UserRepository;
import com.example.auction_server.validation.annotation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(UniqueUserValidator.class);


    public UniqueEmailValidator(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return false;
        }

        boolean isDuplicationEmail = userRepository.existsByEmail(email);

        if (isDuplicationEmail) {
            logger.warn("중복된 Email 주소입니다.");
            throw new DuplicateException("ERR_2002", email);
        }

        return true;
    }
}
