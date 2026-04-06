package com.github.shangtanlin.model.entity.category;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品分类实体类
 * 对应表名：category
 */
@Data
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类层级：1=一级分类，2=二级分类，3=三级分类
     */
    private Integer level;

    /**
     * 排序字段，越小越靠前
     */
    private Integer sort;

    /**
     * 分类图标（可选）
     */
    private String icon;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 非数据库字段：子分类列表
     * 用于前端展示树形类目表
     */
    private List<Category> children;
}