package com.lhqjlb.project.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lhqjlb.project.util.PageVO;
import com.lhqjlb.project.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhqjlb.project.entity.Bookmark;
import com.lhqjlb.project.mapper.BookmarkMapper;
import com.lhqjlb.project.mapper.MedicineMapper;
import com.lhqjlb.project.mapper.UserrMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.lang.Dict;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.lhqjlb.project.entity.Options;

/**
 * 收藏
 */
@Slf4j
@RestController
public class BookmarkController {

    @Autowired
    private BookmarkMapper bookmarkMapper;
    @Autowired
    private MedicineMapper medicineMapper;
    @Autowired
    private UserrMapper userrMapper;

    // 分页
    @PostMapping("/api/bookmark/page")
    public R page(@RequestBody Bookmark bookmark) {
        Dict params = Dict.create();
        params.set("userid", StpUtil.getLoginIdAsLong());
        params.set("typee", bookmark.getTypee());
        Page<Bookmark> page = bookmarkMapper.page(
                new Page<>(bookmark.getPageNum(), bookmark.getPageSize()),
                params
        );
        List<Serializable> list = page.getRecords().stream().map(item -> {
            Long dataid = item.getDataid();
            String typee = item.getTypee();
            if (Objects.equals(typee, "药品")) {
                return medicineMapper.selectById(dataid);
            } else {
                return userrMapper.selectById(dataid);
            }
        }).toList();
        return R.ok(new PageVO(page.getTotal(), list));
    }

    // 列表
    @PostMapping("/api/bookmark/list")
    public R list(@RequestBody Bookmark bookmark) {
        Dict params = Dict.create();
        List<Bookmark> list = bookmarkMapper.list(params);
        return R.ok(list);
    }

    // 添加
    @PostMapping("/api/bookmark/add")
    public R add(@RequestBody Bookmark bookmark) {
        bookmark.setCreatetime(System.currentTimeMillis());
        bookmarkMapper.insert(bookmark);
        return R.ok();
    }

    // 修改
    @PostMapping("/api/bookmark/update")
    public R update(@RequestBody Bookmark bookmark) {
        bookmarkMapper.updateById(bookmark);
        return R.ok();
    }

    // 删除
    @PostMapping("/api/bookmark/delete")
    public R delete(@RequestBody Bookmark bookmark) {
        Dict params = Dict.create()
                .set("deletetime", System.currentTimeMillis())
                .set("ids", bookmark.getIds());
        bookmarkMapper.deleteMany(params);
        return R.ok();
    }

    // 详情
    @PostMapping("/api/bookmark/detail")
    public R detail(@RequestBody Bookmark bookmark) {
        Bookmark entity = bookmarkMapper.selectById(bookmark.getId());
        return R.ok(entity);
    }

    //下拉列表
    @PostMapping("/api/bookmark/options")
    public R options() {
        List<Bookmark> bookmarkList = bookmarkMapper.listOptions();
        List<Options> list = bookmarkList.stream().map(item -> {
            Options options = new Options();
            options.setLabel("下拉");
            options.setValue(item.getId());
            return options;
        }).toList();
        return R.ok(list);
    }

    //是否收藏
    @PostMapping("/api/bookmark/check")
    public R check(@RequestBody Bookmark bookmark) {
        Long userid = bookmark.getUserid();
        Long medicineid = bookmark.getMedicineid();
        Long shopid = bookmark.getShopid();
        Dict params = Dict.create();
        params.set("userid", userid);
        params.set("dataid", medicineid);
        params.set("typee", "药品");
        Bookmark medicine = bookmarkMapper.check(params);
        params.set("dataid", shopid);
        params.set("typee", "店铺");
        Bookmark shop = bookmarkMapper.check(params);
        Dict ret = Dict.create();
        ret.set("medicine", medicine == null ? "default" : "warning");
        ret.set("shop", shop == null ? "default" : "warning");
        return R.ok(ret);
    }

    //切换收藏
    @PostMapping("/api/bookmark/toggle")
    public R toggle(@RequestBody Bookmark bookmark) {
        Long userid = bookmark.getUserid();
        Long dataid = bookmark.getDataid();
        String typee = bookmark.getTypee();
        Dict params = Dict.create();
        params.set("userid", userid);
        params.set("dataid", dataid);
        params.set("typee", typee);
        Bookmark entity = bookmarkMapper.check(params);
        if (entity == null) {
            entity = new Bookmark();
            entity.setUserid(userid);
            entity.setDataid(dataid);
            entity.setTypee(typee);
            entity.setCreatetime(System.currentTimeMillis());
            bookmarkMapper.insert(entity);
        } else {
            Long id = entity.getId();
            params.clear();
            params.set("deletetime", System.currentTimeMillis());
            params.set("ids", List.of(id));
            bookmarkMapper.deleteMany(params);
        }
        return R.ok();
    }
}