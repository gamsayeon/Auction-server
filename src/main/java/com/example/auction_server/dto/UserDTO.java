package com.example.auction_server.dto;

import com.example.auction_server.enums.UserType;
import com.example.auction_server.validation.annotation.IsAdminValidation;
import com.example.auction_server.validation.annotation.isExistEmailValidation;
import com.example.auction_server.validation.annotation.isExistUserIdlValidation;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotBlank(groups = {SignUp.class, AdminSignUp.class, Login.class})
    @isExistUserIdlValidation(groups = {SignUp.class, AdminSignUp.class})
    private String userId;

    @NotBlank(groups = {SignUp.class, AdminSignUp.class, UpdateUser.class, Login.class})
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{3,20}$",
            message = "비밀번호는 3자기 이상 20자리 이하로 숫자와 영어, 특수기호(!@#$%^&*)를 각 하나이상 포함해주세요")
    private String password;

    @NotBlank(groups = {SignUp.class, AdminSignUp.class, UpdateUser.class})
    private String name;

    @NotBlank(groups = {SignUp.class, UpdateUser.class})
    @Size(max = 20)
    @Pattern(regexp = "[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(groups = {SignUp.class, UpdateUser.class})
    @isExistEmailValidation(groups = {SignUp.class, UpdateUser.class})
    private String email;

    @NotNull(groups = {SignUp.class, AdminSignUp.class})
    @IsAdminValidation(groups = {SignUp.class})
    private UserType userType;

    private LocalDateTime createTime;

    public interface Login {
    }

    public interface SignUp {
    }

    public interface AdminSignUp {
    }

    public interface UpdateUser {
    }
}
