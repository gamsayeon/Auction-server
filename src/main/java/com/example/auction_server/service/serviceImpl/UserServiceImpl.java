package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.mapper.UserMapper;
import com.example.auction_server.model.User;
import com.example.auction_server.repository.UserRepository;
import com.example.auction_server.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        User user = userMapper.convertToEntity(userDTO);
        user.setCreateTime(LocalDateTime.now());
        return userMapper.convertToDTO(userRepository.save(user));
    }

    @Override
    public UserDTO loginUser(UserDTO userDTO){
        User user = userMapper.convertToEntity(userDTO);
        Optional<User> optionalUser = userRepository.findByUserIdAndPassword(user.getUserId(), user.getPassword());
        return userMapper.convertToDTO(optionalUser.get());
    }
    @Override
    public UserDTO selectUser(Long id){
        Optional<User> optionalUser = userRepository.findById(id);
        return userMapper.convertToDTO(optionalUser.get());
    }
    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO){
        User user = userMapper.convertToEntity(userDTO);
        Optional<User> optionalUser = userRepository.findById(id);
        user.setId(optionalUser.get().getId());
        user.setCreateTime(optionalUser.get().getCreateTime());
        return userMapper.convertToDTO(userRepository.save(user));
    }
    @Override
    @Transactional
    public void deleteUser(Long id){
        userRepository.deleteUser(id, LocalDateTime.now());
    }


}
