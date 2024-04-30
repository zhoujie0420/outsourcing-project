package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@TableName("course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer score;
    private Integer state;
    private Integer teacherId;
    @TableField(exist = false)
    private String teacher;
    private Integer yearDate;
    private Integer weekTime;
    private Integer dayTime;
    private Integer courseInfoId;
    private String url;
    @TableField(exist = false)
    private String time;

    //选课退课状态
    @TableField(exist = false)
    private Integer studentCourseState;
}
