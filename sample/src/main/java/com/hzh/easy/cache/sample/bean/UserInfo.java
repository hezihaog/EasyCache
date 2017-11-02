package com.hzh.easy.cache.sample.bean;

/**
 * @包名 com.hzh.easy.cache.sample.cache
 * @类名 UserInfo
 * @创建时间 on 2017/11/2  下午12:09
 * @作者 子和
 * @类的描述 用户信息bean
 * Email hezihao@linghit.com
 * 最后更新者 UserInfo
 * 最后修改时间：2017/11/2  下午12:09
 */

public class UserInfo extends Base {
    /**
     * 姓名
     */
    private String name;
    /**
     * 个性签名
     */
    private String sign;

    public UserInfo(String name, String sign) {
        this.name = name;
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
