package com.fs.starter.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类，所有实体继承此类
 */
@Data
public abstract class BaseDomain implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 操作者类型：1-管理员 2-微信用户 */
    @TableField(fill = FieldFill.INSERT)
    private Integer createByType;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 操作者类型：1-管理员 2-微信用户 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updateByType;

    @Version
    private Integer version;

    @TableLogic
    private Integer deleted;
}
