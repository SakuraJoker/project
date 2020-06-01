package com.ihooyah.roadgis.msg;

/**
 * Http请求统一返回结构
 *
 * @param
 * @author 王尧 【wangyao@ihooyah.com】
 * @return
 * @date 2018/3/30 下午7:59
 **/
public class RespInfo {
    private Integer code = -1;
    private String msg = "";
    private Object result = "";
    private Object endpoint = null;

    public RespInfo(Integer code, String msg, Object result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public RespInfo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public RespInfo() {
    }

    public static RespInfo out(RespEnum respEnum) {
        return new RespInfo(respEnum.getCode(), respEnum.getMsg());
    }

    public static RespInfo out(RespEnum respEnum, Object object) {
        RespInfo respInfo = new RespInfo(respEnum.getCode(), respEnum.getMsg(), object);
        respInfo.setEndpoint("web");
        return respInfo;
    }

    public static RespInfo mobiOut(RespEnum respEnum, Object object) {
        RespInfo respInfo = new RespInfo(respEnum.getCode(), respEnum.getMsg(), object);
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiSuccess(RespEnum respEnum, Object object) {
        RespInfo respInfo = new RespInfo(respEnum.getCode(), respEnum.getMsg(), object);
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiSuccess() {
        RespInfo respInfo = new RespInfo(RespEnum.SUCCESS.getCode(), "", "");
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiSuccess(Object obj) {
        RespInfo respInfo = new RespInfo(RespEnum.SUCCESS.getCode(), "", obj);
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiSuccess(String msg, Object obj) {
        RespInfo respInfo = new RespInfo(RespEnum.SUCCESS.getCode(), msg, obj);
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiSuccess(String msg) {
        RespInfo respInfo = new RespInfo(RespEnum.SUCCESS.getCode(), msg);
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiError() {
        RespInfo respInfo = new RespInfo(RespEnum.ERROR.getCode(), RespEnum.ERROR.getMsg());
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiError(String msg) {
        RespInfo respInfo = new RespInfo(RespEnum.ERROR.getCode(), msg);
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiError(String msg, Object result) {
        RespInfo respInfo = new RespInfo(RespEnum.ERROR.getCode(), msg);
        respInfo.setEndpoint("mobi");
        respInfo.setResult(result);
        return respInfo;
    }

    public static RespInfo mobiError(RespEnum respEnum, String errorMsg) {
        RespInfo respInfo = null;
        respInfo = new RespInfo(respEnum.getCode(), errorMsg, "");
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiError(RespEnum respEnum, Object result) {
        RespInfo respInfo = null;
        respInfo = new RespInfo(respEnum.getCode(), respEnum.getMsg(), result);
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public static RespInfo mobiError(RespEnum respEnum) {
        RespInfo respInfo = new RespInfo(respEnum.getCode(), respEnum.getMsg(), "");
        respInfo.setEndpoint("mobi");
        return respInfo;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Object endpoint) {
        this.endpoint = endpoint;
    }
}
