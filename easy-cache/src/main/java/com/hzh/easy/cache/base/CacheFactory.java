package com.hzh.easy.cache.base;

import com.hzh.easy.cache.interf.ICache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hezihao on 2017/8/5.
 */

public class CacheFactory {
    /**
     * 缓存Map，缓存ICache的实例，以子类的Class作为Key，ICache实例作为Value
     */
    private static final ConcurrentHashMap<Class<?>, ICache> mCacheMap = new ConcurrentHashMap<Class<?>, ICache>();

    private CacheFactory() {
    }

    /**
     * 反射构造BaseCache子类，并存入到缓存Map中，如果在缓存中有，则直接返回缓存中缓存实例
     *
     * @param clazz 要构造的BaseCache的Class
     * @param <T>   泛型，限制为ICache的实现类
     * @return 返回要构造的BaseCache的子类
     */
    public static <T extends ICache> T create(Class<T> clazz) {
        ICache cache = mCacheMap.get(clazz);
        if (cache == null) {
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                cache = constructor.newInstance();
                mCacheMap.put(clazz, cache);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return (T) cache;
    }

    /**
     * 从缓存中移除缓存实例
     *
     * @param clazz 缓存的子类Class
     * @param <T>   泛型，限制ICache的子类
     * @return 返回移除前缓存的实例
     */
    public static <T extends ICache> T removeCache(Class<T> clazz) {
        ICache cache = mCacheMap.get(clazz);
        if (cache != null) {
            mCacheMap.remove(clazz);
            return (T) cache;
        } else {
            return null;
        }
    }

    public static void removeAllCache() {
        mCacheMap.clear();
    }
}
