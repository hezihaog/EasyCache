package com.hzh.easy.cache.base;

import android.support.annotation.Nullable;

import com.hzh.easy.cache.interf.ICache;

import java.io.Serializable;

/**
 * Created by Hezihao on 2017/8/4.
 */

public abstract class BaseCache<P extends BaseCacheParams> implements ICache {
    private String versionSymbol;

    public CacheOperate getOperate() {
        return CacheOperate.getInstance();
    }

    @Override
    public int getVersionCode() {
        return getOperate().getVersionCode();
    }

    @Override
    public String getVersionSymbol() {
        if (versionSymbol != null) {
            return versionSymbol;
        } else {
            new StringBuilder()
                    .append("v")
                    .append(getVersionCode())
                    .append("_");
            return versionSymbol;
        }
    }

    public abstract void removeCache(@Nullable P params);

    public abstract void put(@Nullable P params, Serializable target);

    public abstract <T extends Serializable> T get(@Nullable P params);
}