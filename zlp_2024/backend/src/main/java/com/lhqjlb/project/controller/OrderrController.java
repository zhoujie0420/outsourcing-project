package com.lhqjlb.project.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.lhqjlb.project.entity.*;
import com.lhqjlb.project.util.JsonUtil;
import com.lhqjlb.project.util.PageVO;
import com.lhqjlb.project.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhqjlb.project.entity.*;
import com.lhqjlb.project.mapper.CartMapper;
import com.lhqjlb.project.mapper.MedicineMapper;
import com.lhqjlb.project.mapper.OrderrMapper;
import com.lhqjlb.project.mapper.UserrMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.lang.Dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单
 */
@Slf4j
@RestController
public class OrderrController {

    @Autowired
    private OrderrMapper orderrMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private MedicineMapper medicineMapper;
    @Autowired
    private UserrMapper userrMapper;

    /**
     * 分页查询订单信息
     *
     * @param orderr 包含查询条件的订单对象
     * @return 包含分页查询结果的响应对象
     */
    @PostMapping("/api/orderr/page")
    public R page(@RequestBody Orderr orderr) {
        // 创建查询参数对象
        Dict params = Dict.create();
        params.set("userid", orderr.getUserid());
        params.set("shopid", orderr.getShopid());
        params.set("statuss", orderr.getStatuss());
        params.set("orderno", orderr.getOrderno());

        // 分页查询订单信息
        Page<Orderr> page = orderrMapper.page(
                new Page<>(orderr.getPageNum(), orderr.getPageSize()),
                params
        );

        // 遍历查询结果进行处理
        for (Orderr item : page.getRecords()) {
            // 设置查询子订单的参数
            params.clear();
            params.set("orderno", item.getOrderno());

            // 查询子订单列表
            List<Orderr> list = orderrMapper.list(params);
            Orderr orderr1 = list.get(0);
            BeanUtils.copyProperties(orderr1, item);
            item.setSub(list); // 设置子订单列表

            // 计算订单总价
            int totalPrice = list.stream()
                    .mapToInt(med -> med.getCnt() * med.getPrice())
                    .sum();
            item.setTotalPrice(totalPrice); // 设置订单总价

            // 查询订单所属用户信息
            Userr userr = userrMapper.selectById(item.getUserid());
            item.setUser(userr); // 设置订单用户信息

            // 查询订单所属商店信息
            Userr shop = userrMapper.selectById(item.getShopid());
            item.setShop(shop); // 设置订单商店信息

            Dict address = JsonUtil.toBean(item.getAddcontent(), Dict.class);
            item.setAddress(address);
        }

        // 返回包含分页查询结果的响应对象
        return R.ok(new PageVO(page.getTotal(), page.getRecords()));
    }


    // 列表
    @PostMapping("/api/orderr/list")
    public R list(@RequestBody Orderr orderr) {
        Dict params = Dict.create();
        List<Orderr> list = orderrMapper.list(params);
        return R.ok(list);
    }

    /**
     * 添加订单信息
     *
     * @param orderr 包含订单信息的对象
     * @return R.ok() 表示添加订单成功
     */
    @Transactional
    @PostMapping("/api/orderr/add")
    public R add(@RequestBody Orderr orderr) {
        // 打印接收到的订单信息
        System.out.println(JsonUtil.toJsonStr(orderr));

        // 获取当前登录用户的ID
        long userid = StpUtil.getLoginIdAsLong();

        // 获取订单中商品的ID列表
        List<Long> ids = orderr.getIds();

        // 创建一个 Map 用于存储商店ID和对应的药品列表
        Map<Long, List<Medicine>> medicineMap = new HashMap<>();

        // 根据订单中商品的ID，查询对应的购物车信息并处理
        for (Long id : ids) {
            Cart cart = cartMapper.selectById(id); // 根据购物车ID查询购物车信息
            Medicine medicine = medicineMapper.selectById(cart.getMedicineid()); // 根据购物车中药品ID查询药品信息
            medicine.setCnt(cart.getCnt()); // 设置药品的数量
            List<Medicine> list = medicineMap.getOrDefault(medicine.getShopid(), new ArrayList<>()); // 获取商店ID对应的药品列表，如果不存在则新建一个空列表
            list.add(medicine); // 将药品添加到列表中
            medicineMap.put(medicine.getShopid(), list);
        }

        // 遍历药品Map，生成订单并插入数据库
        for (Map.Entry<Long, List<Medicine>> entry : medicineMap.entrySet()) {
            List<Medicine> value = entry.getValue(); // 获取商店对应的药品列表
            String orderno = IdUtil.getSnowflakeNextIdStr(); // 生成订单号
            for (Medicine medicine : value) {
                // 创建订单对象并设置属性
                Orderr entity = new Orderr();
                entity.setShopid(medicine.getShopid());
                entity.setNamee(medicine.getNamee());
                entity.setPrice(medicine.getPrice());
                entity.setCnt(medicine.getCnt());
                entity.setUserid(userid);
                entity.setMedicineid(medicine.getId());
                entity.setAddcontent(orderr.getAddcontent());
                entity.setOrderno(orderno);
                entity.setCreatetime(System.currentTimeMillis());
                // 插入订单信息到数据库
                orderrMapper.insert(entity);
            }
        }

        //清空购物车对应的数据
        Dict params = Dict.create()
                .set("deletetime", System.currentTimeMillis())
                .set("ids", orderr.getIds());
        cartMapper.deleteMany(params);

        // 返回添加订单成功的响应
        return R.ok();
    }

    /**
     * 更新订单状态
     *
     * @param orderr 包含更新状态的订单对象
     * @return R.ok() 表示更新订单状态成功
     */
    @Transactional
    @PostMapping("/api/orderr/updateStatus")
    public R updateStatus(@RequestBody Orderr orderr) {
        // 根据订单ID查询订单信息
        Orderr entity = orderrMapper.selectById(orderr.getId());

        // 创建查询参数对象
        Dict params = Dict.create();
        params.set("orderno", entity.getOrderno());

        // 根据订单号查询相关订单列表
        List<Orderr> list = orderrMapper.list(params);

        // 遍历订单列表，更新订单状态
        for (Orderr item : list) {
            item.setStatuss(orderr.getStatuss()); // 设置订单状态
            orderrMapper.updateById(item); // 更新订单信息
        }

        // 返回更新订单状态成功的响应
        return R.ok();
    }


    /**
     * 更新评价
     */
    @Transactional
    @PostMapping("/api/orderr/updateRate")
    public R updateRate(@RequestBody Orderr orderr) {
        // 根据订单ID查询订单信息
        Orderr entity = orderrMapper.selectById(orderr.getId());

        // 创建查询参数对象
        Dict params = Dict.create();
        params.set("orderno", entity.getOrderno());

        // 根据订单号查询相关订单列表
        List<Orderr> list = orderrMapper.list(params);

        // 遍历订单列表，更新订单
        for (Orderr item : list) {
            item.setContent(orderr.getContent()); // 设置订单
            item.setRate(orderr.getRate()); // 设置订单
            item.setEvastatus("已评价"); // 设置订单
            orderrMapper.updateById(item); // 更新订单信息
        }

        // 返回更新订单成功的响应
        return R.ok();
    }

    /**
     * 更新评价
     */
    @Transactional
    @PostMapping("/api/orderr/updateComments")
    public R updateComments(@RequestBody Orderr orderr) {
        // 根据订单ID查询订单信息
        Orderr entity = orderrMapper.selectById(orderr.getId());

        // 创建查询参数对象
        Dict params = Dict.create();
        params.set("orderno", entity.getOrderno());

        // 根据订单号查询相关订单列表
        List<Orderr> list = orderrMapper.list(params);

        // 遍历订单列表，更新订单
        for (Orderr item : list) {
            item.setShowcontent("否"); // 设置订单
            orderrMapper.updateById(item); // 更新订单信息
        }

        // 返回更新订单成功的响应
        return R.ok();
    }


    // 删除
    @PostMapping("/api/orderr/delete")
    public R delete(@RequestBody Orderr orderr) {
        Dict params = Dict.create();
        List<Long> ids = orderr.getIds();
        List<Long> deleteIds = new ArrayList<>();
        for (Long id : ids) {
            Orderr orderr1 = orderrMapper.selectById(id);
            String orderno = orderr1.getOrderno();
            params.clear();
            params.set("orderno", orderno);
            List<Orderr> list = orderrMapper.list(params);
            for (Orderr item : list) {
                deleteIds.add(item.getId());
            }
        }
        params.clear();
        params.set("deletetime", System.currentTimeMillis());
        params.set("ids", deleteIds);
        orderrMapper.deleteMany(params);
        return R.ok();
    }

    // 详情
    @PostMapping("/api/orderr/detail")
    public R detail(@RequestBody Orderr orderr) {
        Orderr entity = orderrMapper.selectById(orderr.getId());
        return R.ok(entity);
    }

    //下拉列表
    @PostMapping("/api/orderr/options")
    public R options() {
        List<Orderr> orderrList = orderrMapper.listOptions();
        List<Options> list = orderrList.stream().map(item -> {
            Options options = new Options();
            options.setLabel("下拉");
            options.setValue(item.getId());
            return options;
        }).toList();
        return R.ok(list);
    }

    //销量
    @PostMapping("/api/statistics/salesVolume")
    public R salesVolume() {
        List<Dict> list = orderrMapper.listSalesVolume();
        return R.ok(list);
    }


    //销售额
    @PostMapping("/api/statistics/salesRevenue")
    public R salesRevenue() {
        Dict set = Dict.create().set("shopid", StpUtil.getLoginIdAsLong());
        List<Dict> list = orderrMapper.listSalesRevenue(set);
        return R.ok(list);
    }

    //评价
    @PostMapping("/api/order/evaluation")
    public R evaluation(@RequestBody Orderr orderr) {
        Long medicineid = orderr.getMedicineid();
        Dict set = Dict.create().set("medicineid", medicineid);
        List<Orderr> orderrs = orderrMapper.listEvaluation(set);
        for (Orderr orderr1 : orderrs) {
            Userr userr = userrMapper.selectById(orderr1.getUserid());
            orderr1.setUser(userr);
        }
        return R.ok(orderrs);
    }
}