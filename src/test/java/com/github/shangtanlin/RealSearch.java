package com.github.shangtanlin;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.github.shangtanlin.model.dto.es.ProductIndexDoc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class RealSearch {
    // 假设您已经正确注入了 ElasticsearchClient
    @Autowired
    private ElasticsearchClient client;

    private final String INDEX_NAME = "product";
    private final String SEARCH_KEYWORD = "智能跑鞋";

    @Test
    void testMultiMatchSearch() throws IOException {

        // 1. 构建 MultiMatch Query 对象
        // MultiMatchQuery 实现了 Query 接口
        MultiMatchQuery multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(SEARCH_KEYWORD) // 设置查询关键词
                .fields("name", "description") // 设置要查询的字段
        );

        // 2. 将 MultiMatch Query 封装到顶层 Query 对象中
        Query finalQuery = Query.of(q -> q.multiMatch(multiMatchQuery));

        // 3. 构建 SearchRequest
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(INDEX_NAME)
                .query(finalQuery)
                .size(10) // 返回前 10 条结果
        );

        // 4. 执行搜索
        SearchResponse<ProductIndexDoc> response = client.search(searchRequest, ProductIndexDoc.class);

        // 5. 处理结果
        List<ProductIndexDoc> productIndexDocs = response.hits().hits().stream()
                .map(Hit::source) // 获取文档的实际内容
                .collect(Collectors.toList());

        // 打印或断言结果
        System.out.println("成功找到 " + response.hits().total().value() + " 条商品。");
        productIndexDocs.forEach(p -> System.out.println("ID: " + p.getId() + ", 名称: " + p.getName()));

        System.out.println(productIndexDocs);

        // JUnit 断言 (例如，确保至少找到一个结果)
        assert response.hits().total().value() > 0 : "未找到任何相关商品";
    }
}
