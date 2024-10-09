package com.lhqjlb.project.mapper;

import cn.hutool.core.lang.Dict;
import com.lhqjlb.project.entity.Orderr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 订单
 */
@Mapper
public interface OrderrMapper extends BaseMapper<Orderr> {

    //分页查询 记录。
    Page<Orderr> page(Page<Orderr> page, Dict params);

    //根据参数查询 记录列表。
    List<Orderr> list(Dict params);

    //根据参数批量删除 记录。
    void deleteMany(Dict params);

    //获取 记录的选项列表。
    List<Orderr> listOptions();

    List<Dict> listSalesVolume();

    List<Dict> listSalesRevenue(Dict params);

    List<Orderr> listEvaluation(Dict params);
}