package com.github.shangtanlin.model.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSkuInfo {
    // --- 1. SKU 基础信息 ---
    private Long skuId;
    private String skuName;      // 规格名称（如：颜色:黑色, 容量:256G）
    private String picUrl;       // 商品图片（通常取该 SKU 的主图）
    private BigDecimal price;    // **实时价格**（数据库当前最新单价）
    private Integer stock;       // 当前可用库存（用于预下单时的库存校验）

    // --- 2. 关联的 SPU 信息 ---
    private Long spuId;
    private String spuName;      // 商品主标题（如：iPhone 15 Pro）

    // --- 3. 关联的店铺信息 (核心：用于分组) ---
    private Long shopId;         // 店铺ID
    private String shopName;     // 店铺名称
    private String shopLogo;     // 店铺图标（可选，前端展示用）
}
