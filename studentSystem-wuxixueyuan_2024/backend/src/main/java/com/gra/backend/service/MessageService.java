package com.gra.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.github.houbb.sensitive.word.support.result.WordResultHandlers;
import com.gra.backend.common.result.Result;
import com.gra.backend.mapper.MessageMapper;
import com.gra.backend.mapper.UserMapper;
import com.gra.backend.pojo.Message;
import com.gra.backend.utils.UserUtil;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    public Result<?> getMessageList() {
        List<Message> messages = messageMapper.selectList(new LambdaQueryWrapper<>(new Message()).orderBy(true, false, Message::getCreateTime));
        messages.forEach(message -> {
            message.setUsername(userMapper.selectById(message.getUserId()).getUsername());
        });
        return Result.success(messages);
    }


    public Result<?> addMessage(Message message) {
        Integer id = UserUtil.getUser().getId();
        message.setUserId(id);
        Date date = new Date();
        // yyyy-MM-dd HH:mm:ss
        message.setCreateTime(String.format("%tF %<tT", date));
        String text = "";
        message.setMessage(SensitiveWordHelper.replace(message.getMessage()));
        return Result.success(messageMapper.insert(message));
    }

}
