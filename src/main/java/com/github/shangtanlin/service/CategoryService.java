package com.github.shangtanlin.service;


import com.github.shangtanlin.model.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    //获取分类列表
    List<CategoryVO> getcategoryList();

}
