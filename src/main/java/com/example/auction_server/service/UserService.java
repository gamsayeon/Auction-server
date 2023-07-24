package com.example.auction_server.service;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.enums.UserType;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

public interface UserService {

    UserDTO registerUser(UserDTO userDTO) throws AccessDeniedException;

    @Transactional
    UserDTO registerAdminUser(UserDTO userDTO);

    boolean duplicationUserIdCheck(String userId);

    boolean duplicationEmailCheck(String email);

    UserDTO loginUser(UserDTO userDTO, HttpSession session);

    void insertSession(HttpSession session, Long id, UserType userType);

    UserDTO selectUser(Long userNumber);

    UserDTO updateUser(Long userNumber, UserDTO userDTO);

    void deleteUser(Long userNumber);
}
