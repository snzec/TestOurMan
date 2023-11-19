package com.example.citic.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.citic.dto.ManDirectoryDTO;
import com.example.citic.entity.SecondaryDir;
import com.example.citic.mapper.ManMapper;
import com.example.citic.service.ManService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ManServiceImpl extends ServiceImpl<ManMapper, SecondaryDir> implements ManService {

    @Override
    public String getContent(String path) {
        String[] paths = path.split("-", 2);
        LambdaQueryWrapper<SecondaryDir> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecondaryDir::getPrimaryDirectoryName,paths[0]).eq(SecondaryDir::getSubdirectoryName,paths[1]);
        SecondaryDir content = this.getOne(queryWrapper);
        //解析
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new StringReader(content.getContent()))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(ManToHtmlConverter.hyperLink(line));
                //System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();

    }

    @Override
    public List<ManDirectoryDTO> getDirectory() {
        List<ManDirectoryDTO> result = new ArrayList<>();
        //todo 写mapper直接去重
        QueryWrapper<SecondaryDir> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT primary_directory_name");
        List<SecondaryDir> father = baseMapper.selectList(queryWrapper);
        List<String> father_path  = new ArrayList<>();
        for (SecondaryDir tmp:father){
            father_path.add(tmp.getPrimaryDirectoryName());
        }

        for (String s:father_path){

            ManDirectoryDTO fatherManDirectoryDTO = new ManDirectoryDTO();
            List<ManDirectoryDTO> childrenList = new ArrayList<>();
            LambdaQueryWrapper<SecondaryDir> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SecondaryDir::getPrimaryDirectoryName,s);
            fatherManDirectoryDTO.setPath(s);
            fatherManDirectoryDTO.setTitle(s);
            //只查询父目录对应的子目录信息
            lambdaQueryWrapper.select(SecondaryDir::getSubdirectoryName);
            List<SecondaryDir> secondaryDirs = this.list(lambdaQueryWrapper);
            for(SecondaryDir childern:secondaryDirs){
                ManDirectoryDTO childernManDirectoryDTO = new ManDirectoryDTO();
                childernManDirectoryDTO.setPath(s+"-"+childern.getSubdirectoryName());
                childernManDirectoryDTO.setTitle(childern.getSubdirectoryName());
                childrenList.add(childernManDirectoryDTO);
            }
            fatherManDirectoryDTO.setChildren(childrenList);
            result.add(fatherManDirectoryDTO);
        }
       return result;
    }

}
