package com.gra.backend.controller.utils;

import com.gra.backend.service.utils.UtilsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@AllArgsConstructor
public class UtilsController {

    private final UtilsService utilsService;

    @GetMapping("test")
    public String sendEmail(){
        utilsService.sendEmail();
        return "success";
    }

}
