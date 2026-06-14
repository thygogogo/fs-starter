package com.fs.starter.config.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {

    /** 是否使用 mock openId */
    private boolean mock = true;

    /** 小程序 appid */
    private String appid;

    /** 小程序 secret */
    private String secret;

    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(appid);
        config.setSecret(secret);
        WxMaServiceImpl service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}
