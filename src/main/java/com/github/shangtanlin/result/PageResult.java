package com.github.shangtanlin.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//用于返回分页查询结果
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private Long total; //数据库内总记录数，返回用于前端进行分页
    private List<T> records; //当前页数据
}
