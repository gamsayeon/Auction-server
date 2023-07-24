package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.enums.UserType;
import com.example.auction_server.exception.UserAccessDeniedException;
import com.example.auction_server.exception.NotMatchingException;
import com.example.auction_server.mapper.UserMapper;
import com.example.auction_server.model.User;
import com.example.auction_server.repository.UserRepository;
import com.example.auction_server.service.UserService;
import com.example.auction_server.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        if (userDTO.getUserType() != UserType.ADMIN) {
            User user = userMapper.convertToEntity(userDTO);
            user.setCreateTime(LocalDateTime.now());
            return userMapper.convertToDTO(userRepository.save(user));
        } else {
            throw new UserAccessDeniedException("ADMIN 으로 회원가입할 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public UserDTO registerAdminUser(UserDTO userDTO) {
        User user = userMapper.convertToEntity(userDTO);
        user.setUserType(UserType.ADMIN);
        user.setCreateTime(LocalDateTime.now());
        return userMapper.convertToDTO(userRepository.save(user));
    }

    @Override
    public boolean duplicationUserIdCheck(String userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        return optionalUser.isPresent();
    }

    @Override
    public boolean duplicationEmailCheck(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isPresent();
    }

    @Override
    public UserDTO loginUser(UserDTO userDTO, HttpSession session) {
        User user = userMapper.convertToEntity(userDTO);
        Optional<User> optionalUser = userRepository.findByUserIdAndPassword(user.getUserId(), user.getPassword());
        if (optionalUser.isEmpty()) {
            throw new NotMatchingException("로그인에 실패 했습니다.");
        }
        this.insertSession(session, optionalUser.get().getId(), optionalUser.get().getUserType());
        return userMapper.convertToDTO(optionalUser.get());
    }

    @Override
    public void insertSession(HttpSession session, Long id, UserType userType) {
        SessionUtil.setLoginSession(session, id, userType);
    }

    @Override
    public UserDTO selectUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return userMapper.convertToDTO(optionalUser.get());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(id);
        User resultUser = optionalUser.get();
        if (userDTO.getPassword() != null) {
            resultUser.setPassword(userDTO.getPassword());
        }
        if (userDTO.getName() != null) {
            resultUser.setName(userDTO.getName());
        }
        if (userDTO.getPhoneNumber() != null) {
            resultUser.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getEmail() != null && !this.duplicationEmailCheck(userDTO.getEmail())) {
            resultUser.setEmail(userDTO.getEmail());
        }
        return userMapper.convertToDTO(userRepository.save(resultUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteUser(id, LocalDateTime.now());
    }


}
