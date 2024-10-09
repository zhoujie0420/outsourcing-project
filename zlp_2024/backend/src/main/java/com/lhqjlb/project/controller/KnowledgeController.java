package com.lhqjlb.project.controller;

import com.lhqjlb.project.util.PageVO;
import com.lhqjlb.project.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhqjlb.project.entity.Knowledge;
import com.lhqjlb.project.mapper.KnowledgeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.lang.Dict;
import java.util.List;
import com.lhqjlb.project.entity.Options;

/**
 * 知识普及
 */
@Slf4j
@RestController
public class KnowledgeController {

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    // 分页
    @PostMapping("/api/knowledge/page")
    public R page(@RequestBody Knowledge knowledge) {
        Dict params = Dict.create();
        params.set("keyword", knowledge.getKeyword());
        params.set("typee", knowledge.getTypee());
        Page<Knowledge> page = knowledgeMapper.page(
                new Page<>(knowledge.getPageNum(), knowledge.getPageSize()),
                params
        );
        return R.ok(new PageVO(page.getTotal(), page.getRecords()));
    }

    // 分页
    @PostMapping("/api/knowledge/pageNotice")
    public R pageNotice(@RequestBody Knowledge knowledge) {
        Dict params = Dict.create();
        params.set("keyword", knowledge.getKeyword());
        Page<Knowledge> page = knowledgeMapper.pageNotice(
                new Page<>(knowledge.getPageNum(), knowledge.getPageSize()),
                params
        );
        return R.ok(new PageVO(page.getTotal(), page.getRecords()));
    }

    // 列表
    @PostMapping("/api/knowledge/list")
    public R list(@RequestBody Knowledge knowledge) {
        Dict params = Dict.create();
        List<Knowledge> list = knowledgeMapper.list(params);
        return R.ok(list);
    }

    // 添加
    @PostMapping("/api/knowledge/add")
    public R add(@RequestBody Knowledge knowledge) {
        knowledge.setCreatetime(System.currentTimeMillis());
        knowledgeMapper.insert(knowledge);
        return R.ok();
    }

    // 修改
    @PostMapping("/api/knowledge/update")
    public R update(@RequestBody Knowledge knowledge) {
        knowledgeMapper.updateById(knowledge);
        return R.ok();
    }

    // 删除
    @PostMapping("/api/knowledge/delete")
    public R delete(@RequestBody Knowledge knowledge) {
        Dict params = Dict.create()
                        .set("deletetime", System.currentTimeMillis())
                        .set("ids", knowledge.getIds());
        knowledgeMapper.deleteMany(params);
        return R.ok();
    }

    // 详情
    @PostMapping("/api/knowledge/detail")
    public R detail(@RequestBody Knowledge knowledge) {
        Knowledge entity = knowledgeMapper.selectById(knowledge.getId());
        return R.ok(entity);
    }
    
    //下拉列表
    @PostMapping("/api/knowledge/options")
    public R options() {
        List<Knowledge> knowledgeList = knowledgeMapper.listOptions();
        List<Options> list = knowledgeList.stream().map(item -> {
            Options options = new Options();
            options.setLabel("下拉");
            options.setValue(item.getId());
            return options;
        }).toList();
        return R.ok(list);
    }
}