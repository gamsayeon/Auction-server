package com.example.auction_server.dto;

import com.example.auction_server.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(groups = { Login.class, Signup.class, AdminSignup.class})
    private String userId;

    @NotBlank(groups = { Login.class, Signup.class, AdminSignup.class })
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{3,20}$",
            message = "비밀번호는 3자기 이상 20자리 이하로 숫자와 영어, 특수기호(!@#$%^&*)를 각 하나이상 포함해주세요")
    private String password;

    @NotBlank(groups = { Signup.class, AdminSignup.class })
    private String name;

    @NotBlank(groups = { Signup.class })
    @Size(max = 20)
    @Pattern(regexp = "[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}", message = "올바른 전화번호 형식이 아닙니다.")
    private String phoneNumber;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(groups = { Signup.class })
    private String email;

    private UserType userType;

    private LocalDateTime createTime;

    public interface Login {
    }

    public interface Signup {
    }

    public interface AdminSignup {
    }
}
