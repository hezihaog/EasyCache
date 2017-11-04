package com.hzh.easy.cache.config;


import android.content.Context;

/**
// * @package com.hzh.easy.cache.config
// * @fileName CacheConfig
// * @date on 2017/11/4  下午2:15
// * @auther zihe
// * @descirbe 缓存配置器
// * @email hezihao@linghit.com
 */

public class CacheConfig {
    private static final int M = 1024 * 1024;
    //默认磁盘缓存保存文件的文件名
    private static final String DEFAULT_CACHE_FILE_NAME = "ACache";
    //默认磁盘缓存存储大小，默认50 MB
    private static final int DEFAULT_DISK_CACHE_MAX_SIZE = 1000 * 1000 * 50;
    //默认磁盘缓存存储数量，默认不限制
    private static final int DEFAULT_DISK_CACHE_MAX_COUNT = Integer.MAX_VALUE;
    //默认内存缓存数量
    private static final int DEFAULT_MEMORY_CACHE_SIZE = 5 * M;

    private Context context;
    private int versionCode = 0;
    private String cacheFileName;
    private long diskMaxSize;
    private int diskMaxCount;
    private int memoryMaxSize;

    public CacheConfig(Builder builder) {
        this.context = builder.getContext();
        this.versionCode = builder.getVersionCode();
        this.cacheFileName = builder.getCacheFileName();
        this.diskMaxSize = builder.getDiskMaxSize();
        this.diskMaxCount = builder.getDiskMaxCount();
        this.memoryMaxSize = builder.getMemoryMaxSize();
    }

    public static Builder newBuilder(Context context, int versionCode) {
        return new Builder(context, versionCode);
    }

    public Context getContext() {
        return context;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getCacheFileName() {
        return cacheFileName;
    }

    public long getDiskMaxSize() {
        return diskMaxSize;
    }

    public int getDiskMaxCount() {
        return diskMaxCount;
    }

    public int getMemoryMaxSize() {
        return memoryMaxSize;
    }

    public static class Builder {
        private Context context;
        private int versionCode;
        private String cacheFileName = DEFAULT_CACHE_FILE_NAME;
        private long diskMaxSize = DEFAULT_DISK_CACHE_MAX_SIZE;
        private int diskMaxCount = DEFAULT_DISK_CACHE_MAX_COUNT;
        private int memoryMaxSize = DEFAULT_MEMORY_CACHE_SIZE;

        private Builder(Context context, int versionCode) {
            this.context = context;
            this.versionCode = versionCode;
        }

        public Builder withCacheFileName(String cacheFileName) {
            this.cacheFileName = cacheFileName;
            return this;
        }

        public Builder withDiskMaxSize(long maxSize) {
            this.diskMaxSize = maxSize;
            return this;
        }

        public Builder withDiskMaxCount(int maxCount) {
            this.diskMaxCount = maxCount;
            return this;
        }

        public Builder withMemoryMaxSize(int memoryMaxSize) {
            this.memoryMaxSize = memoryMaxSize;
            return this;
        }

        public Context getContext() {
            return context;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public String getCacheFileName() {
            return cacheFileName;
        }

        public long getDiskMaxSize() {
            return diskMaxSize;
        }

        public int getDiskMaxCount() {
            return diskMaxCount;
        }

        public int getMemoryMaxSize() {
            return memoryMaxSize;
        }

        public CacheConfig build() {
            return new CacheConfig(this);
        }
    }
}
