package com.example.auction_server.repository;

import com.example.auction_server.enums.UserType;
import com.example.auction_server.model.User;
import com.example.auction_server.util.Sha256Encrypt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    public User generateTestUser() {
        User user = new User();
        user.setUserId("testUserId");
        user.setPassword(Sha256Encrypt.encrypt("testPassword"));
        user.setName("testName");
        user.setPhoneNumber("010-1234-5678");
        user.setEmail("test@example.com");
        user.setUserType(UserType.UNAUTHORIZED_USER);
        user.setCreateTime(LocalDateTime.now());
        return user;
    }

    @Test
    void findById() {
        User user = this.generateTestUser();
        userRepository.save(user);
        User findUser = userRepository.findByUserId("testUserId").orElse(null);

        assertNotNull(findUser);
        assertEquals("test@example.com", findUser.getEmail());
    }

    @Test
    void findByIdAndUpdateTimeIsNull() {
        User user = this.generateTestUser();
        Long lastId = userRepository.save(user).getId();

        User findUser = userRepository.findByIdAndUpdateTimeIsNull(lastId).orElse(null);

        assertNotNull(findUser);
        assertEquals("test@example.com", findUser.getEmail());
    }

    @Test
    void findByUserId() {
        User user = this.generateTestUser();
        String lastUserId = userRepository.save(user).getUserId();

        User findUser = userRepository.findByUserId(lastUserId).orElse(null);

        assertNotNull(findUser);
        assertEquals("test@example.com", findUser.getEmail());
    }

    @Test
    void findByUserIdAndPassword() {
        User user = this.generateTestUser();
        User savedUser = userRepository.save(user);

        User loginUser = userRepository.findByUserIdAndPassword(savedUser.getUserId(), savedUser.getPassword()).orElse(null);

        assertNotNull(loginUser);
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void existsByEmail() {
        User user = this.generateTestUser();
        User savedUser = userRepository.save(user);

        boolean existsEmail = userRepository.existsByEmail(savedUser.getEmail());

        assertNotNull(existsEmail);
        assertEquals(true, existsEmail);

    }

    @Test
    void existsByUserId() {
        User user = this.generateTestUser();
        User savedUser = userRepository.save(user);

        boolean existsUserId = userRepository.existsByUserId(savedUser.getUserId());

        assertNotNull(existsUserId);
        assertEquals(true, existsUserId);
    }

    @Test
    void findEmailById() {
        User user = this.generateTestUser();
        User savedUser = userRepository.save(user);

        String email = userRepository.findEmailById(savedUser.getId());

        assertNotNull(email);
        assertEquals("test@example.com", email);
    }
}