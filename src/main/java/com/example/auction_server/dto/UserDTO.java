package com.example.auction_server.dto;

import com.example.auction_server.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private UserType userType;
    private LocalDateTime createTime;
}
