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
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private Integer age;
    private String phone;
    /**
     * 身份 医生 2 患者 1 管理员 0
     */
    private Integer role;
    private Integer roleId;



    //返回的都是对方的name
    @TableField(exist = false)
    private String otherName;
    @TableField(exist = false)
    private Integer otherId;
    @TableField(exist = false)
    private String departmentName;
    @TableField(exist = false)
    private String departmentDes;

}
