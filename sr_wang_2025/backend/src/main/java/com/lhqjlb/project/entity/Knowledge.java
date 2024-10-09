package com.lhqjlb.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.util.List;

/**
 * 知识普及
 */
@Data
@TableName("knowledge")
public class Knowledge implements Serializable {
    //主键
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    //标题
    private String title;
    //类型，用药知识、健康知识、公告
    private String typee;
    //图片
    private String img;
    //内容
    private String content;
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
}
