package com.hzh.easy.cache.sample.cache;

import android.support.annotation.Nullable;

import com.hzh.easy.cache.base.BaseCache;
import com.hzh.easy.cache.sample.cache.params.UserInfoCacheParams;

import java.io.Serializable;

/**
 * @包名 com.hzh.easy.cache.sample.cache
 * @类名 UserInfoCache
 * @创建时间 on 2017/11/2  下午1:19
 * @作者 子和
 * @类的描述 TODO
 * Email hezihao@linghit.com
 * 最后更新者 UserInfoCache
 * 最后修改时间：2017/11/2  下午1:19
 */

public class UserInfoCache extends BaseCache<UserInfoCacheParams> {
    private static final String KEY = "cache_user_id_";

    private UserInfoCache() {
    }

    @Override
    public void removeCache(@Nullable UserInfoCacheParams params) {
        //params.
        getOperate().removeCache(KEY + params.getUserId());
    }

    @Override
    public void put(@Nullable UserInfoCacheParams params, Serializable target) {
        getOperate().putData(params.getUserId(), target);
    }

    @Override
    public <T extends Serializable> T get(@Nullable UserInfoCacheParams params) {
        return getOperate().getData(params.getUserId(), null);
    }
}