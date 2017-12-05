package com.hzh.easy.cache.base;

import android.content.Context;
import android.util.LruCache;

import com.hzh.easy.cache.config.CacheConfig;
import com.hzh.easy.cache.util.ACache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hezihao on 2017/8/4.
 * 数据缓存，提供内存缓存和磁盘缓存数据
 */

public class CacheOperate {
    private LruCache<String, Object> mLruCache;
    private ACache mDiskCache;
    private Context context;
    private int versionCode;
    private static boolean isInited = false;

    private CacheOperate() {
    }

    private static class SingletonHolder {
        private static final CacheOperate instance = new CacheOperate();
    }

    /**
     * 初始化，会保存Context，最好传入ApplicationContext
     *
     * @return
     */
    public static CacheOperate init(CacheConfig config) {
        if (isInited) {
            return SingletonHolder.instance;
        } else {
            CacheOperate instance = SingletonHolder.instance;
            instance.context = config.getContext();
            instance.versionCode = config.getVersionCode();
            instance.mLruCache = new LruCache<String, Object>(config.getMemoryMaxSize());
            instance.mDiskCache = ACache.get(config.getContext(), config.getCacheFileName()
                    , config.getDiskMaxSize(), config.getDiskMaxCount());
            isInited = true;
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

    public int getVersionCode() {
        return versionCode;
    }

    /**
     * 永久保存集合对象
     *
     * @param key
     * @param list
     * @param <T>
     */
    public <T extends Serializable> void putListData(String key, List<T> list) {
        putListData(key, list, -1);
    }

    public <T extends Serializable> void putListData(String key, List<T> list, int saveTime) {
        ArrayList<T> dataList = new ArrayList<T>(list);
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
    public <T extends Serializable> void putData(String key, T data) {
        putData(key, data, -1);
    }

    public <T extends Serializable> void putData(String key, T data, int saveTime) {
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
    public <T extends Serializable> T getData(String key) {
        return getData(key, null);
    }

    public <T extends Serializable> T getData(String key, T defaultValue) {
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
     * 按key在内存缓存中移除
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T extends Serializable> T removeMemoryCache(String key) {
        return (T) mLruCache.remove(key);
    }

    /**
     * 移除磁盘缓存
     *
     * @param key
     * @return
     */
    public <T extends Serializable> T removeDiskCache(String key) {
        Object cache = mDiskCache.getAsObject(key);
        if (cache != null) {
            mDiskCache.remove(key);
            return (T) cache;
        } else {
            return null;
        }
    }

    /**
     * 移除内存缓存和磁盘缓存
     */
    public void removeCache(String key) {
        removeMemoryCache(key);
        removeDiskCache(key);
    }

    /**
     * 移除所有Lru内存缓存
     */
    public void removeAllMemoryCache() {
        mLruCache.evictAll();
    }

    /**
     * 移除所有Disk磁盘缓存
     */
    public void removeAllDiskCache() {
        mDiskCache.clear();
    }
}
