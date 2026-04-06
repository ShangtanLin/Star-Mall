package com.github.shangtanlin.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.shangtanlin.model.entity.category.Category;
import com.github.shangtanlin.mapper.CategoryMapper;
import com.github.shangtanlin.model.vo.CategoryVO;
import com.github.shangtanlin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.shangtanlin.common.constant.RedisConstant.CATEGORY_CACHE_KEY;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    //获取分类列表
    @Override
    public List<CategoryVO> getcategoryList() {
        List<Category> list = categoryMapper.list();
        return list.stream()
            .map(category -> {
                CategoryVO vo = new CategoryVO();
                vo.setId(category.getId());
                vo.setName(category.getName());

                String originalDesc = category.getDescription();
                if (originalDesc != null) {
                    // 将顿号替换为空格
                    // 例如："手机、数码、电脑" -> "手机 数码 电脑"
                    vo.setDescStr(originalDesc.replace("、", " "));
                }

                return vo;
            })
            .collect(Collectors.toList());

    }
}
