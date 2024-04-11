package com.lhqjlb.project.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lhqjlb.project.BaiDuDemo.Chat;
import com.lhqjlb.project.util.PageVO;
import com.lhqjlb.project.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhqjlb.project.entity.Message;
import com.lhqjlb.project.entity.Userr;
import com.lhqjlb.project.mapper.MessageMapper;
import com.lhqjlb.project.mapper.UserrMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.lang.Dict;

import java.util.List;
import java.util.Scanner;

import com.lhqjlb.project.entity.Options;

/**
 * 消息
 */
@Slf4j
@RestController
public class MessageController {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserrMapper userrMapper;

    // 分页
    @PostMapping("/api/message/page")
    public R page(@RequestBody Message message) {
        Dict params = Dict.create();
        params.set("keyword", message.getKeyword());
        Page<Message> page = messageMapper.page(
                new Page<>(message.getPageNum(), message.getPageSize()),
                params
        );
        return R.ok(new PageVO(page.getTotal(), page.getRecords()));
    }

    // 列表
    @PostMapping("/api/message/list")
    public R list(@RequestBody Message message) {
        Dict params = Dict.create();
        params.set("shopid", message.getShopid());
        params.set("userid", message.getUserid());
        List<Message> list = messageMapper.list(params);
        for (Message item : list) {
            Long userid = item.getUserid();
            Userr userr = userrMapper.selectById(userid);
            item.setUser(userr);
            Long shopid = item.getShopid();
            Userr shop = userrMapper.selectById(shopid);
            item.setShop(shop);
        }
        return R.ok(list);
    }

    //店铺
    @PostMapping("/api/message/listContactUser")
    public R listContactUser(@RequestBody Message message) {
        Dict params = Dict.create();
        params.set("shopid", StpUtil.getLoginIdAsLong());
        List<Message> list = messageMapper.listContactUser(params);
        for (Message item : list) {
            Long userid = item.getUserid();
            Userr userr = userrMapper.selectById(userid);
            item.setUser(userr);
        }
        return R.ok(list);
    }

    //用户
    @PostMapping("/api/message/listContactShop")
    public R listContactShop(@RequestBody Message message) {
        Dict params = Dict.create();
        params.set("userid", StpUtil.getLoginIdAsLong());
        List<Message> list = messageMapper.listContactShop(params);
        for (Message item : list) {
            Long shopid = item.getShopid();
            Userr shop = userrMapper.selectById(shopid);
            item.setShop(shop);
        }
        return R.ok(list);
    }

    // 添加
    @PostMapping("/api/message/add")
    public R add(@RequestBody Message message) {
        message.setCreatetime(System.currentTimeMillis());
        messageMapper.insert(message);
        return R.ok();
    }

    // 修改
    @PostMapping("/api/message/update")
    public R update(@RequestBody Message message) {
        messageMapper.updateById(message);
        return R.ok();
    }

    // 删除
    @PostMapping("/api/message/delete")
    public R delete(@RequestBody Message message) {
        Dict params = Dict.create()
                .set("deletetime", System.currentTimeMillis())
                .set("ids", message.getIds());
        messageMapper.deleteMany(params);
        return R.ok();
    }

    // 详情
    @PostMapping("/api/message/detail")
    public R detail(@RequestBody Message message) {
        Message entity = messageMapper.selectById(message.getId());
        return R.ok(entity);
    }

    //下拉列表
    @PostMapping("/api/message/options")
    public R options() {
        List<Message> messageList = messageMapper.listOptions();
        List<Options> list = messageList.stream().map(item -> {
            Options options = new Options();
            options.setLabel("下拉");
            options.setValue(item.getId());
            return options;
        }).toList();
        return R.ok(list);
    }

    @PostMapping("/api/message/aiChat")
    public R aiChat(@RequestBody Message message) {
        Chat chat = new Chat();
        boolean result = chat.getAccessToken();
        String answer = "";
        if (result){
            answer = chat.getAnswer(message.getContent());
        }
        return R.ok(answer);
    }
}