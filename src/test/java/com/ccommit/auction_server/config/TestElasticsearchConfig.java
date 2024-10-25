package com.ccommit.auction_server.config;

import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Configuration
@Profile("test")
public class TestElasticsearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient() throws IOException {
        RestHighLevelClient mockClient = Mockito.mock(RestHighLevelClient.class);
        IndicesClient mockIndicesClient = Mockito.mock(IndicesClient.class);

        // Mocking 설정
        when(mockClient.indices()).thenReturn(mockIndicesClient);
        when(mockIndicesClient.exists((GetIndexRequest) any(), any())).thenReturn(true); // 필요한 값 반환

        return mockClient;
    }

    @Bean(name = "elasticsearchTemplate")
    public ElasticsearchOperations elasticsearchTemplate() throws IOException {
        return new ElasticsearchRestTemplate(restHighLevelClient());
    }
}
