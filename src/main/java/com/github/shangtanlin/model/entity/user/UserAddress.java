package com.github.shangtanlin.model.entity.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAddress {
    private Long id;

    private Long userId;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private Integer isDefault;

    private LocalDateTime createTime;
}
