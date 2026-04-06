package com.github.shangtanlin.model.vo;

import lombok.Data;

@Data
public class UserAddressVO {
    // 1. 核心标识
    private Long id;                // 地址唯一主键，修改/删除/下单时必传

    // 2. 收货人信息
    private String receiverName;    // 收货人姓名
    private String receiverPhone;   // 收货人电话

    // 3. 地区信息（拆解版：方便前端在修改页面回显三级联动）
    private String province;        // 省份
    private String city;            // 城市
    private String district;        // 区/县
    private String detailAddress;   // 详细地址（街道门牌号）

    // 4. 辅助展示（拼接版：方便前端直接在列表中渲染，减少前端计算量）
    private String fullAddress;     // 格式如：“广东省深圳市南山区粤海街道...”

    // 5. 状态标识
    private Integer isDefault;      // 0-非默认，1-默认（或者用 Boolean 类型）
}
