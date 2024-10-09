package com.lhqjlb.project.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lhqjlb.project.util.MD5Util;
import com.lhqjlb.project.util.R;
import com.lhqjlb.project.entity.Userr;
import com.lhqjlb.project.mapper.UserrMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class ProjectController {

    @Autowired
    private UserrMapper userrMapper;
    @Autowired
    private HttpServletResponse response;

    // 退出登录
    @PostMapping("/api/logout")
    public R logout() {
        StpUtil.logout();
        return R.ok();
    }

    // 修改自己的密码
    @PostMapping("/api/updatePwd")
    public R updatePwd(@RequestBody Userr userr) {
        String oldpwd = userr.getOldpwd();
        String newpwd = userr.getNewpwd();
        Userr user = userrMapper.selectById(StpUtil.getLoginIdAsLong());
        if (!user.getPwd().equals(MD5Util.encrypt(oldpwd))) {
            return R.error("旧密码不正确");
        }
        user.setPwd(MD5Util.encrypt(newpwd));
        userrMapper.updateById(user);
        return R.ok();
    }

    // 后台登录
    // String username
    // String pwd
    @PostMapping("/login")
    public R login(@RequestBody Userr userr) {
        userr.setPwd(MD5Util.encrypt(userr.getPwd()));
        Userr user = userrMapper.login(userr);
        if (user == null) {
            return R.error("账号或密码不正确");
        }
        StpUtil.login(user.getId());
        StpUtil.getSession().set("typee", user.getTypee());
        StpUtil.getSession().set("isAdmin", user.getTypee().equals("管理员"));
        user.setToken(StpUtil.getTokenValue());
        return R.ok(user);
    }

    // 微信登录
    // String username
    // String pwd
    @PostMapping("/wxLogin")
    public R wxLogin(@RequestBody Userr userr) {
        userr.setPwd(MD5Util.encrypt(userr.getPwd()));
        Userr user = userrMapper.login(userr);
        if (user == null) {
            return R.error("账号或密码不正确");
        }
        StpUtil.login(user.getId());
        StpUtil.getSession().set("typee", user.getTypee());
        user.setToken(StpUtil.getTokenValue());
        return R.ok(user);
    }

    // 上传
    @PostMapping("/upload")
    public R uploadFile(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("参数错误");
        }
        String dir = System.getProperty("user.dir") + "/upload";
        String originalFilename = file.getOriginalFilename();
        String path = StrUtil.format("{}/{}/{}", dir, IdUtil.getSnowflakeNextIdStr(), originalFilename);
        File newFile = new File(path);
        newFile.mkdirs();
        try {
            file.transferTo(newFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return R.error("上传失败");
        }
        return R.ok(Dict.create()
                .set("name", file.getOriginalFilename())
                .set("url", "http://localhost:8080/file?path=" + path.replace(dir, "/upload")));
    }

    //获取文件
    @SneakyThrows
    @GetMapping("/file")
    public void file(String path) {
        File file = new File(System.getProperty("user.dir") + path);
        if (file.exists()) {
            response.getOutputStream().write(FileUtil.readBytes(file));
        }
    }

    // 上传
    @PostMapping("/upload2")
    public Dict uploadFile2(@RequestParam("wangeditor-uploaded-image") MultipartFile file) {
        Dict ret = Dict.create();
        if (file.isEmpty()) {
            return ret.set("errno", 1);
        }
        String dir = System.getProperty("user.dir") + "/upload";
        String originalFilename = file.getOriginalFilename();
        String path = StrUtil.format("{}/{}/{}", dir, IdUtil.getSnowflakeNextIdStr(), originalFilename);
        File newFile = new File(path);
        newFile.mkdirs();
        try {
            file.transferTo(newFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ret.set("errno", 1);
        }
        return ret.set("errno", 0)
                .set("data", Dict.create()
                        .set("url", "http://localhost:8080/file?path=" + path.replace(dir, "/upload"))
                );
    }

    //注册
    @Transactional
    @PostMapping("/reg")
    public R reg(@RequestBody Userr userr) {
        List<Userr> userList = userrMapper.listByUsername(Dict.create().set("username", userr.getUsername()));
        if (!userList.isEmpty()) {
            return R.error("用户名已存在");
        }
        if (Objects.equals(userr.getTypee(), "用户")) {
            userr.setStatuss("已通过");
        } else {
            userr.setStatuss("审核中");
        }
        userr.setPwd(MD5Util.encrypt(userr.getPwd()));
        userr.setCreatetime(System.currentTimeMillis());
        userrMapper.insert(userr);
        return R.ok();
    }
}
