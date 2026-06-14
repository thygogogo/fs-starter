package com.fs.starter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fs.starter.domain.entity.WechatUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 微信用户 Mapper
 */
public interface WechatUserMapper extends BaseMapper<WechatUser> {

    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count FROM wx_user " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND deleted = 0 " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> countNewUsersLast7Days();
}
