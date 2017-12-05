package com.hzh.easy.cache.sample.cache.params;

import com.hzh.easy.cache.base.BaseCacheParams;

/**
 * @package com.hzh.easy.cache.sample.cache.params
 * @fileName UserInfoCacheParams
 * @date on 2017/11/2  下午2:04
 * @auther 子和
 * @descirbe 用户信息缓存参数类
 * @email hezihao@linghit.com
 */
public class UserInfoCacheParams extends BaseCacheParams {
    private static final String KEY_USER_ID = "userId";

    public UserInfoCacheParams putUserId(String userId) {
        return put(KEY_USER_ID, userId);
    }

    public String getUserId() {
        return get(KEY_USER_ID);
    }
}