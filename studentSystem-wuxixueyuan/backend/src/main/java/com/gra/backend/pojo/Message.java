package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("message")
public class Message {
    private Integer id;

    private Integer userId;

    private String message;

    private String createTime;


    @TableField(exist = false)
    private String username;
}
