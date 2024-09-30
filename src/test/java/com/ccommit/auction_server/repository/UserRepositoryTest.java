package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.config.TestElasticsearchConfig;
import com.ccommit.auction_server.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@DataJpaTest
@Import(TestElasticsearchConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Qualifier("elasticsearchTemplate")
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId("testUserId")
                .password("testPassword")
                .name("Test User")
                .email("test@example.com")
                .build();

        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Test findByUserId")
    void testFindByUserId() {
        User foundUser = userRepository.findByUserId("testUserId").orElse(null);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(testUser.getUserId(), foundUser.getUserId());
    }

}