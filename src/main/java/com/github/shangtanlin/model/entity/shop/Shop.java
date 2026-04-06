package com.github.shangtanlin.model.entity.shop;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

    private Long id;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺Logo
     */
    private String shopLogo;

    /**
     * 所属卖家的用户ID (对应用户表的管理者)
     */
    private Long userId;

    /**
     * 店铺简介
     */
    private String description;

    /**
     * 店铺状态：0->已关闭；1->营业中；2->违规封禁
     */
    private Integer status;

    /**
     * 店铺评分
     */
    private BigDecimal rating;

    /**
     * 总销量
     */
    private Integer salesVolume;

    /**
     * 商家真实姓名
     */
    private String realName;

    /**
     * 联系电话
     */
    private String mobile;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDelete;
}