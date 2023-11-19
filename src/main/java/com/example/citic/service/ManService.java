package com.example.citic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.citic.dto.ManDirectoryDTO;
import com.example.citic.entity.SecondaryDir;

import java.util.List;

public interface ManService extends IService<SecondaryDir> {
    String getContent(String path);

    List<ManDirectoryDTO> getDirectory();
}
