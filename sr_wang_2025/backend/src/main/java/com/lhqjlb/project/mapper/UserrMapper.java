package com.lhqjlb.project.mapper;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhqjlb.project.entity.Userr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户
 */
@Mapper
public interface UserrMapper extends BaseMapper<Userr> {

    Userr login(Userr user);

    List<Userr> listByUsername(Dict params);

    Page<Userr> page(Page<Userr> page, Dict params);


    List<Userr> listOptions();

    void deleteMany(Dict params);

}