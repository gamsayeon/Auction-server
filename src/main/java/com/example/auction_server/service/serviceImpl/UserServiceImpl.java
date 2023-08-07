package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.enums.UserType;
import com.example.auction_server.exception.*;
import com.example.auction_server.mapper.UserMapper;
import com.example.auction_server.model.User;
import com.example.auction_server.repository.UserRepository;
import com.example.auction_server.service.UserService;
import com.example.auction_server.util.SessionUtil;
import com.example.auction_server.util.Sha256Encrypt;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        boolean isDuplicationUserId = this.checkDuplicationUserId(userDTO.getUserId());
        boolean isDuplicationEmail = this.checkDuplicationEmail(userDTO.getEmail());

        if (isDuplicationUserId) {
            logger.warn("중복된 ID 입니다.");
            throw new DuplicateException("중복된 ID 입니다.");
        } else if (isDuplicationEmail) {
            logger.warn("중복된 Email 입니다.");
            throw new DuplicateException("중복된 Email 입니다.");
        } else {
            if (userDTO.getUserType() != UserType.ADMIN) {
                User user = userMapper.convertToEntity(userDTO);
                user.setUserType(UserType.WAITING_EMAIL);
                user.setCreateTime(LocalDateTime.now());
                UserDTO resultUser = userMapper.convertToDTO(userRepository.save(user));
                if (resultUser == null) {
                    logger.warn("회원가입 오류. 재시도 해주세요.");
                    throw new AddException("회원가입에 성공하지 못했습니다. 다시 시도해주세요");
                } else {
                    logger.info("유저 " + resultUser.getUserId() + "을 회원가입에 성공했습니다.");
                    return resultUser;
                }
            } else {
                logger.warn("ADMIN으로 회원가입할 수 없습니다. 재시도해주세요.");
                throw new UserAccessDeniedException("ADMIN 으로 회원가입할 수 없습니다.");
            }
        }
    }

    @Override
    @Transactional
    public UserDTO registerAdminUser(UserDTO userDTO) {
        boolean isDuplicationUserId = this.checkDuplicationUserId(userDTO.getUserId());
        boolean isDuplicationEmail = this.checkDuplicationEmail(userDTO.getEmail());

        if (isDuplicationUserId) {
            logger.warn("중복된 ID 입니다.");
            throw new DuplicateException("중복된 ID 입니다.");
        } else if (isDuplicationEmail) {
            logger.warn("중복된 Email 입니다.");
            throw new DuplicateException("중복된 Email 입니다.");
        } else {
            User user = userMapper.convertToEntity(userDTO);
            user.setUserType(UserType.ADMIN);
            user.setCreateTime(LocalDateTime.now());
            UserDTO resultUser = userMapper.convertToDTO(userRepository.save(user));
            if (resultUser == null) {
                logger.warn("회원가입 오류. 재시도 해주세요.");
                throw new AddException("회원가입에 성공하지 못했습니다. 다시 시도해주세요");
            } else {
                logger.info("ADMIN 유저 " + resultUser.getUserId() + "을 회원가입에 성공했습니다.");
                return resultUser;
            }
        }
    }

    @Override
    public boolean checkDuplicationUserId(String userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        return optionalUser.isPresent();
    }

    @Override
    public boolean checkDuplicationEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.isPresent();
    }

    @Override
    public boolean emailAndIdMatched(Long id, String email) {
        Optional<User> optionalUser = userRepository.findByIdAndEmail(id, email);
        return optionalUser.isPresent();
    }

    @Override
    @Transactional
    public UserDTO loginUser(UserDTO userDTO, HttpSession session) {
        User user = userMapper.convertToEntity(userDTO);
        Optional<User> optionalUser = userRepository.findByUserIdAndPassword(user.getUserId(), user.getPassword());
        if (optionalUser.isEmpty()) {
            logger.warn("로그인에 실패 했습니다. 아이디 및 비밀번호 확인 후 재시도 해주세요.");
            throw new NotMatchingException("로그인에 실패 했습니다.");
        } else {
            this.insertSession(session, optionalUser.get().getId(), optionalUser.get().getUserType());
            User resultUser = optionalUser.get();
            resultUser.setLastLoginTime(LocalDateTime.now());
            userRepository.save(resultUser);
            return userMapper.convertToDTO(optionalUser.get());
        }
    }

    @Override
    public void insertSession(HttpSession session, Long id, UserType userType) {
        SessionUtil.setLoginSession(session, id, userType);
    }

    @Override
    public UserDTO selectUser(Long id) {
        Optional<User> optionalUser = userRepository.findByIdWithNullUpdateTime(id);
        if (optionalUser.isEmpty()) {
            logger.warn("회원 조회에 실패 했습니다. 재시도 해주세요.");
            throw new NotMatchingException("회원 조회에 실패했습니다.");
        }
        return userMapper.convertToDTO(optionalUser.get());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        boolean isDuplicationEmail = this.checkDuplicationEmail(userDTO.getEmail());
        boolean isEmailAndIdMatched = this.emailAndIdMatched(id, userDTO.getEmail());

        if (isEmailAndIdMatched) {
            logger.warn("현재 Email 주소입니다.");
            throw new DuplicateException("현재 Email 주소 입니다. 다른 Email을 입력해주세요.");
        } else if (isDuplicationEmail) {
            logger.warn("중복된 Email 주소입니다.");
            throw new DuplicateException("중복된 Email 입니다. 다른 Email을 입력해주세요.");
        }else{
            Optional<User> optionalUser = userRepository.findById(id);
            User resultUser = optionalUser.get();
            if (userDTO.getPassword() != null) {
                resultUser.setPassword(Sha256Encrypt.encrypt(userDTO.getPassword()));
            }
            if (userDTO.getName() != null) {
                resultUser.setName(userDTO.getName());
            }
            if (userDTO.getPhoneNumber() != null) {
                resultUser.setPhoneNumber(userDTO.getPhoneNumber());
            }
            if (userDTO.getEmail() != null) {
                resultUser.setEmail(userDTO.getEmail());
            }
            UserDTO resultUserDTO = userMapper.convertToDTO(userRepository.save(resultUser));
            if (resultUserDTO != null) {
                logger.info("회원정보를 성공적으로 수정했습니다.");
                return resultUserDTO;
            } else {
                logger.warn("회원정보를 수정하지 못했습니다. 재시도 해주세요");
                throw new UpdateException("회원정보를 수정하지 못했습니다.");
            }
        }
    }

    @Override
    @Transactional
    public void withDrawUser(Long id) {
        int updateCount = userRepository.withDrawUser(id, LocalDateTime.now());
        if (updateCount != 1) {
            logger.warn("회원 삭제 업데이트에 실패했습니다.");
            throw new UpdateException("회원 삭제에 실패했습니다.");
        } else {
            logger.info("회원 삭제 업데이트에 성공했습니다.");
        }
    }

    @Override
    public void logoutUser(HttpSession session) {
        try {
            SessionUtil.clear(session);
            logger.info("로그아웃에 성공했습니다.");
        } catch (Exception e) {
            logger.warn("로그아웃에 실패했습니다.");
            throw new LogoutFailedException("로그아웃에 실패했습니다.");
        }
    }


}
