package com.gra.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gra.backend.common.result.Result;
import com.gra.backend.dto.RecordAddDto;
import com.gra.backend.mapper.RecordMapper;
import com.gra.backend.mapper.UserMapper;
import com.gra.backend.pojo.Record;
import com.gra.backend.pojo.User;
import com.gra.backend.utils.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: jiezhou
 **/

@Service
public class RecordService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private RecordMapper recordMapper;


    public Result<?> addRecord(Record record) {
        recordMapper.insert(record);
        return Result.success();
    }

    public Result<?> getRecords() {
        User user = UserUtil.getUser();
        if (user == null) {
            return Result.fail("用户未登录");
        }
        LambdaQueryWrapper<Record> recordLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Integer role = user.getRole();
        recordLambdaQueryWrapper.orderByDesc(Record::getConsultationDate);
        List<Record> records = new ArrayList<>();
        if (role == 2) {
            recordLambdaQueryWrapper.eq(Record::getDoctorId, user.getId()).eq(Record::getFatherId, 0);
            records = recordMapper.selectList(recordLambdaQueryWrapper);
            for (Record record : records) {
                User patient = userMapper.selectById(record.getPatientId());
                record.setOtherName(patient.getUsername());
                record.setOtherPhone(patient.getPhone());
                LambdaQueryWrapper<Record> child = new LambdaQueryWrapper<>();
                child.eq(Record::getFatherId, record.getId());
                List<Record> followUpList = recordMapper.selectList(child);
                record.setFollowUpList(followUpList);
            }
        } else if (role == 1) {
            recordLambdaQueryWrapper.eq(Record::getPatientId, user.getId()).eq(Record::getFatherId, 0);
            records = recordMapper.selectList(recordLambdaQueryWrapper);
            for (Record record : records) {
                User doctor = userMapper.selectById(record.getDoctorId());
                record.setOtherName(doctor.getUsername());
                record.setOtherPhone(doctor.getPhone());
                LambdaQueryWrapper<Record> child = new LambdaQueryWrapper<>();
                child.eq(Record::getFatherId, record.getId());
                List<Record> followUpList = recordMapper.selectList(child);
                record.setFollowUpList(followUpList);
            }
        }
        return Result.success(records);
    }

    public Result<?> updateRecord(Record record) {
        record.setStatus(1);
        recordMapper.updateById(record);
        return Result.success();
    }
}
