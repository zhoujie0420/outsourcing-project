package com.lhqjlb.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;

/**
 * 收藏
 */
@Data
@TableName("bookmark")
public class Bookmark implements Serializable {
    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    //用户ID
    private Long userid;
    //数据ID
    private Long dataid;
    //类型，药品、店铺
    private String typee;
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
    private Long medicineid;
    @TableField(exist = false)
    private Long shopid;
}
