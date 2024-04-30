package com.gra.backend.controller;

import com.gra.backend.common.result.Result;
import com.gra.backend.pojo.Message;
import com.gra.backend.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user/message")
public class MessageController {

    @Resource
    private MessageService messageService;
    @GetMapping("getMessageList")
    public Result<?> getMessageList() {
        return messageService.getMessageList();
    }

    @PostMapping("addMessage")
    public Result<?> addMessage(Message message) {
        return messageService.addMessage(message);
    }
}
