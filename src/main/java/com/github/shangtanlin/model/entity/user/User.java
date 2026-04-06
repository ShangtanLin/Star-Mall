package com.github.shangtanlin.model.entity.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;

    private String phone;

    private String username;

    private String password;

    private String email;

    private String avatar;

    private LocalDateTime createTime;
}
