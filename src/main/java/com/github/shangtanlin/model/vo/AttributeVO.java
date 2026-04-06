package com.github.shangtanlin.model.vo;

import lombok.Data;

import java.util.List;
@Data
public class AttributeVO {
    private String attrName;           // 例如：颜色、尺码
    private List<String> values;       // 例如：["黑色", "白色", "蓝色"]
}
