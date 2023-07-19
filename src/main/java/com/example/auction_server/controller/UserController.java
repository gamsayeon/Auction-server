package com.example.auction_server.controller;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.service.serviceImpl.UserServiceImpl;
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
        UserDTO resultUserDTO = userService.registerUser(userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody UserDTO userDTO) {
        UserDTO resultUserDTO = userService.loginUser(userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> selectUser(@PathVariable("id") Long id) {
        UserDTO resultUserDTO = userService.selectUser(id);
        return ResponseEntity.ok(resultUserDTO);
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long id,@RequestBody UserDTO userDTO) {
        UserDTO resultUserDTO = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(resultUserDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(id + "User delete Success");
    }
}
