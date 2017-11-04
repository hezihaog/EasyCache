package com.hzh.easy.cache.base;

import com.hzh.easy.cache.interf.ICacheParams;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Hezihao on 2017/9/8.
 * Cache使用时，可附带参数
 */

public abstract class BaseCacheParams implements ICacheParams {
    private HashMap<String, Serializable> paramsMap;
    private String versionSymbol;

    @Override
    public int getVersionCode() {
        return CacheOperate.getInstance().getVersionCode();
    }

    @Override
    public String getVersionSymbol() {
        if (versionSymbol != null) {
            return versionSymbol;
        } else {
            versionSymbol = new StringBuilder()
                    .append("v")
                    .append(getVersionCode())
                    .append("_").toString();
            return versionSymbol;
        }
    }

    private HashMap<String, Serializable> getParamsMap() {
        if (paramsMap == null) {
            paramsMap = new HashMap<String, Serializable>();
        }
        return paramsMap;
    }

    @Override
    public <P extends ICacheParams> P put(String key, Serializable target) {
        getParamsMap().put(key, target);
        return (P) this;
    }

    @Override
    public <T extends Serializable> T get(String key) {
        return get(key, null);
    }

    @Override
    public <T extends Serializable> T get(String key, Serializable defaultValue) {
        T value = (T) getParamsMap().get(key);
        if (value == null) {
            return (T) defaultValue;
        } else {
            return value;
        }
    }
}
