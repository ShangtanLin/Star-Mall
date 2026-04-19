package com.github.shangtanlin.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.net.ssl.SSLContext;


@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() throws Exception {
        // 1. 配置 ObjectMapper 以支持 Java 8 时间类
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule()) // 关键：处理 LocalDateTime
                // 可选：如果 ES 返回了 DTO 中没有的字段，不报错
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        // 2. 忽略证书验证（开发环境用）
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial((x509Certificates, s) -> true)
                .build();

        // 3. 用户名 + 密码
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "z0Me_63Lyq4GnIZ0g_KW"));

        // 4. 构建 RestClient
        RestClient restClient = RestClient.builder(
                new HttpHost("192.168.244.130", 9200, "https")
        ).setHttpClientConfigCallback(http ->
                http.setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(provider)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        ).build();

        // 5. 使用自定义的 ObjectMapper 创建 Transport
        // 关键点：将配置好的 objectMapper 传给 JacksonJsonpMapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper(objectMapper)
        );

        return new ElasticsearchClient(transport);
    }

    /**
     * 项目启动后自动执行一次 ping，提前完成 SSL 握手和连接池初始化
     */
    @Bean
    public ApplicationRunner elasticsearchWarmup(ElasticsearchClient client) {
        return args -> {
            int maxAttempts = 5;      // 最大重试次数
            long waitTime = 2000;     // 初始等待时间（毫秒）

            for (int i = 1; i <= maxAttempts; i++) {
                try {
                    // 发送轻量级请求
                    if (client.ping().value()) {
                        System.out.println(">>> [Attempt " + i + "] Elasticsearch 预热成功：连接已就绪");
                        return; // 成功后直接退出循环
                    }
                } catch (Exception e) {
                    System.err.println(">>> [Attempt " + i + "] Elasticsearch 尚未就绪，等待重试...");

                    if (i == maxAttempts) {
                        System.err.println(">>> Elasticsearch 预热最终失败，请检查服务状态。");
                        break;
                    }

                    try {
                        // 随着次数增加，等待时间可以适当加长（可选）
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
    }

}