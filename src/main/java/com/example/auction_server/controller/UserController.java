package com.example.auction_server.controller;

import com.example.auction_server.aop.LoginCheck;
import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.exception.DuplicateException;
import com.example.auction_server.service.serviceImpl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RestController
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        boolean isDuplicationUserId = userService.duplicationUserIdCheck(userDTO.getUserId());
        boolean isDuplicationEmail = userService.duplicationEmailCheck(userDTO.getEmail());

        if(isDuplicationUserId){
            throw new DuplicateException("중복된 ID 입니다.");
        } else if(isDuplicationEmail){
            throw new DuplicateException("중복된 Email 입니다.");
        } else {
            UserDTO resultUserDTO = userService.registerUser(userDTO);
            return ResponseEntity.ok(resultUserDTO);
        }
    }

    @PostMapping("/admin/register")
    public ResponseEntity<UserDTO> registerAdminUser(@RequestBody UserDTO userDTO) {
        boolean isDuplicationUserId = userService.duplicationUserIdCheck(userDTO.getUserId());
        boolean isDuplicationEmail = userService.duplicationEmailCheck(userDTO.getEmail());

        if(isDuplicationUserId){
            throw new DuplicateException("중복된 ID 입니다.");
        } else if(isDuplicationEmail){
            throw new DuplicateException("중복된 Email 입니다.");
        } else {
            UserDTO resultUserDTO = userService.registerAdminUser(userDTO);
            return ResponseEntity.ok(resultUserDTO);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody UserDTO userDTO, HttpSession session) {
        UserDTO resultUserDTO = userService.loginUser(userDTO, session);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_USER, LoginCheck.LoginType.LOGIN_ADMIN})
    @GetMapping
    public ResponseEntity<UserDTO> selectUser(Long loginId) {
        UserDTO resultUserDTO = userService.selectUser(loginId);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_USER, LoginCheck.LoginType.LOGIN_ADMIN})
    @PatchMapping
    public ResponseEntity<UserDTO> updateUser(Long loginId, @RequestBody UserDTO userDTO) {
        UserDTO resultUserDTO = userService.updateUser(loginId, userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_ADMIN})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserByAdmin(Long loginId, @PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(id + "User delete Success");
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_USER})
    @PatchMapping("/delete")
    public ResponseEntity<String> deleteUser(Long loginId) {
        userService.deleteUser(loginId);
        return ResponseEntity.ok(loginId + "User delete Success");
    }

    @LoginCheck(types = {LoginCheck.LoginType.LOGIN_USER, LoginCheck.LoginType.LOGIN_ADMIN})
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpSession session){
        return ResponseEntity.ok("logout");
    }
}
