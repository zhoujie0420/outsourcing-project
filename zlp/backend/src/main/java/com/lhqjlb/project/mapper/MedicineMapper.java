package com.lhqjlb.project.mapper;

import cn.hutool.core.lang.Dict;
import com.lhqjlb.project.entity.Medicine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 药品
 */
@Mapper
public interface MedicineMapper extends BaseMapper<Medicine> {

    //分页查询 记录。
    Page<Medicine> page(Page<Medicine> page, Dict params);

    //根据参数查询 记录列表。
    List<Medicine> list(Dict params);

    //根据参数批量删除 记录。
    void deleteMany(Dict params);

    //获取 记录的选项列表。
    List<Medicine> listOptions();
}