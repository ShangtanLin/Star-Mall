package com.github.shangtanlin.model.dto;

import lombok.Data;

@Data
public class SendCodeDTO {
    private String phoneNumber;
    private String type;
}
