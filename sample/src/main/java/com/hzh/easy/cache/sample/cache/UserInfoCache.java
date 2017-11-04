package com.hzh.easy.cache.sample.cache;

import android.support.annotation.Nullable;

import com.hzh.easy.cache.base.BaseCache;
import com.hzh.easy.cache.sample.cache.params.UserInfoCacheParams;
import com.hzh.easy.cache.util.ACache;

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
    //保存的唯一判断标准
    private static final String KEY = "cache_user_id_";
    //缓存时间，保存7天
    private static final int SAVE_TIME = 7 * ACache.TIME_DAY;

    private UserInfoCache() {
    }

    @Override
    public void removeCache(@Nullable UserInfoCacheParams params) {
        getOperate().removeCache(KEY + params.getUserId());
    }

    @Override
    public void put(@Nullable UserInfoCacheParams params, Serializable target) {
        getOperate().putData(params.getUserId(), target, SAVE_TIME);
    }

    @Override
    public <T extends Serializable> T get(@Nullable UserInfoCacheParams params) {
        return getOperate().getData(params.getUserId(), null);
    }
}