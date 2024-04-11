package com.lhqjlb.project.mapper;

import cn.hutool.core.lang.Dict;
import com.lhqjlb.project.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 购物车
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    //分页查询 记录。
    Page<Cart> page(Page<Cart> page, Dict params);

    //根据参数查询 记录列表。
    List<Cart> list(Dict params);

    //根据参数批量删除 记录。
    void deleteMany(Dict params);

    //获取 记录的选项列表。
    List<Cart> listOptions();

    Cart getCart(Dict params);
}