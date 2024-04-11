package com.lhqjlb.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;

/**
 * 购物车
 */
@Data
@TableName("cart")
public class Cart implements Serializable {
    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    //用户ID
    private Long userid;
    //药品ID
    private Long medicineid;
    //数量
    private Integer cnt;
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
    private Medicine medicine;
    @TableField(exist = false)
    private Userr shop;
}
