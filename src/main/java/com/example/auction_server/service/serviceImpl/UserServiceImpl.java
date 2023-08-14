package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.enums.UserType;
import com.example.auction_server.exception.*;
import com.example.auction_server.mapper.UserMapper;
import com.example.auction_server.model.User;
import com.example.auction_server.repository.UserRepository;
import com.example.auction_server.service.UserService;
import com.example.auction_server.util.SessionUtil;
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

    private final int UPDATE_SUCCESS = 1;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        User user = userMapper.convertToEntity(userDTO);
        boolean isDuplicationUserId = this.checkDuplicationUserId(user.getUserId());
        boolean isDuplicationEmail = this.checkDuplicationEmail(user.getEmail());

        userDTO.setPassword(user.getPassword());
        if (isDuplicationUserId && isDuplicationEmail) {
            logger.warn("Id와 Email이 중복되었습니다.");
            throw new DuplicateException("ERR_2000", userDTO);
        } else if (isDuplicationUserId) {
            logger.warn("중복된 ID 입니다.");
            throw new DuplicateException("ERR_2001", userDTO);
        } else if (isDuplicationEmail) {
            logger.warn("중복된 Email 입니다.");
            throw new DuplicateException("ERR_2002", userDTO);
        } else {
            if (user.getUserType() != UserType.ADMIN) {
                user.setUserType(UserType.UNAUTHORIZED_USER);
                user.setCreateTime(LocalDateTime.now());
                User resultUser = userRepository.save(user);
                if (resultUser != null) {
                    UserDTO resultUserDTO = userMapper.convertToDTO(resultUser);
                    if (resultUserDTO == null) {
                        logger.warn("매핑에 실패했습니다.");
                        throw new NotMatchingException("ERR_1001", userDTO);
                    } else {
                        logger.info("유저 " + resultUser.getUserId() + "을 회원가입에 성공했습니다.");
                        return resultUserDTO;
                    }
                } else {
                    logger.warn("회원가입 오류. 재시도 해주세요.");
                    throw new AddException("ERR_1000", userDTO);
                }
            } else {
                logger.warn("ADMIN으로 회원가입할 수 없습니다. 재시도해주세요.");
                throw new UserAccessDeniedException("ERR_3000");
            }
        }
    }

    public UserDTO updateUserType(String userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            logger.warn("해당 유저를 찾을수 없습니다.");
            throw new NotMatchingException("ERR_4001", userId);
        } else {
            UserDTO resultUserDTO = userMapper.convertToDTO(optionalUser.get());
            resultUserDTO.setUserType(UserType.USER);
            User user = userMapper.convertToEntity(resultUserDTO);
            User resultUser = userRepository.save(user);
            if (resultUser != null) {
                logger.info("인증되었습니다.");
                return resultUserDTO;
            } else {
                logger.warn("타입을 수정하지 못했습니다. 다시 인증해주세요.");
                throw new UpdateException("ERR_5003");
            }
        }
    }

    @Override
    @Transactional
    public UserDTO registerAdminUser(UserDTO userDTO) {
        User user = userMapper.convertToEntity(userDTO);
        boolean isDuplicationUserId = this.checkDuplicationUserId(user.getUserId());

        if (isDuplicationUserId) {
            logger.warn("중복된 ID 입니다.");
            throw new DuplicateException("ERR_2001", user);
        } else {
            user.setUserType(UserType.ADMIN);
            user.setCreateTime(LocalDateTime.now());
            User resultUser = userRepository.save(user);
            if (resultUser != null) {
                UserDTO resultUserDTO = userMapper.convertToDTO(resultUser);
                if (resultUserDTO == null) {
                    logger.warn("매핑에 실패했습니다.");
                    throw new AddException("ERR_1001", user);
                } else {
                    logger.info("ADMIN 유저 " + resultUserDTO.getUserId() + "을 회원가입에 성공했습니다.");
                    return resultUserDTO;
                }
            } else {
                logger.warn("회원가입 오류. 재시도 해주세요.");
                throw new AddException("ERR_1000", user);
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
            throw new NotMatchingException("ERR_4000", userDTO);
        } else {
            this.insertSession(session, optionalUser.get().getId(), optionalUser.get().getUserType());
            User resultUser = optionalUser.get();
            resultUser.setLastLoginTime(LocalDateTime.now());
            resultUser = userRepository.save(resultUser);
            if (resultUser == null) {
                logger.warn("마지막 로그인 시간을 업데이트 하는데 실패하였습니다.");
                throw new UpdateException("ERR_5002");
            } else {
                UserDTO resultUserDTO = userMapper.convertToDTO(optionalUser.get());
                if (resultUserDTO == null) {
                    logger.warn("매핑에 실패했습니다.");
                    throw new NotMatchingException("ERR_1001", userDTO);
                } else {
                    logger.info("로그인에 성공하엿습니다.");
                    return resultUserDTO;
                }
            }
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
            throw new NotMatchingException("ERR_4002", id);
        }
        UserDTO resultUserDTO = userMapper.convertToDTO(optionalUser.get());
        if (resultUserDTO == null) {
            logger.warn("매핑에 실패했습니다.");
            throw new AddException("ERR_1001", id);
        } else {
            logger.info("회원 조회에 성공하였습니다.");
            return resultUserDTO;
        }
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userMapper.convertToEntity(userDTO);
        boolean isDuplicationEmail = this.checkDuplicationEmail(user.getEmail());
        boolean isEmailAndIdMatched = this.emailAndIdMatched(id, user.getEmail());

        if (isEmailAndIdMatched) {
            logger.warn("현재 Email 주소입니다.");
            throw new DuplicateException("ERR_2003", user.getEmail());
        } else if (isDuplicationEmail) {
            logger.warn("중복된 Email 주소입니다.");
            throw new DuplicateException("ERR_2002", user.getEmail());
        } else {
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                logger.warn("유저를 찾지 못했습니다.");
                throw new NotMatchingException("ERR_4001", userDTO);
            } else {
                User resultUser = optionalUser.get();
                resultUser.setPassword(userDTO.getPassword());
                resultUser.setName(userDTO.getName());
                resultUser.setPhoneNumber(userDTO.getPhoneNumber());
                resultUser.setEmail(userDTO.getEmail());

                resultUser = userRepository.save(resultUser);
                if (resultUser != null) {
                    UserDTO resultUserDTO = userMapper.convertToDTO(resultUser);
                    if (resultUserDTO != null) {
                        logger.info("회원정보를 성공적으로 수정했습니다.");
                        return resultUserDTO;
                    } else {
                        logger.warn("매핑에 실패했습니다.");
                        throw new AddException("ERR_1001", user);
                    }
                } else {
                    logger.warn("회원정보를 수정하지 못했습니다. 재시도 해주세요");
                    throw new UpdateException("ERR_5000");
                }
            }
        }
    }

    @Override
    @Transactional
    public void withDrawUser(Long id) {
        int updateCount = userRepository.withDrawUser(id, LocalDateTime.now());
        if (updateCount != UPDATE_SUCCESS) {
            logger.warn("회원 삭제 업데이트에 실패했습니다.");
            throw new UpdateException("ERR_5001");
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
            throw new LogoutFailedException("ERR_6000");
        }
    }

}
