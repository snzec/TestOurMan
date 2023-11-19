package com.example.citic.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("first_directories")
public class FirstDirectories implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String directoryName;

}
