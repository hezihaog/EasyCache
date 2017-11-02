package com.hzh.easy.cache;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * @包名 com.hzh.easy.cache
 * @类名 CacheManager
 * @创建时间 on 2017/11/2  下午2:42
 * @作者 子和
 * @类的描述 TODO
 * Email hezihao@linghit.com
 * 最后更新者 CacheManager
 * 最后修改时间：2017/11/2  下午2:42
 */

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
