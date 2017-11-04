package com.hzh.easy.cache.interf;

import java.io.Serializable;

/**
 * Created by Hezihao on 2017/9/8.
 * 参数类Map
 */

public interface ICacheParams {
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

    /**
     * 将参数存进容器，子类定义容器，通常为Map
     *
     * @param key    键
     * @param target 要存入的数据
     * @param <P>
     * @return
     */
    <P extends ICacheParams> P put(String key, Serializable target);

    /**
     * 从容器中取出参数
     *
     * @param key 键
     * @param <T>
     * @return
     */
    <T extends Serializable> T get(String key);

    /**
     * 从容器中取出参数，可设置默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param <T>
     * @return
     */
    <T extends Serializable> T get(String key, Serializable defaultValue);
}
