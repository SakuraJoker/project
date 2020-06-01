package com.ihooyah.roadgis.utils.coodinate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @Description 虎牙科技坐标转换服务工具类
 * @Author: 王尧 / wangyao@ihooyah.com
 * @CreateDate: 2020-03-07 21:57:41
 * @Version: [V-1.0]
 **/
public class HYCoordinateTransform {

    private static final String coordinateTransFromUrl = "http://open.ihooyah.com/api/gis/coordinate-transfrom";
    private static final String appKey = "sjJSGEpIM8vTxmKG";
    private static final String appSecret = "a6c22c749ea74a7a1abbe19c7862f70a2a35dca0";

    /**
     * 初始化网络请求  RestTemplate
     *
     * @author: 王尧 / wangyao@ihooyah.com
     * @createDate: 22:05
     */
    static RestTemplate initRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000);
        return new RestTemplate(requestFactory);
    }

    /**
     * WGS84 转 WGS84P
     *
     * @author: 王尧 / wangyao@ihooyah.com
     * @createDate: 22:01
     */
    public static HYCoodinates wgs84ToWgs84P(String lat, String lng) {
        try {
            return requestSend(lat, lng, "WGS84toWGS84P");
        } catch (Exception e) {
            e.printStackTrace();
            return new HYCoodinates(0.0D, 0.0D);
        }
    }

    /**
     * WGS84P 转 WGS84
     *
     * @author: 王尧 / wangyao@ihooyah.com
     * @createDate: 22:02
     */
    public static HYCoodinates wgs84PToWgs84(String lat, String lng) {
        try {
            return requestSend(lat, lng, "WGS84PtoWGS84");
        } catch (Exception e) {
            e.printStackTrace();
            return new HYCoodinates(0.0D, 0.0D);
        }
    }


    /**
     * 发送请求
     *
     * @author: 王尧 / wangyao@ihooyah.com
     * @createDate: 23:21
     */
    static HYCoodinates requestSend(String lat, String lng, String transfromType) throws Exception {
        //入参
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("locations", lng + "," + lat);
        params.add("transfromType", transfromType);
        //header
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("appKey", appKey);
        header.add("appSecret", appSecret);
        //初始化网络请求句柄
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, header);
        RestTemplate restTemplate = initRestTemplate();
        //发送网络请求
        JSONObject jsonObject = restTemplate.postForObject(coordinateTransFromUrl, httpEntity, JSONObject.class);
        if (jsonObject.getInteger("code") == 200) {
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            if (jsonArray.size() > 0) {
                JSONObject coordinateObject = jsonArray.getJSONObject(0);
                //{ "latitude":31.949390704834798, "longitude":118.69000082057063 }
                double latV = coordinateObject.getDouble("latitude");
                double lntV = coordinateObject.getDouble("longitude");
                return new HYCoodinates(latV, lntV);
            }
        }
        return new HYCoodinates(0.0D, 0.0D);
    }

    public static void main(String[] args) {
        //118.678454384295,31.9451261290794
        HYCoodinates hyCoodinates = wgs84PToWgs84("31.9451261290794", "118.678454384295");
        System.out.println(hyCoodinates.getLatitude()+","+hyCoodinates.getLongitude());
    }

}
