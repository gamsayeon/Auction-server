package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.service.serviceImpl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RestController
@Log4j2
public class UserController {

    private final UserServiceImpl userService;

    private final Logger logger = LogManager.getLogger(UserController.class);

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Validated({UserDTO.Signup.class}) UserDTO userDTO) {
        logger.debug("회원을 가입합니다.");
        UserDTO resultUserDTO = userService.registerUser(userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @PostMapping("/admin")
    public ResponseEntity<UserDTO> registerAdminUser(@RequestBody @Validated({UserDTO.AdminSignup.class}) UserDTO userDTO) {
        logger.debug("ADMIN 회원을 가입합니다.");
        UserDTO resultUserDTO = userService.registerAdminUser(userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody @Validated({UserDTO.Login.class}) UserDTO userDTO, HttpSession session) {
        logger.debug("유저 로그인합니다.");
        UserDTO resultUserDTO = userService.loginUser(userDTO, session);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_SELLER, LoginCheck.LoginType.LOGIN_BUYER,
            LoginCheck.LoginType.LOGIN_ADMIN, LoginCheck.LoginType.WAITING_EMAIL})
    @GetMapping
    public ResponseEntity<UserDTO> selectUser(Long loginId) {
        logger.debug("유저를 조회합니다.");
        UserDTO resultUserDTO = userService.selectUser(loginId);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_SELLER, LoginCheck.LoginType.LOGIN_BUYER,
            LoginCheck.LoginType.LOGIN_ADMIN, LoginCheck.LoginType.WAITING_EMAIL})
    @PatchMapping
    public ResponseEntity<UserDTO> updateUser(Long loginId, @RequestBody UserDTO userDTO) {
        logger.debug("유저를 수정합니다.");
        UserDTO resultUserDTO = userService.updateUser(loginId, userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_SELLER, LoginCheck.LoginType.LOGIN_BUYER,
            LoginCheck.LoginType.WAITING_EMAIL})
    @PatchMapping("/withdraw")
    public ResponseEntity<String> withDrawUser(Long loginId, HttpSession session) {
        logger.debug("유저를 탈퇴합니다.");
        userService.withDrawUser(loginId);
        userService.logoutUser(session);
        return ResponseEntity.ok(loginId + "User delete Success");
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_ADMIN})
    @PatchMapping("/withdraw/{id}")
    public ResponseEntity<String> withDrawByAdmin(Long loginId, @PathVariable("id") Long id) {
        logger.debug("ADMIN 계정으로 이상유저를 탈퇴합니다.");
        userService.withDrawUser(id);
        return ResponseEntity.ok(id + "User delete Success");
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_SELLER, LoginCheck.LoginType.LOGIN_BUYER,
            LoginCheck.LoginType.LOGIN_ADMIN, LoginCheck.LoginType.WAITING_EMAIL})
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(Long loginId, HttpSession session) {
        logger.debug("유저 로그아웃합니다.");
        userService.logoutUser(session);
        return ResponseEntity.ok("logout");
    }
}
