package com.github.shangtanlin.controller;

import com.github.shangtanlin.model.vo.CategoryVO;
import com.github.shangtanlin.model.vo.ProductCardVO;
import com.github.shangtanlin.result.Result;
import com.github.shangtanlin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类列表
     * @return
     */
    @GetMapping("/getCategoryList")
    public Result<?> categoryList() {
        List<CategoryVO> categoryVOS = categoryService.getcategoryList();
        return Result.ok(categoryVOS);
    }


}
