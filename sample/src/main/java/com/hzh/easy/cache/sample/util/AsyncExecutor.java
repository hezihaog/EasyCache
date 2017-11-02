package com.hzh.easy.cache.sample.util;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SparseArrayCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncExecutor implements Handler.Callback {
    public static final int ON_CREATE = 11;
    public static final int ON_START = 12;
    public static final int ON_RESUME = 13;
    public static final int ON_PAUSE = 14;
    public static final int ON_STOP = 15;
    public static final int ON_DESTROY = 16;

    private Handler mainHandler;
    private ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ArrayMap<Integer, AsyncCallback> commonCallbackCache = new ArrayMap<Integer, AsyncCallback>();
    private SparseArrayCompat<List<AsyncCallback>> activityCallbackCache = new SparseArrayCompat<List<AsyncCallback>>();
    private Lifecycle lifecycle;
    private int stopOnLifecycleEvent = ON_DESTROY;

    private static class InnerHolder {
        static final AsyncExecutor executor = new AsyncExecutor();
    }

    public static AsyncExecutor getInstance() {
        return InnerHolder.executor;
    }

    private AsyncExecutor() {
        mainHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mainHandler.getLooper().getThread() == Thread.currentThread()) {
            if (msg.obj instanceof AsyncCallback) {
                AsyncCallback callback = (AsyncCallback) msg.obj;
                if (callback.stop) return true;
                callback.runAfter(callback.t);
                commonCallbackCache.remove(msg.what);
                if (activityCallbackCache.indexOfKey(msg.what) >= 0) {
                    List<AsyncCallback> list = activityCallbackCache.get(msg.what);
                    list.remove(callback);
                    if (list.isEmpty()) {
                        activityCallbackCache.remove(msg.what);
                    }
                }
            }
        }
        return true;
    }

    public void execute(final AsyncCallback<?> callback) {
        execute(null, callback);
    }

    public void execute(final Activity activity, final AsyncCallback<?> callback) {
        if (callback == null) return;
        int hashCode;
        if (activity == null) {
            hashCode = callback.hashCode();
            AsyncCallback cacheCallback = commonCallbackCache.get(hashCode);
            if (cacheCallback == null) {
                commonCallbackCache.put(hashCode, callback);
            }
        } else {
            hashCode = activity.hashCode();
            if (lifecycle == null) {
                synchronized (AsyncExecutor.class) {
                    if (lifecycle == null) {
                        lifecycle = new Lifecycle();
                        activity.getApplication().registerActivityLifecycleCallbacks(lifecycle);
                    }
                }
            }
            List<AsyncCallback> list = activityCallbackCache.get(hashCode);
            if (list == null) {
                list = new ArrayList<AsyncCallback>();
                activityCallbackCache.put(hashCode, list);
            }
            if (!list.contains(callback)) {
                list.add(callback);
            }
        }
        final int what = hashCode;
        callback.runBefore();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (callback.stop) return;
                callback.t = callback.running();
                mainHandler.obtainMessage(what, callback).sendToTarget();
            }
        });
    }

    public void cancel(AsyncCallback callback) {
        if (callback == null) {
            return;
        }
        if (commonCallbackCache.containsKey(callback.hashCode())) {
            mainHandler.removeMessages(callback.hashCode());
            commonCallbackCache.get(callback.hashCode()).stop = true;
        }
    }

    /**
     * @param onLifecycleEvent {@link AsyncExecutor#ON_START}, {@link AsyncExecutor#ON_RESUME},
     *                         {@link AsyncExecutor#ON_PAUSE}, {@link AsyncExecutor#ON_STOP}, {@link AsyncExecutor#ON_DESTROY}
     */
    public void setStopOnLifecycleEvent(int onLifecycleEvent) {
        if (onLifecycleEvent == ON_START
                || onLifecycleEvent == ON_RESUME
                || onLifecycleEvent == ON_PAUSE
                || onLifecycleEvent == ON_STOP
                || onLifecycleEvent == ON_DESTROY) {
            this.stopOnLifecycleEvent = onLifecycleEvent;
        }
    }

    private void checkStopOnLifecycleEvent(Activity activity, int onLifecycleEvent) {
        if (stopOnLifecycleEvent == onLifecycleEvent) {
            if (activityCallbackCache.indexOfKey(activity.hashCode()) >= 0) {
                mainHandler.removeMessages(activity.hashCode());
                List<AsyncCallback> list = activityCallbackCache.get(activity.hashCode());
                for (AsyncCallback callback : list) {
                    callback.stop = true;
                }
                activityCallbackCache.remove(activity.hashCode());
            }
        }
    }

    private class Lifecycle implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            checkStopOnLifecycleEvent(activity, ON_START);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            checkStopOnLifecycleEvent(activity, ON_RESUME);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            checkStopOnLifecycleEvent(activity, ON_PAUSE);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            checkStopOnLifecycleEvent(activity, ON_STOP);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            checkStopOnLifecycleEvent(activity, ON_DESTROY);
        }
    }

    public static abstract class AsyncCallback<T> {
        protected boolean stop = false;
        Object t;

        protected void runBefore() {
        }

        protected abstract T running();

        protected abstract void runAfter(T t);
    }
}