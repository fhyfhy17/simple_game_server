package com.util;

import com.esotericsoftware.minlog.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PropertiesUtil {
    private static Map<String, Prop> configMap = new ConcurrentHashMap<>();

    private static void check(String fileName) {
        if (!configMap.containsKey(fileName)) {
            Prop cfg = new Prop(fileName);
            configMap.put(fileName, cfg);
        }
    }


    // 读String
    public static String getStringValue(String fileName, String key) {
        check(fileName);
        return configMap.get(fileName).getStringValue(key);
    }// 读int

    public static int getIntValue(String fileName, String key) {
        check(fileName);
        return configMap.get(fileName).getIntValue(key);
    }

    // 读boolean
    public static boolean getBooleanValue(String fileName, String key) {
        check(fileName);
        return configMap.get(fileName).getBooleanValue(key);
    }

    // 读List
    public static List<?> getListValue(String fileName, String key) {
        check(fileName);
        return configMap.get(fileName).getListValue(key);
    }

    // 读数组
    public String[] getArrayValue(String fileName, String key) {
        check(fileName);
        return configMap.get(fileName).getArrayValue(key);
    }


    public static class Prop {
        PropertiesConfiguration cfg = null;

        Prop(String fileName) {
            init(fileName);
        }

        public void init(String fileName) {
            try {
                cfg = new PropertiesConfiguration(fileName);
            } catch (ConfigurationException e) {
                Log.error("", e);
            }
            // 当文件的内容发生改变时，配置对象也会刷新
            cfg.setReloadingStrategy(new FileChangedReloadingStrategy());

        }


        // 读String
        public String getStringValue(String key) {
            return cfg.getString(key);
        }// 读int

        public int getIntValue(String key) {
            return cfg.getInt(key);
        }

        // 读boolean
        public boolean getBooleanValue(String key) {
            return cfg.getBoolean(key);
        }

        // 读List
        public List<?> getListValue(String key) {
            return cfg.getList(key);
        }

        // 读数组
        public String[] getArrayValue(String key) {
            return cfg.getStringArray(key);
        }

    }
}
