package com.example.citic.dto;

import com.example.citic.service.ManService;
import lombok.Data;

import java.util.List;

@Data
public class ManDirectoryDTO {
    private  String path;
    private String title;
    private List<ManDirectoryDTO> children;
}
