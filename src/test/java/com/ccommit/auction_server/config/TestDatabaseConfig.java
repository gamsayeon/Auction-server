package com.ccommit.auction_server.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Configuration
@Testcontainers
@Profile("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 클래스당 하나의 인스턴스를 사용합니다.
public class TestDatabaseConfig {
    @Value("${test.container.mysql.image}")
    private String mysqlImage;
    @Value("${test.container.mysql.database}")
    private String databaseName;
    @Value("${test.container.mysql.username}")
    private String username;
    @Value("${test.container.mysql.password}")
    private String password;
    private static MySQLContainer<?> mysqlContainer;

    @PostConstruct
    public void startContainer() {
        if (mysqlContainer == null) { // 이미 컨테이너가 생성되어 있지 않은 경우에만 생성
            mysqlContainer = new MySQLContainer<>(mysqlImage)
                    .withDatabaseName(databaseName)
                    .withUsername(username)
                    .withPassword(password)
                    .withInitScript("schema.sql")
                    .withCreateContainerCmdModifier(cmd -> cmd.withMemory((long) (512 * 1024 * 1024)));

            mysqlContainer.start();
        }
    }

    @PreDestroy
    public void stopContainer() {
        if (mysqlContainer.isRunning()) {
            mysqlContainer.stop();
        }
    }

    @Bean
    public MySQLContainer<?> mysqlContainer() {
        return mysqlContainer;
    }

    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(mysqlContainer.getDriverClassName());
        dataSource.setUrl(mysqlContainer.getJdbcUrl());
        dataSource.setUsername(mysqlContainer.getUsername());
        dataSource.setPassword(mysqlContainer.getPassword());
        return dataSource;
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mysqlContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mysqlContainer.getUsername());
        registry.add("spring.datasource.password", () -> mysqlContainer.getPassword());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Test
    void testDatabaseConnection() {
        // 데이터베이스 연결 확인
        assertNotNull(jdbcTemplate(), "JdbcTemplate should not be null");

        // 데이터베이스에서 테이블 확인
        Integer tableCount = jdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'testDB'",
                Integer.class
        );
        assertNotNull(tableCount, "Table count should not be null");
        // 테이블이 존재하는지 확인 (테이블 이름을 실제 테이블 이름으로 바꾸세요)
        assert tableCount == 7 : "Not all tables were created in the database";
    }
}