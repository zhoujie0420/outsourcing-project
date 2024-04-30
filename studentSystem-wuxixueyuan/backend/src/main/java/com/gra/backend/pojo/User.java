package com.gra.backend.pojo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    private Integer id;
    @ExcelProperty("学生姓名")
    private String username;
    @ExcelIgnore
    private String password;
    @ExcelIgnore
    private Integer age;
    @ExcelProperty("手机号")
    private String phone;
    /**
     * 身份 管理员0 学生1 教师2
     */
    @ExcelIgnore
    private Integer role;
    @ExcelIgnore
    private Integer userInfo;
    //0禁用 1启用
    @ExcelIgnore
    private Integer status;

    @TableField(exist = false)
    @ExcelProperty("成绩")
    private String score;
}
