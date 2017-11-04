package com.hzh.easy.cache.interf;


import com.hzh.easy.cache.base.CacheOperate;

/**
 * Created by Hezihao on 2017/8/4.
 */

public interface ICache {
    /**
     * 获取操作对象
     *
     * @return
     */
    CacheOperate getOperate();

    /**
     * 获取VersionCode，通常为App的VersionCode
     *
     * @return
     */
    int getVersionCode();

    /**
     * 获取Version标志，通常为特定字符拼接VersionCode
     *
     * @return
     */
    String getVersionSymbol();
}