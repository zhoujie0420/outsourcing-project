package com.gra.backend.controller;

import com.gra.backend.common.result.Result;
import com.gra.backend.pojo.Sign;
import com.gra.backend.service.SignService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/signing")
public class SigningRecordController {
    @Resource
    private SignService signService;

    @PostMapping("addSigning")
    public Result<?> addSigning(Sign signRecord) {
        return signService.addSign(signRecord);
    }

    @PostMapping("getSigning")
    public Result<?> getSigning() {
        return signService.getSign();
    }

    @PostMapping("updateSigning")
    public Result<?> updateSigning(Sign signRecord) {
        return signService.updateSign(signRecord);
    }
}
