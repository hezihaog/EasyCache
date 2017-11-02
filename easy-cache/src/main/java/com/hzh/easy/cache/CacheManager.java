package com.hzh.easy.cache;

import android.content.Context;
import android.content.pm.PackageManager;

import com.hzh.easy.cache.base.CacheOperate;

public class CacheManager {
    private CacheManager() {
    }

    private static final class SingletonHolder {
        private static final CacheManager instance = new CacheManager();
    }

    public static CacheManager getInstance() {
        return SingletonHolder.instance;
    }

    public void init(Context context) {
        if (context == null) {
            throw new NullPointerException("context 不能为空");
        }
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        CacheOperate.init(context, versionCode);
    }
}