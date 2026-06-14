package com.fs.starter.admin;

import cn.dev33.satoken.dao.SaTokenDao;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试用 Sa-Token DAO（内存存储，不依赖 Redis）
 */
@TestConfiguration
public class TestSaTokenDaoConfig {

    private final ConcurrentHashMap<String, String> dataMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> objectMap = new ConcurrentHashMap<>();

    @Bean
    @Primary
    public SaTokenDao saTokenDao() {
        return new SaTokenDao() {
            @Override
            public String get(String key) {
                return dataMap.get(key);
            }

            @Override
            public void set(String key, String value, long timeout) {
                dataMap.put(key, value);
            }

            @Override
            public void update(String key, String value) {
                dataMap.put(key, value);
            }

            @Override
            public void delete(String key) {
                dataMap.remove(key);
            }

            @Override
            public long getTimeout(String key) {
                return dataMap.containsKey(key) ? 86400 : -2;
            }

            @Override
            public void updateTimeout(String key, long timeout) {
            }

            @Override
            public Object getObject(String key) {
                return objectMap.get(key);
            }

            @Override
            public void setObject(String key, Object object, long timeout) {
                objectMap.put(key, object);
            }

            @Override
            public void updateObject(String key, Object object) {
                objectMap.put(key, object);
            }

            @Override
            public void deleteObject(String key) {
                objectMap.remove(key);
            }

            @Override
            public long getObjectTimeout(String key) {
                return objectMap.containsKey(key) ? 86400 : -2;
            }

            @Override
            public void updateObjectTimeout(String key, long timeout) {
            }

            @Override
            public List<String> searchData(String prefix, String keyword, int count, int start, boolean isRemove) {
                List<String> list = new ArrayList<>();
                for (String key : dataMap.keySet()) {
                    if (key.startsWith(prefix) && (keyword == null || key.contains(keyword))) {
                        list.add(key);
                        if (count > 0 && list.size() >= count) break;
                    }
                }
                return list;
            }
        };
    }
}
