package com.example.auction_server.validation;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.enums.UserType;
import com.example.auction_server.exception.DuplicateException;
import com.example.auction_server.exception.UserAccessDeniedException;
import com.example.auction_server.mapper.UserMapper;
import com.example.auction_server.model.User;
import com.example.auction_server.repository.UserRepository;
import com.example.auction_server.validation.annotation.UniqueUser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniqueUserValidator implements ConstraintValidator<UniqueUser, UserDTO> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Logger logger = LogManager.getLogger(UniqueUserValidator.class);


    public UniqueUserValidator(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void initialize(UniqueUser constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext context) {
        User user = userMapper.convertToEntity(userDTO);

        if (userDTO == null) {
            return false;
        }

        boolean isDuplicationUserId = userRepository.existsByUserId(user.getUserId());
        boolean isDuplicationEmail = userRepository.existsByEmail(user.getEmail());

        if (isDuplicationUserId && isDuplicationEmail) {
            logger.warn("Id와 Email이 중복되었습니다.");
            throw new DuplicateException("ERR_2000", user);
        } else if (isDuplicationUserId) {
            logger.warn("중복된 ID 입니다.");
            throw new DuplicateException("ERR_2001", user);
        } else if (isDuplicationEmail) {
            logger.warn("중복된 Email 입니다.");
            throw new DuplicateException("ERR_2002", user);
        }

        if (user.getUserType() == UserType.ADMIN) {
            logger.warn("ADMIN으로 회원가입할 수 없습니다. 재시도해주세요.");
            throw new UserAccessDeniedException("ERR_3000");
        }

        return true;
    }
}

