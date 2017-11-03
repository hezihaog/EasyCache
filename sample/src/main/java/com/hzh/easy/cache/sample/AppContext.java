package com.hzh.easy.cache.sample;

import android.app.Application;
import android.os.Handler;

/**
 * @package com.hzh.easy.cache.sample
 * @fileName AppContext
 * @date on 2017/11/3  下午5:33
 * @auther 子和
 * @descirbe TODO
 * @email hezihao@linghit.com
 */

public class AppContext extends Application {
    private static AppContext instance;
    private Handler mainHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mainHandler = new Handler(getMainLooper());
    }

    public static AppContext getInstance() {
        return instance;
    }

    public void post(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public void postDelayed(Runnable runnable, long delayMillis) {
        mainHandler.postDelayed(runnable, delayMillis);
    }
}
