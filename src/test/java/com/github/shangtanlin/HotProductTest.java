package com.github.shangtanlin;

import com.github.shangtanlin.model.vo.ProductCardVO;
import com.github.shangtanlin.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class HotProductTest {

    @Autowired
    private ProductService productService;

    @Test
    public void testGetHotProductsToday() {
        // 调用热门商品接口
        List<ProductCardVO> hotProducts = productService.getHotProductsToday();

        // 打印结果
        System.out.println("===== 今日热门商品 =====");
        System.out.println("商品数量: " + hotProducts.size());

        for (int i = 0; i < hotProducts.size(); i++) {
            ProductCardVO product = hotProducts.get(i);
            System.out.println(String.format("排名%d: spuId=%d, name=%s, sales=%d",
                    i + 1,
                    product.getSpuId(),
                    product.getSpuName(),
                    product.getSales()));
        }
        System.out.println("========================");
    }
}