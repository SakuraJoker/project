package com.ihooyah.roadgis.msg;

/**
 * @author 王尧 【wangyao@ihooyah.com】
 * @description
 * @create 2018-03-30 下午8:05
 **/
public enum RespEnum {
    //基础请求
    SUCCESS(200, "请求成功"),
    ERROR(500, "请求失败"),
    INVALID_PARAMS(400, ""),

    //授权相关 11000
    AUTH_NO_ACCESS(11001, "没有权限访问该资源"),
    AUTH_ACCESSTOKEN_EXPIRED(11002, "ACCESS_TOKEN已失效"),
    AUTH_ACCESSTOKEN_INEXISTENCE(11003, "ACCESS_TOKEN不存在"),
    //用户相关 12000
    USER_NOLOGIN(12001, "用户未登录"),
    USER_INVALID_PWD(12002, "用户密码错误"),
    USER_INVALID_LOGINNAME(12003, "用户不存在"),
    USER_INFO_LACK(12101, "用户信息缺失"),

    USER_DEVICE_NO_ACCESS(30000, "该设备没有权限"),
    ILLEGAL_THIRD_TOKEN(30001, "非法的第三方登录token"),

    //登录超时
    USER_LOGIN_OUT_TIME(20000, "登录超时"),
    //缺少token
    USER_LOSE_LOGIN_TOKEN(20001, "登录token缺失"),
    //认证凭据丢失
    USER_LOSE_IDENTIFY_CREDENTIAL(20002, "认证凭据丢失");

    private int code;
    private String msg;

    RespEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通过code获取对应的内容
     *
     * @param
     * @return
     * @author 王尧 【wangyao@ihooyah.com】
     * @date 2018/3/30 下午8:15
     **/
    public static String getMsgByCode(int code) {
        for (RespEnum responseEnum : RespEnum.values()) {
            if (responseEnum.getCode() == code) {
                return responseEnum.msg;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
