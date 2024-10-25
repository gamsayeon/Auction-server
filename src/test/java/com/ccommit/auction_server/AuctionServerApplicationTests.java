package com.ccommit.auction_server;

import com.ccommit.auction_server.config.TestDatabaseConfig;
import com.ccommit.auction_server.config.TestElasticsearchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({TestDatabaseConfig.class, TestElasticsearchConfig.class})
class AuctionServerApplicationTests {

    @Test
    void contextLoads() {
        // 컨텍스트가 로드되면 이 테스트가 성공합니다.
    }

}
