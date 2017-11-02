package com.hzh.easy.cache.sample.cache.params;

import android.support.annotation.NonNull;

import com.hzh.easy.cache.base.BaseCacheParams;

/**
 * @包名 com.hzh.easy.cache.sample.cache.params
 * @类名 UserInfoCacheParams
 * @创建时间 on 2017/11/2  下午2:04
 * @作者 子和
 * @类的描述 TODO
 * Email hezihao@linghit.com
 * 最后更新者 UserInfoCacheParams
 * 最后修改时间：2017/11/2  下午2:04
 */

public class UserInfoCacheParams extends BaseCacheParams {
    private static final String KEY_USER_ID = "userId";

    public UserInfoCacheParams putUserId(@NonNull String userId) {
        return put(KEY_USER_ID, userId);
    }

    public String getUserId() {
        return get(KEY_USER_ID);
    }
}
