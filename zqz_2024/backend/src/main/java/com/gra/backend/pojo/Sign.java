package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sign {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer doctorId;
    private Integer patientId;
    private String docUrl;
    private String createTime;
    // 0 患者上传状态 1 医生上传状态 2 管理员确认状态
    private Integer status;


    //返回的都是对方的name
    @TableField(exist = false)
    private String otherName;
    @TableField(exist = false)
    private String departmentName;
    @TableField(exist = false)
    private String departmentDes;
}