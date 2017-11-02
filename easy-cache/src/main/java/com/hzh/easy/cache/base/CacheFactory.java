package com.hzh.easy.cache.base;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hezihao on 2017/8/5.
 */

public class CacheFactory {
    private static final ConcurrentHashMap<Class<?>, ICache> mCacheMap = new ConcurrentHashMap<Class<?>, ICache>();

    private CacheFactory() {
    }

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
}
