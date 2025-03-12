package br.com.ufpb.GerenciadorEscolar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${minio.base.url}")
    private String minioBaseUrl;

    @Value("${minio.api.key}")
    private String minioApiKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(minioBaseUrl)
                .defaultHeader("api-key", minioApiKey)
                .build();
    }
}
