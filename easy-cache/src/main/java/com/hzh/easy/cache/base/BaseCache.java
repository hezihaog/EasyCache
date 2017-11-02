package com.hzh.easy.cache.base;

import android.support.annotation.Nullable;
import android.util.LruCache;

import com.hzh.easy.cache.CacheOperate;
import com.hzh.easy.cache.util.ACache;

import java.io.Serializable;

/**
 * Created by Hezihao on 2017/8/4.
 */

public abstract class BaseCache<P extends BaseCacheParams> implements ICache {
    protected final CacheOperate operate;
    protected final ACache mDiskCache;
    protected final String versionCode;
    protected final LruCache<String, Object> mLruCache;

    public BaseCache() {
        operate = getOperate();
        mLruCache = operate.getLruCache();
        mDiskCache = operate.getDiskCache();
        versionCode = "v_".concat(String.valueOf(getVersionCode()).concat("_"));
    }

    @Override
    public CacheOperate getOperate() {
        return CacheOperate.getInstance();
    }

    protected int getVersionCode() {
        return getOperate().getAppVersionCode();
    }

    public abstract void removeCache(@Nullable P params);

    public abstract void put(@Nullable P params, Serializable target);

    public abstract <T extends Serializable> T get(@Nullable P params);
}