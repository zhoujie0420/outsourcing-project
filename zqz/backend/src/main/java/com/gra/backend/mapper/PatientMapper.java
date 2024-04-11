package com.gra.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.gra.backend.pojo.Doctor;
import com.gra.backend.pojo.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: elk
 * @create: 2024-02-05 17:49
 **/
@Mapper
public interface PatientMapper extends MPJBaseMapper<Patient> {
}
