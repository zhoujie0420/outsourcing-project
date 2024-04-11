package com.lhqjlb.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;

/**
 * 药品
 */
@Data
@TableName("medicine")
public class Medicine implements Serializable {
    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    //药店ID
    private Long shopid;
    //药品名称
    private String namee;
    //药品描述
    private String description;
    //图片
    private String img;
    //单价，元
    private Integer price;
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
    private Userr shop;
    //数量
    @TableField(exist = false)
    private Integer cnt;
    // 药品类别
    private Integer medicineCategory;

    @TableField(exist = false)
    private String medicineCategoryName;



}
