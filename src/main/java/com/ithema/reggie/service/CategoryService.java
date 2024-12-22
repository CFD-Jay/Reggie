package com.ithema.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithema.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 自定义删除方法，当存在关联时不能删除类别。
     * @param id
     */
    void remove(Long id);
}
