package com.example.auction_server.service;

import com.example.auction_server.dto.UserDTO;

public interface UserService {

    UserDTO registerUser(UserDTO userDTO);

    UserDTO loginUser(UserDTO userDTO);

    UserDTO selectUser(Long userNumber);

    UserDTO updateUser(Long userNumber, UserDTO userDTO);

    void deleteUser(Long userNumber);
}
