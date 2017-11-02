package com.hzh.easy.cache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

import com.hzh.easy.cache.util.ACache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hezihao on 2017/8/4.
 * 数据缓存，提供内存缓存和磁盘缓存数据
 */

public class CacheOperate {
    private static final int M = 1024 * 1024;
    private LruCache<String, Object> mLruCache;
    private ACache mDiskCache;
    private Context context;
    private int appVersionCode;
    private static boolean isInited = false;

    private CacheOperate() {
    }

    private static class SingletonHolder {
        private static final CacheOperate instance = new CacheOperate();
    }

    public static CacheOperate init(Context context, int appVersionCode) {
        if (isInited) {
            return getInstance();
        } else {
            CacheOperate instance = getInstance();
            instance.context = context;
            instance.appVersionCode = appVersionCode;
            instance.mLruCache = new LruCache<String, Object>(5 * M);
            instance.mDiskCache = ACache.get(context);
            return instance;
        }
    }

    public static CacheOperate getInstance() {
        if (!isInited) {
            throw new IllegalStateException("必须先调用init()，初始化");
        }
        return SingletonHolder.instance;
    }

    public LruCache<String, Object> getLruCache() {
        return mLruCache;
    }

    public ACache getDiskCache() {
        return mDiskCache;
    }

    public Context getContext() {
        return context;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    /**
     * 永久保存集合对象
     *
     * @param key
     * @param list
     * @param <T>
     */
    public <T extends Serializable> void saveListData(@NonNull String key, @NonNull List<T> list) {
        saveListData(key, list, -1);
    }

    public <T extends Serializable> void saveListData(@NonNull String key, @NonNull List<T> list, @Nullable int saveTime) {
        ArrayList<T> dataList = (ArrayList<T>) list;
        mLruCache.put(key, list);
        mDiskCache.put(key, dataList, saveTime);
    }

    /**
     * 永久保存序列化数据
     *
     * @param key
     * @param data
     * @param <T>
     */
    public <T extends Serializable> void putData(@NonNull String key, @NonNull T data) {
        putData(key, data, -1);
    }

    public <T extends Serializable> void putData(@NonNull String key, @NonNull T data, @Nullable int saveTime) {
        mLruCache.put(key, data);
        mDiskCache.put(key, data, saveTime);
    }

    /**
     * 获取数据
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Serializable> T getData(@NonNull String key) {
        return getData(key, null);
    }

    public <T extends Serializable> T getData(@NonNull String key, @Nullable T defaultValue) {
        T result = (T) mLruCache.get(key);
        if (result == null) {
            result = (T) mDiskCache.getAsObject(key);
            if (result == null && defaultValue != null) {
                result = defaultValue;
            }
            if (result != null) {
                mLruCache.put(key, result);
            } else {
                return defaultValue;
            }
        }
        return result;
    }

    /**
     * 移除磁盘缓存
     *
     * @param key
     * @return
     */
    public boolean removeDiskCache(@NonNull String key) {
        return mDiskCache.remove(key);
    }

    /**
     * 按key移除缓存
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Serializable> T removeCache(@NonNull String key) {
        return (T) mLruCache.remove(key);
    }

    /**
     * 移除所有lru内存缓存
     */
    public void removeAllCache() {
        mLruCache.evictAll();
    }
}
