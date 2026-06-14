package com.fs.starter.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseDomain {
    private String fileHash;
    private String url;
    private String originalName;
    private Long fileSize;
}
