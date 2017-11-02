package com.hzh.easy.cache.sample.cache;

import android.support.annotation.Nullable;

import com.hzh.easy.cache.base.BaseCache;
import com.hzh.easy.cache.sample.cache.params.UserInfoCacheParams;

import java.io.Serializable;

/**
 * @package com.hzh.easy.cache.sample.cache
 * @fileName UserInfoCache
 * @date on 2017/11/2  下午1:19
 * @auther 子和
 * @descirbe 用户信息缓存
 * @email hezihao@linghit.com
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