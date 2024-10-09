package com.lhqjlb.project.controller;

import cn.hutool.core.lang.Dict;
import com.lhqjlb.project.util.MD5Util;
import com.lhqjlb.project.util.PageVO;
import com.lhqjlb.project.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhqjlb.project.entity.Options;
import com.lhqjlb.project.entity.Userr;
import com.lhqjlb.project.mapper.UserrMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户
 */
@Slf4j
@RestController
public class UserrController {

    @Autowired
    private UserrMapper userrMapper;


    // 分页
    @PostMapping("/api/userr/page")
    public R page(@RequestBody Userr userr) {
        Dict params = Dict.create();
        params.set("keyword", userr.getKeyword());
        params.set("typee", userr.getTypee());
        params.set("statuss", userr.getStatuss());
        Page<Userr> page = userrMapper.page(
                new Page<>(userr.getPageNum(), userr.getPageSize()),
                params
        );
        return R.ok(new PageVO(page.getTotal(), page.getRecords()));
    }

    // 添加
    @PostMapping("/api/userr/add")
    public R add(@RequestBody Userr userr) {
        userr.setPwd(MD5Util.encrypt(userr.getPwd()));
        userr.setCreatetime(System.currentTimeMillis());
        userrMapper.insert(userr);
        return R.ok();
    }

    // 修改
    @PostMapping("/api/userr/update")
    public R update(@RequestBody Userr userr) {
        userrMapper.updateById(userr);
        return R.ok();
    }

    // 删除
    @PostMapping("/api/userr/delete")
    public R delete(@RequestBody Userr userr) {
        Dict params = Dict.create()
                .set("deletetime", System.currentTimeMillis())
                .set("ids", userr.getIds());
        userrMapper.deleteMany(params);
        return R.ok();
    }

    // 详情
    @PostMapping("/api/userr/detail")
    public R detail(@RequestBody Userr userr) {
        Userr entity = userrMapper.selectById(userr.getId());
        return R.ok(entity);
    }

    //下拉列表
    @PostMapping("/api/userr/options")
    public R options() {
        List<Userr> userList = userrMapper.listOptions();
        List<Options> list = userList.stream().map(item -> {
            Options options = new Options();
            options.setLabel(item.getNamee());
            options.setValue(item.getId());
            return options;
        }).toList();
        return R.ok(list);
    }

    //重置密码
    @PostMapping("/api/userr/updatepwd")
    public R updatepwd(@RequestBody Userr userr) {
        userr.setPwd(MD5Util.encrypt(userr.getPwd()));
        userrMapper.updateById(userr);
        return R.ok();
    }
}