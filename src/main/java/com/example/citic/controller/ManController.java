package com.example.citic.controller;


import com.example.citic.common.response.Result;
import com.example.citic.common.response.ResultCodeEnum;
import com.example.citic.dto.ManDirectoryDTO;
import com.example.citic.service.ManService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/man2html")

@Api("man在线手册查询")
public class ManController {

    @Resource
    ManService manService;

    @ApiOperation("查询某一路径man对应的具体html文件内容")
    @GetMapping("/getContent")
    public Result<String> getContent(@RequestParam String path){
        return Result.ok(manService.getContent(path));
    }

    @ApiOperation("查询路径")
    @GetMapping("/getPath")
    public Result<List<ManDirectoryDTO>> getDirectory(){
        return Result.build(manService.getDirectory(), ResultCodeEnum.SUCCESS);
    }

}
