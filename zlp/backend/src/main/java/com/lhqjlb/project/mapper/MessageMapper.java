package com.lhqjlb.project.mapper;

import cn.hutool.core.lang.Dict;
import com.lhqjlb.project.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 消息
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    //分页查询 记录。
    Page<Message> page(Page<Message> page, Dict params);

    //根据参数查询 记录列表。
    List<Message> list(Dict params);

    List<Message> listContactUser(Dict params);

    List<Message> listContactShop(Dict params);

    //根据参数批量删除 记录。
    void deleteMany(Dict params);

    //获取 记录的选项列表。
    List<Message> listOptions();
}