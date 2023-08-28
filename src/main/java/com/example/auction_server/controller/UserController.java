package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.service.serviceImpl.EmailServiceImpl;
import com.example.auction_server.service.serviceImpl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
@Log4j2
public class UserController {

    private final UserServiceImpl userService;
    private final EmailServiceImpl emailService;

    private final Logger logger = LogManager.getLogger(UserController.class);

    public UserController(UserServiceImpl userService, EmailServiceImpl emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Validated({UserDTO.Signup.class}) UserDTO userDTO) {
        logger.debug("회원을 가입합니다.");
        UserDTO resultUserDTO = userService.registerUser(userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<UserDTO> verifyAccount(@RequestParam("token") String token) {
        // 토큰을 이용한 회원 가입 인증 처리
        String id = emailService.verifyEmail(token);
        UserDTO resultUserDTO = userService.updateUserType(id);
        return ResponseEntity.ok(resultUserDTO);
    }

    @PostMapping("/admin")
    public ResponseEntity<UserDTO> registerAdminUser(@RequestBody @Validated({UserDTO.AdminSignup.class}) UserDTO userDTO) {
        logger.debug("ADMIN 회원을 가입합니다.");
        UserDTO resultUserDTO = userService.registerUser(userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody @Validated({UserDTO.Login.class}) UserDTO userDTO, HttpSession session) {
        logger.debug("유저 로그인합니다.");
        UserDTO resultUserDTO = userService.loginUser(userDTO, session);
        return ResponseEntity.ok(resultUserDTO);
    }

    @PostMapping("/send-email")
    @LoginCheck(types = LoginCheck.LoginType.UNAUTHORIZED_USER)
    public ResponseEntity<String> sendEmail(Long id) {
        logger.debug("인증 이메일을 재전송 합니다.");
        UserDTO resultUserDTO = userService.selectUser(id);
        emailService.sendToUser(resultUserDTO.getUserId(), resultUserDTO.getEmail());
        return ResponseEntity.ok(resultUserDTO.getEmail() + "Email을 확인해주세요");
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @GetMapping
    public ResponseEntity<UserDTO> selectUser(Long loginId) {
        logger.debug("유저를 조회합니다.");
        UserDTO resultUserDTO = userService.selectUser(loginId);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @PatchMapping
    public ResponseEntity<UserDTO> updateUser(Long loginId, @RequestBody @Validated({UserDTO.UpdateUser.class}) UserDTO userDTO) {
        logger.debug("유저를 수정합니다.");
        UserDTO resultUserDTO = userService.updateUser(loginId, userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.ADMIN})
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserByAdmin(Long loginId, @PathVariable("id") Long id,
                                                     @RequestBody @Validated({UserDTO.UpdateUser.class}) UserDTO userDTO) {
        logger.debug("유저를 수정합니다.");
        UserDTO resultUserDTO = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.UNAUTHORIZED_USER, LoginCheck.LoginType.ADMIN})
    @PatchMapping("/withdraw")
    public ResponseEntity<String> withDrawUser(Long loginId, HttpSession session) {
        logger.debug("유저를 탈퇴합니다.");
        userService.withDrawUser(loginId);
        userService.logoutUser(session);
        return ResponseEntity.ok(loginId + " User delete Success");
    }

    @LoginCheck(types = {LoginCheck.LoginType.ADMIN})
    @PatchMapping("/withdraw/{id}")
    public ResponseEntity<String> withDrawByAdmin(Long loginId, @PathVariable("id") Long id) {
        logger.debug("ADMIN 계정으로 이상유저를 탈퇴합니다.");
        userService.withDrawUser(id);
        return ResponseEntity.ok(id + "User delete Success");
    }

    @LoginCheck(types = {LoginCheck.LoginType.USER, LoginCheck.LoginType.ADMIN,
            LoginCheck.LoginType.UNAUTHORIZED_USER})
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(Long loginId, HttpSession session) {
        logger.debug("유저 로그아웃합니다.");
        userService.logoutUser(session);
        return ResponseEntity.ok("logout");
    }
}
