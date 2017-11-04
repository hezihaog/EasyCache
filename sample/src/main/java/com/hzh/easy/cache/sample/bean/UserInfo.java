package com.hzh.easy.cache.sample.bean;

/**
 * @package com.hzh.easy.cache.sample.cache
 * @fileName UserInfo
 * @date on 2017/11/2  下午12:09
 * @auther 子和
 * @descirbe 用户信息bean
 * @email hezihao@linghit.com
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

    @Override
    public String toString() {
        return new StringBuilder()
                .append("name ::: ")
                .append(" -- ")
                .append(" sign ::: " + sign)
                .toString();
    }
}
