package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.service.serviceImpl.EmailServiceImpl;
import com.example.auction_server.service.serviceImpl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RestController
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final EmailServiceImpl emailService;

    private final Logger logger = LogManager.getLogger(UserController.class);

    @PostMapping
    public ResponseEntity<CommonResponse<UserDTO>> registerUser(@RequestBody @Validated({UserDTO.SignUp.class}) UserDTO userDTO,
                                                                HttpServletRequest request) {
        logger.debug("회원을 가입합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "회원가입에 성공했습니다.",
                request.getRequestURI(), userService.registerUser(userDTO));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<CommonResponse<UserDTO>> verifyAccount(@RequestParam("token") String token,
                                                                 HttpServletRequest request) {
        // 토큰을 이용한 회원 가입 인증 처리
        String id = emailService.verifyEmail(token);
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "이메일 인증에 성공했습니다.",
                request.getRequestURI(), userService.updateUserType(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<CommonResponse<UserDTO>> registerAdminUser(@RequestBody @Validated({UserDTO.AdminSignUp.class}) UserDTO userDTO,
                                                                     HttpServletRequest request) {
        logger.debug("ADMIN 회원을 가입합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "ADMIN 유저 회원가입에 성공했습니다.",
                request.getRequestURI(), userService.registerUser(userDTO));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<UserDTO>> loginUser(@RequestBody @Validated({UserDTO.Login.class}) UserDTO userDTO,
                                                             HttpSession session, HttpServletRequest request) {
        logger.debug("유저 로그인합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "로그인에 성공했습니다.",
                request.getRequestURI(), userService.loginUser(userDTO, session));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-email")
    @LoginCheck(types = LoginCheck.LoginType.UNAUTHORIZED_USER)
    public ResponseEntity<CommonResponse<String>> sendEmail(Long id, HttpServletRequest request) {
        logger.debug("인증 이메일을 재전송 합니다.");
        UserDTO resultUserDTO = userService.selectUser(id);
        emailService.sendToUser(resultUserDTO.getUserId(), resultUserDTO.getEmail());
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "이메일 재전송에 성공했습니다.",
                request.getRequestURI(), resultUserDTO.getEmail() + "Email을 확인해주세요");
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @GetMapping
    public ResponseEntity<CommonResponse<UserDTO>> selectUser(Long loginId, HttpServletRequest request) {
        logger.debug("유저를 조회합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "회원을 조회하는데 성공했습니다.",
                request.getRequestURI(), userService.selectUser(loginId));
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @PatchMapping
    public ResponseEntity<CommonResponse<UserDTO>> updateUser(Long loginId, @RequestBody @Validated({UserDTO.UpdateUser.class}) UserDTO userDTO,
                                                              HttpServletRequest request) {
        logger.debug("유저를 수정합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "회원 정보를 수정했습니다.",
                request.getRequestURI(), userService.updateUser(loginId, userDTO));
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.ADMIN})
    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponse<UserDTO>> updateUserByAdmin(Long loginId, @PathVariable("id") Long id,
                                                                     @RequestBody @Validated({UserDTO.UpdateUser.class}) UserDTO userDTO,
                                                                     HttpServletRequest request) {
        logger.debug("유저를 수정합니다.");
        CommonResponse<UserDTO> response = new CommonResponse<>("SUCCESS", "ADMIN이 해당하는 회원의 정보를 수정했습니다.",
                request.getRequestURI(), userService.updateUser(id, userDTO));
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.UNAUTHORIZED_USER, LoginCheck.LoginType.ADMIN})
    @PatchMapping("/withdraw")
    public ResponseEntity<CommonResponse<String>> withDrawUser(Long loginId, HttpSession session,
                                                               HttpServletRequest request) {
        logger.debug("유저를 탈퇴합니다.");
        userService.withDrawUser(loginId);
        userService.logoutUser(session);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "해당 유저를 탈퇴했습니다.",
                request.getRequestURI(), loginId + " User delete Success");
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.ADMIN})
    @PatchMapping("/withdraw/{id}")
    public ResponseEntity<CommonResponse<String>> withDrawByAdmin(Long loginId, @PathVariable("id") Long id,
                                                                  HttpServletRequest request) {
        logger.debug("ADMIN 계정으로 이상유저를 탈퇴합니다.");
        userService.withDrawUser(id);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "ADMIN이 이상유저를 탈퇴했습니다.",
                request.getRequestURI(), id + "User delete Success");
        return ResponseEntity.ok(response);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logoutUser(Long loginId, HttpSession session,
                                                             HttpServletRequest request) {
        logger.debug("유저 로그아웃합니다.");
        userService.logoutUser(session);
        CommonResponse<String> response = new CommonResponse<>("SUCCESS", "회원 로그아웃 했습니다.",
                request.getRequestURI(), loginId + "logout");
        return ResponseEntity.ok(response);
    }
}
