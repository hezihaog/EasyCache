package com.hzh.easy.cache.base;

import android.support.v4.util.ArrayMap;

import java.io.Serializable;

/**
 * Created by Hezihao on 2017/9/8.
 * Cache使用时，可附带参数
 */

public abstract class BaseCacheParams implements ICacheParams {
    protected final String versionSymbol;

    public BaseCacheParams() {
        versionSymbol = "v_".concat(String.valueOf(getVersionCode()).concat("_"));
    }

    private class CacheMap extends ArrayMap implements Serializable {
    }

    private CacheMap paramsMap = new CacheMap();

    protected static int getVersionCode() {
        return CacheOperate.getInstance().getAppVersionCode();
    }

    protected <P extends BaseCacheParams> P put(String key, Serializable target) {
        paramsMap.put(key, target);
        return (P) this;
    }

    protected <T extends Serializable> T get(String key) {
        return (T) paramsMap.get(key);
    }
}
