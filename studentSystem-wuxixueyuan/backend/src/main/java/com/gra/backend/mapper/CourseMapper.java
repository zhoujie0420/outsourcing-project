package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.Course;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CourseMapper extends MPJBaseMapper<Course> {

    int insert(Course course);

}
