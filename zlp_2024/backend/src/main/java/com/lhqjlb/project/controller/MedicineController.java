package com.lhqjlb.project.controller;

import com.lhqjlb.project.constant.MedicineCategory;
import com.lhqjlb.project.response.MedicineRep;
import com.lhqjlb.project.util.PageVO;
import com.lhqjlb.project.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhqjlb.project.entity.Medicine;
import com.lhqjlb.project.entity.Userr;
import com.lhqjlb.project.mapper.MedicineMapper;
import com.lhqjlb.project.mapper.UserrMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.lang.Dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.lhqjlb.project.entity.Options;

/**
 * 药品
 */
@Slf4j
@RestController
public class MedicineController {

    @Autowired
    private MedicineMapper medicineMapper;
    @Autowired
    private UserrMapper userrMapper;

    // 分页
    @PostMapping("/api/medicine/page")
    public R page(@RequestBody Medicine medicine) {
        Dict params = Dict.create();
        params.set("keyword", medicine.getKeyword());
        params.set("shopid", medicine.getShopid());
        Page<Medicine> page = medicineMapper.page(
                new Page<>(medicine.getPageNum(), medicine.getPageSize()),
                params
        );
        for (Medicine item : page.getRecords()) {
            Userr userr = userrMapper.selectById(item.getShopid());
            item.setShop(userr);
            item.setMedicineCategoryName(Objects.requireNonNull(MedicineCategory.getName(item.getMedicineCategory())).getName());
        }
        return R.ok(new PageVO(page.getTotal(), page.getRecords()));
    }

    // 列表
    @PostMapping("/api/medicine/list")
    public R list(@RequestBody Medicine medicine) {
        Dict params = Dict.create();
        List<Medicine> list = medicineMapper.list(params);
        HashMap<String, List<Medicine>> stringListHashMap = new HashMap<>();
        ArrayList<MedicineRep> medicineReps = new ArrayList<>();
        for (Medicine item : list) {
            item.setMedicineCategoryName(Objects.requireNonNull(MedicineCategory.getName(item.getMedicineCategory())).getName());
            String key = item.getMedicineCategoryName();
            stringListHashMap.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
        for (String key : stringListHashMap.keySet()) {
            MedicineRep medicineRep = new MedicineRep();
            medicineRep.setMedicineCategoryName(key);
            medicineRep.setList(stringListHashMap.get(key));
            medicineReps.add(medicineRep);
        }
        return R.ok(medicineReps);
    }

    // 添加
    @PostMapping("/api/medicine/add")
    public R add(@RequestBody Medicine medicine) {
        medicine.setCreatetime(System.currentTimeMillis());
        medicineMapper.insert(medicine);
        return R.ok();
    }

    // 修改
    @PostMapping("/api/medicine/update")
    public R update(@RequestBody Medicine medicine) {
        medicineMapper.updateById(medicine);
        return R.ok();
    }

    // 删除
    @PostMapping("/api/medicine/delete")
    public R delete(@RequestBody Medicine medicine) {
        Dict params = Dict.create()
                .set("deletetime", System.currentTimeMillis())
                .set("ids", medicine.getIds());
        medicineMapper.deleteMany(params);
        return R.ok();
    }

    // 详情
    @PostMapping("/api/medicine/detail")
    public R detail(@RequestBody Medicine medicine) {
        Medicine entity = medicineMapper.selectById(medicine.getId());
        return R.ok(entity);
    }

    //下拉列表
    @PostMapping("/api/medicine/options")
    public R options() {
        List<Medicine> medicineList = medicineMapper.listOptions();
        List<Options> list = medicineList.stream().map(item -> {
            Options options = new Options();
            options.setLabel("下拉");
            options.setValue(item.getId());
            return options;
        }).toList();
        return R.ok(list);
    }
}