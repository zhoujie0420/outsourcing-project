package com.lhqjlb.project.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lhqjlb.project.util.PageVO;
import com.lhqjlb.project.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhqjlb.project.entity.Cart;
import com.lhqjlb.project.entity.Medicine;
import com.lhqjlb.project.entity.Userr;
import com.lhqjlb.project.mapper.CartMapper;
import com.lhqjlb.project.mapper.MedicineMapper;
import com.lhqjlb.project.mapper.UserrMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cn.hutool.core.lang.Dict;
import java.util.List;
import com.lhqjlb.project.entity.Options;

/**
 * 购物车
 */
@Slf4j
@RestController
public class CartController {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private MedicineMapper medicineMapper;
    @Autowired
    private UserrMapper userrMapper;

    // 分页
    @PostMapping("/api/cart/page")
    public R page(@RequestBody Cart cart) {
        Dict params = Dict.create();
        params.set("userid", StpUtil.getLoginIdAsLong());
        Page<Cart> page = cartMapper.page(
                new Page<>(cart.getPageNum(), cart.getPageSize()),
                params
        );
        for (Cart item : page.getRecords()) {
            Long medicineid = item.getMedicineid();
            Medicine medicine = medicineMapper.selectById(medicineid);
            item.setMedicine(medicine);
            Userr shop = userrMapper.selectById(medicine.getShopid());
            item.setShop(shop);
        }
        return R.ok(new PageVO(page.getTotal(), page.getRecords()));
    }

    // 列表
    @PostMapping("/api/cart/list")
    public R list(@RequestBody Cart cart) {
        Dict params = Dict.create();
        List<Cart> list = cartMapper.list(params);
        return R.ok(list);
    }

    /**
     * 添加购物车项接口。
     *
     * @param cart 购物车项信息
     * @return 返回操作结果
     */
    @PostMapping("/api/cart/add")
    public R add(@RequestBody Cart cart) {
        // 获取当前登录用户的用户ID
        long userid = StpUtil.getLoginIdAsLong();
        // 获取购物车项中药品的ID
        Long medicineid = cart.getMedicineid();
        // 创建查询参数对象
        Dict params = Dict.create();
        params.set("userid",userid);
        params.set("medicineid",medicineid);
        // 根据用户ID和药品ID查询购物车中是否存在该项
        Cart cart1 = cartMapper.getCart(params);
        // 如果购物车中已存在该项，则增加数量；否则新增购物车项
        if (cart1 != null){
            cart1.setCnt(cart1.getCnt() + 1);
            cartMapper.updateById(cart1);
        }else{
            cart.setCreatetime(System.currentTimeMillis());
            cart.setUserid(userid);
            cart.setCnt(1);
            cartMapper.insert(cart);
        }
        // 返回操作成功结果
        return R.ok();
    }


    // 修改
    @PostMapping("/api/cart/update")
    public R update(@RequestBody Cart cart) {
        cartMapper.updateById(cart);
        return R.ok();
    }

    // 删除
    @PostMapping("/api/cart/delete")
    public R delete(@RequestBody Cart cart) {
        Dict params = Dict.create()
                        .set("deletetime", System.currentTimeMillis())
                        .set("ids", cart.getIds());
        cartMapper.deleteMany(params);
        return R.ok();
    }

    // 详情
    @PostMapping("/api/cart/detail")
    public R detail(@RequestBody Cart cart) {
        Cart entity = cartMapper.selectById(cart.getId());
        return R.ok(entity);
    }
    
    //下拉列表
    @PostMapping("/api/cart/options")
    public R options() {
        List<Cart> cartList = cartMapper.listOptions();
        List<Options> list = cartList.stream().map(item -> {
            Options options = new Options();
            options.setLabel("下拉");
            options.setValue(item.getId());
            return options;
        }).toList();
        return R.ok(list);
    }
}