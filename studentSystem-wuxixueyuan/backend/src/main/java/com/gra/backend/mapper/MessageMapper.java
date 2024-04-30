package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends MPJBaseMapper<Message> {
}
