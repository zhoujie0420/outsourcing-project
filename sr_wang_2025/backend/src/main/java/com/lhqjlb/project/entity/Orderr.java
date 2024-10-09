package com.lhqjlb.project.entity;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.List;

/**
 * 订单
 */
@Data
@TableName("orderr")
public class Orderr implements Serializable {
    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    //订单号
    private String orderno;
    //用户ID
    private Long userid;
    //店铺ID
    private Long shopid;
    //药品ID
    private Long medicineid;
    //药品名称
    private String namee;
    //单价，元
    private Integer price;
    //数量
    private Integer cnt;
    //地址
    private String addcontent;
    //发货状态，待发货、已发货、已签收
    private String statuss;
    //评价状态，未评论、已评价
    private String evastatus;
    //评价内容
    private String content;
    //是否显示评价，是、否
    private String showcontent;
    //评分
    private Integer rate;
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
    @TableField(exist = false)
    private List<Long> ids;
    //关键字
    @TableField(exist = false)
    private String keyword;
    @TableField(exist = false)
    private List<Orderr> sub;
    @TableField(exist = false)
    private Userr user;
    @TableField(exist = false)
    private Userr shop;
    @TableField(exist = false)
    private Integer totalPrice;
    @TableField(exist = false)
    private Dict address;
}
