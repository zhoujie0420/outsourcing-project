package com.gra.backend.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourse {
    @TableId
    private Integer id;
    private Integer studentId;
    private Integer courseId;
    private String score;
}
