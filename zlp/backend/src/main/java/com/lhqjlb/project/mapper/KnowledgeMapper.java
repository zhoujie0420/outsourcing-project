package com.lhqjlb.project.mapper;

import cn.hutool.core.lang.Dict;
import com.lhqjlb.project.entity.Knowledge;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 知识普及
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {

    //分页查询 记录。
    Page<Knowledge> page(Page<Knowledge> page, Dict params);

    //公告
    Page<Knowledge> pageNotice(Page<Knowledge> page, Dict params);

    //根据参数查询 记录列表。
    List<Knowledge> list(Dict params);

    //根据参数批量删除 记录。
    void deleteMany(Dict params);

    //获取 记录的选项列表。
    List<Knowledge> listOptions();
}