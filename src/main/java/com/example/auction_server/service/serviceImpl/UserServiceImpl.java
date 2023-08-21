package com.example.auction_server.service.serviceImpl;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.enums.UserType;
import com.example.auction_server.exception.AddException;
import com.example.auction_server.exception.LogoutFailedException;
import com.example.auction_server.exception.NotMatchingException;
import com.example.auction_server.exception.UpdateException;
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

    /***
     * @Transactional
     * - 어노테이션을 사용하면 데이터베이스 작업들이 하나의 트랜잭션으로 묶이게 됩니다.
     * - 트랜잭션 내에서 예외가 발생하면 해당 트랜잭션이 롤백이 됩니다.
     * - 예외가 발생한 경우에도 데이터 일관성을 보장
     *
     * 1. 메서드에 Transactional 어노테이션이 적용
     * 2-1. 예외 발생시
     * 3-1. 예외가 발생시 DB CRUD 내용이 적용되지 않음(ROLLBACK)
     * 2-2. 예외가 발생하지 않을시
     * 3-2. DB CRUD 내용이 적용(COMMIT)
     *
     * ex) 회원가입시 ID 유효성 검사를 통해 중복되어 있다면 Create 하지 않게 할 수 있다.
     * 또는 회원가입시 DB Connection exception 이 발생한다면 ROLLBACK 으로 인해 추가 되지 않는다.
     * @param userDTO
     * @return
     */
    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO, String userType) {
        User user = userMapper.convertToEntity(userDTO);

        if (userType == "USER") {
            user.setUserType(UserType.UNAUTHORIZED_USER);
        } else if (userType == "ADMIN") {
            user.setUserType(UserType.ADMIN);
        }
        user.setCreateTime(LocalDateTime.now());
        User resultUser = userRepository.save(user);
        if (resultUser != null) {
            UserDTO resultUserDTO = userMapper.convertToDTO(resultUser);
            if (resultUserDTO == null) {
                logger.warn("매핑에 실패했습니다.");
                throw new NotMatchingException("ERR_1001", user);
            } else {
                logger.info("유저 " + resultUser.getUserId() + "을 회원가입에 성공했습니다.");
                return resultUserDTO;
            }
        } else {
            logger.warn("회원가입 오류. 재시도 해주세요.");
            throw new AddException("ERR_1000", user);
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
    public UserDTO loginUser(UserDTO userDTO, HttpSession session) {
        User user = userMapper.convertToEntity(userDTO);
        Optional<User> optionalUser = userRepository.findByUserIdAndPassword(user.getUserId(), user.getPassword());

        if (optionalUser.isEmpty()) {
            logger.warn("로그인에 실패 했습니다. 아이디 및 비밀번호 확인 후 재시도 해주세요.");
            throw new NotMatchingException("ERR_4000", user);
        } else {
            User resultUser = optionalUser.get();
            this.insertSession(session, resultUser.getId(), resultUser.getUserType());
            resultUser.setLastLoginTime(LocalDateTime.now());
            resultUser = userRepository.save(resultUser);
            if (resultUser == null) {
                logger.warn("마지막 로그인 시간을 업데이트 하는데 실패하였습니다.");
                throw new UpdateException("ERR_5002");
            } else {
                UserDTO resultUserDTO = userMapper.convertToDTO(optionalUser.get());
                if (resultUserDTO == null) {
                    logger.warn("매핑에 실패했습니다.");
                    throw new NotMatchingException("ERR_1001", user);
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

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            logger.warn("유저를 찾지 못했습니다.");
            throw new NotMatchingException("ERR_4001", id);
        } else {
            User resultUser = optionalUser.get();
            resultUser.setPassword(user.getPassword());
            resultUser.setName(user.getName());
            resultUser.setPhoneNumber(user.getPhoneNumber());
            resultUser.setEmail(user.getEmail());

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
