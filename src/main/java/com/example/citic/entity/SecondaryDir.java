package com.example.citic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("secondary_dir")
public class SecondaryDir implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String subdirectoryName;
    private String primaryDirectoryName;
    private String content;


}
