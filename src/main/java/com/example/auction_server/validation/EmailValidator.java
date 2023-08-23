package com.example.auction_server.validation;

import com.example.auction_server.exception.DuplicateException;
import com.example.auction_server.repository.UserRepository;
import com.example.auction_server.validation.annotation.EmailValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {
    private final UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(EmailValidator.class);


    public EmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        boolean isDuplicationEmail = userRepository.existsByEmail(email);

        if (isDuplicationEmail) {
            logger.warn("중복된 Email 주소입니다.");
            throw new DuplicateException("ERR_2002", email);
        }

        return true;
    }
}
