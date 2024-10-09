package com.lhqjlb.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;

/**
 * 用户
 */
@Data
@TableName("userr")
public class Userr implements Serializable {
    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    //用户名
    private String username;
    //密码
    private String pwd;
    //昵称
    private String namee;
    //头像
    private String img;
    //类型，管理员、药店、用户
    private String typee;
    //状态，审核中、已通过、未通过
    private String statuss;
    //店铺介绍
    private String shopcontent;
    //店铺资质
    private String shopimgs;
    //地址
    private String address;
    //删除时间
    private Long deletetime;
    //创建时间
    private Long createtime;

    // ==============================
    //页码，从1开始
    @TableField(exist = false)
    private Integer pageNum;
    //每页查询数
    @TableField(exist = false)
    private Integer pageSize;
    //token
    @TableField(exist = false)
    private String token;
    @TableField(exist = false)
    private String oldpwd;
    @TableField(exist = false)
    private String newpwd;
    @TableField(exist = false)
    private List<Long> ids;
    @TableField(exist = false)
    private String keyword;

}
