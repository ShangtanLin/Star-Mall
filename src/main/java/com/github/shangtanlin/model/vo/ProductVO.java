package com.github.shangtanlin.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class ProductVO {

    private Long spuId;

    private String name;

    private String description;

    private String mainImage;

    private Long brandId;

    private String brandName;

    private Long categoryId;

    private String categoryName;

    /** 商品所有属性（颜色、尺码...） */
    private List<AttributeVO> attributes;

    /** 所有 SKU 列表 */
    private List<SkuVO> skus;


    // --- 新增：商家/店铺相关信息 ---
    private Long shopId;         // 店铺ID（核心：用于跳转店铺页和后续下单分组）
    private String shopName;     // 店铺名称
    private String shopLogo;     // 店铺图片/Logo
    private BigDecimal shopRating;   // 店铺评分


}
