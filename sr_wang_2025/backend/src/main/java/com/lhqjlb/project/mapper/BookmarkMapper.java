package com.lhqjlb.project.mapper;

import cn.hutool.core.lang.Dict;
import com.lhqjlb.project.entity.Bookmark;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 收藏
 */
@Mapper
public interface BookmarkMapper extends BaseMapper<Bookmark> {

    //分页查询 记录。
    Page<Bookmark> page(Page<Bookmark> page, Dict params);

    //根据参数查询 记录列表。
    List<Bookmark> list(Dict params);

    //根据参数批量删除 记录。
    void deleteMany(Dict params);

    //获取 记录的选项列表。
    List<Bookmark> listOptions();

    Bookmark check(Dict params);
}