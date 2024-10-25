package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.config.TestDatabaseConfig;
import com.ccommit.auction_server.config.TestElasticsearchConfig;
import com.ccommit.auction_server.config.testDataInitializer.TestDataInitializer;
import com.ccommit.auction_server.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Unit 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestDatabaseConfig.class, TestElasticsearchConfig.class, TestDataInitializer.class})
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestDataInitializer testDataInitializer;

    private User savedUser;

    @BeforeEach
    void generateTestUser() {
        //given
        savedUser = testDataInitializer.getSavedUser();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test findByUserId")
    void testFindByUserId() {
        //when
        User foundUser = userRepository.findByUserId(testDataInitializer.getTEST_USER_ID()).orElse(null);

        //then
        assertNotNull(foundUser);
        assertEquals(savedUser.getUserId(), foundUser.getUserId());
    }

    @Test
    @DisplayName("유저 식별자와 업데이트 시간이 널인 유저 조회")
    void findByIdAndUpdateTimeIsNull() {
        //when
        User findUser = userRepository.findByIdAndUpdateTimeIsNull(savedUser.getId()).orElse(null);

        //then
        assertNotNull(findUser);
        assertEquals(savedUser.getId(), findUser.getId());
    }

    @Test
    @DisplayName("유저 ID로 유저 조회")
    void findByUserId() {
        //when
        User findUser = userRepository.findByUserId(testDataInitializer.getTEST_USER_ID()).orElse(null);

        //then
        assertNotNull(findUser);
        assertEquals(testDataInitializer.getTEST_USER_ID(), findUser.getUserId());
    }

    @Test
    @DisplayName("유저 아이디와 비밀번호로 로그인 확인")
    void findByUserIdAndPassword() {
        //when
        User loginUser = userRepository.findByUserIdAndPassword(testDataInitializer.getTEST_USER_ID(), testDataInitializer.getTEST_PASSWORD()).orElse(null);

        //then
        assertNotNull(loginUser);
        assertEquals(testDataInitializer.getTEST_USER_ID(), loginUser.getUserId());
        assertEquals(testDataInitializer.getTEST_PASSWORD(), loginUser.getPassword());
    }

    @Test
    @DisplayName("유효한 유저 Email 확인")
    void existsByEmail() {
        //when
        boolean existsEmail = userRepository.existsByEmail(testDataInitializer.getTEST_EMAIL());

        //then
        assertEquals(true, existsEmail);
    }

    @Test
    @DisplayName("유효한 유저 ID 확인")
    void existsByUserId() {
        //when
        boolean existsUserId = userRepository.existsByUserId(testDataInitializer.getTEST_USER_ID());

        //then
        assertEquals(true, existsUserId);
    }

    @Test
    @DisplayName("유저 식별자로 유저 Email 조회")
    void findEmailById() {
        //when
        String email = userRepository.findUserProjectionById(savedUser.getId()).getEmail();

        //then
        assertNotNull(email);
        assertEquals(testDataInitializer.getTEST_EMAIL(), email);
    }
}