package com.github.shangtanlin;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.transport.rest5_client.low_level.RequestOptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

import java.io.StringReader;
import java.util.Map;

@SpringBootTest
public class ElasticsearchConnectionTest {

    @Autowired
    private ElasticsearchClient client;

    @Test
    public void testConnect() throws Exception {
        var info = client.info();
        System.out.println("连接成功，Elasticsearch 版本：" + info.version().number());
    }


    @Test
    void testCreateIndex() throws Exception {
        CreateIndexResponse response = client.indices().create(c -> c
                .index("heima")
                .withJson(new StringReader(MAPPING_TEMPLATE))
        );

        System.out.println("创建索引库成功：" + response.acknowledged());
    }


    private static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"info\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"email\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": \"false\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"properties\": {\n" +
            "          \"firstName\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";


    //测试新增文档
    @Test
    void testIndexDoc() throws Exception {
        // 3. 发送插入请求
        IndexResponse response = client.index(i -> i
                .index("heima") // 索引库
                .id("1")             // 文档ID，可不写
                .withJson(new StringReader(DOC_TEMPLATE)) // ★ 核心：直接插入 JSON
        );

        System.out.println("插入成功，结果：" + response.result());
    }

    //测试删除文档
    @Test
    void testDeleteDoc() throws Exception {
        DeleteResponse response = client.delete(d -> d
                .index("heima") //索引库名称
                .id("1")        //文档id
        );
        //打印删除结果
        System.out.println("删除状态：" + response.result());
    }

    //测试全量修改文档（全覆盖）
    @Test
    void testUpdateDocFull() throws Exception {
        UpdateResponse response = client.update(u -> u
                .index("heima")
                .id("1")
                .withJson(new StringReader(UPDATE_FULL_TEMPLATE)), Map.class);
        //打印修改结果
        System.out.println("修改结果"+response.result());
    }

    //测试增量(局部)修改文档
    @Test
    void testUpdateDocPartial() throws Exception {
        UpdateResponse response = client.update(u -> u.index("heima").id("1").doc(Map.of(
                "info", "测试一下，看看修改是否成功"
        )),Map.class);
        //打印修改结果
        System.out.println(response.result());
    }

    //测试查询文档
    @Test
    void testGetDoc() throws Exception {
        GetResponse response = client.get(g -> g
                .index("heima")
                .id("1"), Map.class);  //文档反序列化类型
        // 判断文档是否存在
        if (response.found()) {
            System.out.println("文档内容：");
            System.out.println(response.source());   // 打印文档 JSON
        } else {
            System.out.println("文档不存在");
        }
    }

    //原文档数据
    private static final String  DOC_TEMPLATE = "{  \n" +
            "\n" +
            "\"info\": \"小米14是一款性能强劲的旗舰手机，搭载徕卡光学镜头\", \n" +
            "\n" +
            " \"email\": \"user123@example.com\",  \n" +
            "\n" +
            "\"name\": {    \"firstName\": \"Xiaomi\"  } \n" +
            "\n" +
            "}";

    private static final String UPDATE_FULL_TEMPLATE = "{\n" +
            "  \"doc\": {\n" +
            "    \"info\": \"小米14 Pro，性能更强，影像系统全面升级\",\n" +
            "    \"email\": \"user123@example.com\",\n" +
            "    \"name\": {\n" +
            "      \"firstName\": \"Xiaomi\"\n" +
            "    }\n" +
            "  }\n" +
            "}\n";


    //private static final String UPDATE_PARTIAL_TEMPLATE =
}