package com.ihooyah.roadgis.rest;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ihooyah.roadgis.dto.CrossType;
import com.ihooyah.roadgis.dto.PointReq;
import com.ihooyah.roadgis.dto.RoadScopeDto;
import com.ihooyah.roadgis.entity.IntersectionPoint;
import com.ihooyah.roadgis.enums.IntersectionType;
import com.ihooyah.roadgis.msg.RespEnum;
import com.ihooyah.roadgis.msg.RespInfo;
import com.ihooyah.roadgis.service.RoadPgrService;
import com.ihooyah.roadgis.vo.ResultVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
@Log4j2
@Controller
@RequestMapping("gis")
public class WebGisController{
    private String prefix = "gis";

    @Autowired
    private RoadPgrService roadPgrService;

    @GetMapping("/scopeList")
    @ResponseBody
    public RespInfo scopeList(@Valid RoadScopeDto roadScopeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return getValidateMsgs(bindingResult);
        return RespInfo.mobiSuccess(roadPgrService.selectRoadPointPgrs(roadScopeDto));
    }

    @GetMapping("/trackList")
    @ResponseBody
    public RespInfo trackList(@Valid RoadScopeDto roadScopeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return getValidateMsgs(bindingResult);
        /**
         * 根据经纬度查询最近节点id
         */
        Integer pointId = roadPgrService.selectNearbyRoadPoint(roadScopeDto.getLat(),roadScopeDto.getLng());
        if (pointId == null){
            return RespInfo.mobiError("附近没有找到道路节点");
        }
        return RespInfo.mobiSuccess(roadPgrService.selectRoadLinePgrs(pointId,roadScopeDto.getDistance()));
    }


    protected RespInfo getValidateMsgs(BindingResult bindingResult) {
        if (!bindingResult.getAllErrors().isEmpty()) {
            List<String> errorMsgs = bindingResult.getAllErrors().stream().map(t -> t.getDefaultMessage()).collect(Collectors.toList());
            return RespInfo.mobiError(RespEnum.INVALID_PARAMS, StringUtils.join(errorMsgs, " "));
        }
        return RespInfo.mobiError(RespEnum.ERROR, null);
    }

    @GetMapping("/selectSTIntersects")
    @ResponseBody
    public RespInfo selectSTIntersects(@RequestBody Map map) {
        TreeSet<String> list=new TreeSet<>();
       int sum=roadPgrService.selectTotal(map);
        for (int i=1;i<sum;i++) {
            for (int j=1;j<sum;j++) {
                map.put("line",i);
                map.put("line2",j);
                if (i==j){
                   continue;
                }
                String str=  roadPgrService.selectSTIntersects(map);
                if ("t".equals(str)){
                    String result=roadPgrService.selectSTasgeojson(map);
                    list.add(result);
                }
            }
        }
        List<ResultVo> resultVoList=list.stream().map(t->JSON.parseObject(t, ResultVo.class)).collect(Collectors.toList());
        System.out.print(list.size()+"=================================");
//        List<Object> objectList=new ArrayList<>();
//        resultVoList.forEach(item->{
//          if ("Point".equals(item.getType())){
//              objectList.add(item.getCoordinates());
//          }
//        });
//        return RespInfo.mobiSuccess(objectList);
        return RespInfo.mobiSuccess(resultVoList);
    }




    @GetMapping("/intersectionType")
    @ResponseBody
    public RespInfo IntersectionType() {
       List<IntersectionPoint> list=roadPgrService.selectIntersectionPointGroupByLngLat(new IntersectionPoint());
       list.forEach(item->{
           int num=roadPgrService.selectCountByLntLat(item);
           if (num==5){
               item.setIntersectionType(IntersectionType.MOREROADS.getType());
           }else if(num==4){
               item.setIntersectionType(IntersectionType.CROSS.getType());
           }else if(num==3){
               item.setIntersectionType(IntersectionType.T.getType());
           }else if (num==2){
               item.setIntersectionType(IntersectionType.L.getType());
           }else {
               item.setIntersectionType(IntersectionType.OTHER.getType());
           }
//           HYCoodinates hyCoodinates= HYCoordinateTransform.wgs84PToWgs84(item.getLat(),item.getLng());
//           item.setLng(hyCoodinates.getLongitude()+"");
//           item.setLat(hyCoodinates.getLatitude()+"");
       });

        return RespInfo.mobiSuccess(list);
    }

    @PostMapping("/selectGisLineList")
    @ResponseBody
    public RespInfo selectGisLineList(@RequestBody Map map) {
        if (!map.containsKey("metre")){
            return RespInfo.mobiError("参数解析错误");
        }
        int metre=Integer.valueOf(map.get("metre").toString());
        List<Map> list=roadPgrService.selectGisLineList(map);
        int sum=0;
        for (Map item : list) {
            if (item.containsKey("shape_leng")) {
                int num = (int) (Float.valueOf(item.get("shape_leng").toString())/metre);
                sum += num;
               if (Float.valueOf(item.get("shape_leng").toString())%metre==0){
                   sum--;//整除的话路口有监控不需要在建
               }
            }

        }
        return RespInfo.mobiSuccess(sum);
    }

    /**
     * 计算路段摄像头数目
     * @param pointReq
     * @return
     */
    @PostMapping("/getPointList")
    @ResponseBody
    public RespInfo getPointList(@Valid PointReq pointReq) {
        if (pointReq==null||0==pointReq.getMetre()||0==pointReq.getNum()){
            return RespInfo.mobiError("请求参数为空！");
        }
        Map map=new HashMap();
        if (StringUtils.isNotEmpty(pointReq.getEnticlassi())){
            map.put("enticlassi",pointReq.getEnticlassi());
        }
        if (StringUtils.isNotEmpty(pointReq.getArea())){
            map.put("area", pointReq.getArea());
        }
        List<Map> list=roadPgrService.selectGisLineList(map);
        List<Object> objectList=new ArrayList<>();
        list.forEach(item->{
           if (pointReq==null||!item.containsKey("shape_leng")){
               return;
           }else if (Float.valueOf(pointReq.getMetre())>Float.valueOf(item.get("shape_leng").toString())){
               return;
           }
           //MULTILINESTRING((118.78148007175 32.0364405110896,118.781514779286 32.0365378896807))
           String linestring=roadPgrService.selectSTastext(item);
           linestring=linestring.substring(linestring.indexOf("(")+2,linestring.length()-2);
           linestring=linestring.replace("(","").replace(")","");
           //拼凑所需参数  LINESTRING(118.78148007175 32.0364405110896,118.781514779286 32.0365378896807)
           linestring="'"+"LINESTRING("+linestring+")'";
           Map hashMap=new HashMap();
           double fraction=Double.valueOf(pointReq.getMetre())/Double.valueOf(item.get("shape_leng").toString());
           hashMap.put("fraction",fraction);
           hashMap.put("linestring",linestring);
           //log.info(fraction);
           String result=  roadPgrService.STLineInterpolatePoints(hashMap);
          // log.info(result);
           ResultVo resultVo=JSON.parseObject(result, ResultVo.class);
           List<Object> coordinates=resultVo.getCoordinates();
           if ("Point".equals(resultVo.getType())){//说明是对象
               objectList.add(coordinates);
           }else {//MultiPoint
               objectList.addAll(coordinates);
           }
       });
        map.put("sum",objectList.size()*pointReq.getNum());
        map.put("list",objectList);
        // System.out.println(objectList.size()+"==========================");
        return RespInfo.mobiSuccess(map);
    }

    /**
     * 计算路口的摄像头数目
     * @param crossType
     * @return
     */
    @PostMapping("/getIntersectionSize")
    @ResponseBody
    public RespInfo getIntersectionSize(@Valid CrossType crossType) {
        if (crossType==null||0==crossType.getCross()||0==crossType.getTrident()||0==crossType.getCrossroads()){
            return RespInfo.mobiError("请求参数为空！");
        }
        IntersectionPoint intersectionPoint=  new IntersectionPoint();
        intersectionPoint.setEnticlassi(crossType.getEnticlassi());
        List<IntersectionPoint> list=roadPgrService.selectIntersectionPointGroupByLngLat(intersectionPoint);
        int sum=0;
        for (IntersectionPoint item : list) {
            int num = roadPgrService.selectCountByLntLat(item);
            if (num == 5) {
                sum += crossType.getMoreroads();
            } else if (num == 4) {
                sum += crossType.getCrossroads();
            } else if (num == 3) {
                sum += crossType.getTrident();
            } else if (num == 2) {
                sum += crossType.getCross();
            }else {
                sum += crossType.getOther();
            }

        }
        return RespInfo.mobiSuccess(sum);
    }


    @PostMapping("/getInfo")
    @ResponseBody
    public RespInfo getInfo(@Valid PointReq pointReq) {
        Map<String,Object> map=new HashMap<>();
        IntersectionPoint intersectionPoint=new IntersectionPoint();
        if (pointReq!=null&&StringUtils.isNotEmpty(pointReq.getArea())){
            intersectionPoint.setArea(pointReq.getArea());
             map.put("area",pointReq.getArea());
        }
        int sum=roadPgrService.selectTotal(map);
        map.put("roadTotal",sum);//道路条数
        double zkm = roadPgrService.getShapeLenght(map);
        zkm/=1000;
        map.put("zkm",zkm);//总公里数

        double km=0;
        //国道  1140103000
        map.put("enticlassi","1140103000");
        double gd = roadPgrService.getShapeLenght(map);
        map.put("gd",gd/1000);
        km+=gd;
        //省道  1140104000
        map.put("enticlassi","1140104000");
        double sd = roadPgrService.getShapeLenght(map);
        map.put("sd",sd/1000);
        km+=sd;
        //城市主干道  1140303000
        map.put("enticlassi","1140303000");
        double zgd = roadPgrService.getShapeLenght(map);
        map.put("zgd",zgd/1000);
        km+=zgd;
        //城市次干道  1140304000
        map.put("enticlassi","1140304000");
        double cgd = roadPgrService.getShapeLenght(map);
        map.put("cgd",cgd/1000);
        km+=cgd;
        //高速公里  1140102000
        map.put("enticlassi","1140102000");
        double gsgl = roadPgrService.getShapeLenght(map);
        map.put("gsgl",gsgl/1000);
        km+=gsgl;
        //县道  1140105000
        map.put("enticlassi","1140105000");
        double xd = roadPgrService.getShapeLenght(map);
        map.put("xd",xd/1000);
        km+=xd;
        //乡道  1140106000
        map.put("enticlassi","1140106000");
        double xcd = roadPgrService.getShapeLenght(map);
        map.put("xcd",xcd/1000);
        km+=xcd;
        //快速路  1140301000
        map.put("enticlassi","1140301000");
        double ksl = roadPgrService.getShapeLenght(map);
        map.put("ksl",ksl/1000);
        km+=ksl;
        //高架路  1140302000
        map.put("enticlassi","1140302000");
        double gjl = roadPgrService.getShapeLenght(map);
        map.put("gjl",gjl/1000);
        km+=gjl;
        //支路  1140305000
        map.put("enticlassi","1140305000");
        double zl = roadPgrService.getShapeLenght(map);
        map.put("zl",zl/1000);
        km+=zl;
        //内部道路  1140306000
        map.put("enticlassi","1140306000");
        double nbdl = roadPgrService.getShapeLenght(map);
        map.put("nbdl",nbdl/1000);
        km+=nbdl;
        //机耕路  1140401000
        map.put("enticlassi","1140401000");
        double jgl = roadPgrService.getShapeLenght(map);
        map.put("jgl",jgl/1000);
        km+=jgl;
        //乡村路  1140402000
        map.put("enticlassi","1140402000");
        double xcl = roadPgrService.getShapeLenght(map);
        map.put("xcl",xcl/1000);
        km+=xcl;
        //小路  1140403000
        map.put("enticlassi","1140403000");
        double xl = roadPgrService.getShapeLenght(map);
        map.put("xl",xl/1000);
        km+=xl;
        map.put("km",km/1000);//道路总数

        List<IntersectionPoint> list=roadPgrService.selectIntersectionPointGroupByLngLat(intersectionPoint);
        int moreroads=0;
        //十字路口
        int crossroads=0;
        //三叉字路口
        int trident=0;
        //L型路口
        int cross=0;
        int other=0;
        //道路末端
        intersectionPoint.setPointNum(1);
        List<IntersectionPoint> oneList=roadPgrService.selectIntersectionPointGroupByLngLat(intersectionPoint);
       // intersectionPoint.setPointNum("2");
       // List<IntersectionPoint> crossList=roadPgrService.selectIntersectionPointGroupByLngLat(intersectionPoint);
        intersectionPoint.setPointNum(3);
        List<IntersectionPoint> tridentList=roadPgrService.selectIntersectionPointGroupByLngLat(intersectionPoint);
        intersectionPoint.setPointNum(4);
        List<IntersectionPoint> crossroadsList=roadPgrService.selectIntersectionPointGroupByLngLat(intersectionPoint);
        intersectionPoint.setPointNum(5);
        List<IntersectionPoint> moreroadsList=roadPgrService.selectIntersectionPointGroupByLngLat(intersectionPoint);
        intersectionPoint.setPointNum(0);
        intersectionPoint.setOtherNum("other");
        List<IntersectionPoint> otherList=roadPgrService.selectIntersectionPointGroupByLngLat(intersectionPoint);

        map.put("intersectionTotal",list.size()-oneList.size());//路口总数
        map.put("list",list);
        map.put("moreroads",moreroadsList.size());
        map.put("moreroadsList",moreroadsList);
        map.put("crossroads",crossroadsList.size());
        map.put("crossroadsList",crossroadsList);
        map.put("trident",tridentList.size());
        map.put("tridentList",tridentList);
        map.put("other",otherList.size());
        map.put("otherList",otherList);
        map.put("sum",list.size()-oneList.size());

        return RespInfo.mobiSuccess(map);

    }

    @RequestMapping("/test")
    public String test(Model model) {
        Map<String,Object> map=new HashMap<>();
        double km=0;
     //国道  1140103000
        map.put("enticlassi","1140103000");
        double gd = roadPgrService.getShapeLenght(map);
        map.put("gd",gd/1000);
        km+=gd;
     //省道  1140104000
        map.put("enticlassi","1140104000");
        double sd = roadPgrService.getShapeLenght(map);
        map.put("sd",sd/1000);
        km+=sd;
     //城市主干道  1140303000
        map.put("enticlassi","1140303000");
        double zgd = roadPgrService.getShapeLenght(map);
        map.put("zgd",zgd/1000);
        km+=zgd;
     //城市次干道  1140304000
        map.put("enticlassi","1140304000");
        double cgd = roadPgrService.getShapeLenght(map);
        map.put("cgd",cgd/1000);
        km+=cgd;
       //高速公里  1140102000
        map.put("enticlassi","1140102000");
        double gsgl = roadPgrService.getShapeLenght(map);
        map.put("gsgl",gsgl/1000);
        km+=gsgl;
        //县道  1140105000
        map.put("enticlassi","1140105000");
        double xd = roadPgrService.getShapeLenght(map);
        map.put("xd",xd/1000);
        km+=xd;
        //乡道  1140106000
        map.put("enticlassi","1140106000");
        double xcd = roadPgrService.getShapeLenght(map);
        map.put("xcd",xcd/1000);
        km+=xcd;
        //快速路  1140301000
        map.put("enticlassi","1140301000");
        double ksl = roadPgrService.getShapeLenght(map);
        map.put("ksl",ksl/1000);
        km+=ksl;
        //高架路  1140302000
        map.put("enticlassi","1140302000");
        double gjl = roadPgrService.getShapeLenght(map);
        map.put("gjl",gjl/1000);
        km+=gjl;
        //支路  1140305000
        map.put("enticlassi","1140305000");
        double zl = roadPgrService.getShapeLenght(map);
        map.put("zl",zl/1000);
        km+=zl;
        //内部道路  1140306000
        map.put("enticlassi","1140306000");
        double nbdl = roadPgrService.getShapeLenght(map);
        map.put("nbdl",nbdl/1000);
        km+=nbdl;
        //机耕路  1140401000
        map.put("enticlassi","1140401000");
        double jgl = roadPgrService.getShapeLenght(map);
        map.put("jgl",jgl/1000);
        km+=jgl;
        //乡村路  1140402000
        map.put("enticlassi","1140402000");
        double xcl = roadPgrService.getShapeLenght(map);
        map.put("xcl",xcl/1000);
        km+=xcl;
        //小路  1140403000
        map.put("enticlassi","1140403000");
        double xl = roadPgrService.getShapeLenght(map);
        map.put("xl",xl/1000);
        km+=xl;


        map.put("km",km/1000);
        List<IntersectionPoint> list=roadPgrService.selectIntersectionPointGroupByLngLat(new IntersectionPoint());
        int moreroads=0;
        //十字路口
       int crossroads=0;
        //三叉字路口
        int trident=0;
        //L型路口
        int cross=0;
        int other=0;
        for (IntersectionPoint item : list) {
            int num = roadPgrService.selectCountByLntLat(item);
            if (num == 5) {
                moreroads++;
            } else if (num == 4) {
                crossroads++;
            } else if (num == 3) {
                trident++;
            } else if (num == 2) {
                cross++;
            }else {
                other++;
            }
        }
        map.put("moreroads",moreroads);
        map.put("crossroads",crossroads);
        map.put("trident",trident);
     //   map.put("cross",cross);
        map.put("other",other+cross);
        map.put("sum",moreroads+crossroads+trident+other+cross);
        model.addAttribute("message",map);
        return "hello";
    }

    @PostMapping("/getLine")
    @ResponseBody
    public RespInfo getLine(@Valid List<PointReq> pointReqList ) {
        if (pointReqList.size()<=0){
            return RespInfo.mobiError("参数为空！");
        }
        Map resultMap=new HashMap();
        int sum=0;
        for (PointReq pointReq : pointReqList) {
            Map map = new HashMap();
           // 国道 1140103000
            map.put("enticlassi", pointReq.getEnticlassi());
            if (StringUtils.isNotEmpty(pointReq.getArea())){
                map.put("area", pointReq.getArea());
            }
            List<Map> list = roadPgrService.selectGisLineList(map);
            List<Object> objectList = new ArrayList<>();
            list.forEach(item -> {
                if (!item.containsKey("shape_leng")) {
                    return;
                } else if (Float.valueOf(pointReq.getMetre()) > Float.valueOf(item.get("shape_leng").toString())) {
                    return;
                }
                //MULTILINESTRING((118.78148007175 32.0364405110896,118.781514779286 32.0365378896807))
                String linestring = roadPgrService.selectSTastext(item);
                linestring = linestring.substring(linestring.indexOf("(") + 2, linestring.length() - 2);
                linestring = linestring.replace("(", "").replace(")", "");
                //拼凑所需参数  LINESTRING(118.78148007175 32.0364405110896,118.781514779286 32.0365378896807)
                linestring = "'" + "LINESTRING(" + linestring + ")'";
                Map hashMap = new HashMap();
                double fraction = Double.valueOf(pointReq.getMetre()) / Double.valueOf(item.get("shape_leng").toString());
                hashMap.put("fraction", fraction);
                hashMap.put("linestring", linestring);
                //log.info(fraction);
                String result = roadPgrService.STLineInterpolatePoints(hashMap);
                // log.info(result);
                ResultVo resultVo = JSON.parseObject(result, ResultVo.class);
                List<Object> coordinates = resultVo.getCoordinates();
                if ("Point".equals(resultVo.getType())) {//说明是对象
                    objectList.add(coordinates);
                } else {//MultiPoint
                    objectList.addAll(coordinates);
                }
            });

            map.put("total", objectList.size() * pointReq.getNum());
            map.put("list", objectList);
            resultMap.put(pointReq.getType(), map);
            sum += objectList.size() * pointReq.getNum();
        }
        resultMap.put("sum", sum);

        return RespInfo.mobiSuccess(resultMap);
    }


    @PostMapping("/getLngLat")
    @ResponseBody
    public RespInfo getLatlat(@Valid RoadScopeDto roadScopeDto , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return getValidateMsgs(bindingResult);
        List<Map> list= roadPgrService.selectIntersectGeography(roadScopeDto);
        List<Object> listList=new LinkedList<>();
        double metre=2;
        if (roadScopeDto.getDistance()<200){
            metre=2;
        }else if (roadScopeDto.getDistance()<=500){
            metre=2.5;
        }else if (roadScopeDto.getDistance()<=1000){
            metre=3;
        } else if (roadScopeDto.getDistance()<2000) {
            metre=4;
        }else if (roadScopeDto.getDistance()<=3000) {
            metre=5;
        }else {
            metre=10;
        }
        double finalMetre = metre;
        list.forEach(item->{
//            ResultVo resultVo = JSON.parseObject(item.get("geom").toString(), ResultVo.class);
//            if ("MultiLineString".equals(resultVo.getType())) {
//                List<Object> lngLatList=new LinkedList<>();
//                resultVo.getCoordinates().forEach(object->{
//                    JSONArray lnglat=JSON.parseArray(object.toString());
//                    lngLatList.addAll(lnglat);
//                });
//                lngLatList.forEach(object->{
//                    JSONArray lnglat = JSON.parseArray(object.toString());
//                    Map map=new HashMap();
//                    map.put("lng",roadScopeDto.getLng());
//                    map.put("lat",roadScopeDto.getLat());
//                    map.put("lng2",lnglat.get(0));
//                    map.put("lat2",lnglat.get(1));
//                    float meter= roadPgrService.STDistance(map);
//                    if (roadScopeDto.getDistance()-5<=meter&&meter<=roadScopeDto.getDistance()+5){
//                        System.err.println(meter+"=======================");
//                        listList.add(lnglat);
//                       // return;
//                    }
//                });
//            }

            List<Object> objectList=new ArrayList<>();
            String linestring = roadPgrService.selectSTastext(item);
            linestring = linestring.substring(linestring.indexOf("(") + 2, linestring.length() - 2);
            linestring = linestring.replace("(", "").replace(")", "");
            linestring = "'" + "LINESTRING(" + linestring + ")'";
            Map hashMap = new HashMap();
            double fraction = finalMetre / Double.valueOf(item.get("shape_leng").toString());
            hashMap.put("fraction", fraction);
            hashMap.put("linestring", linestring);
            //log.info(fraction);
            String result = roadPgrService.STLineInterpolatePoints(hashMap);
            // log.info(result);
            ResultVo resultVo = JSON.parseObject(result, ResultVo.class);
            List<Object> coordinates = resultVo.getCoordinates();
            if ("Point".equals(resultVo.getType())) {//说明是对象
                objectList.add(coordinates);
            } else {//MultiPoint
                objectList.addAll(coordinates);
            }
            objectList.forEach(object->{
                Map map=new HashMap();
                JSONArray lnglat = JSON.parseArray(object.toString());
                map.put("lng",roadScopeDto.getLng());
                map.put("lat",roadScopeDto.getLat());
                 map.put("lng2",lnglat.get(0));
                 map.put("lat2",lnglat.get(1));
                float meter= roadPgrService.STDistance(map);
                if (roadScopeDto.getDistance()-1<meter&&meter<roadScopeDto.getDistance()+1){
                   // System.err.println(meter+"=======================");
                    listList.add(lnglat);
                       // return;
                    }
            });
        });
        System.err.println(listList.size()+"=======================");
        return RespInfo.mobiSuccess(listList);
    }


    @PostMapping("/getIntersectionList")
    @ResponseBody
    public RespInfo getIntersectionList(@Valid RoadScopeDto roadScopeDto, BindingResult bindingResult) {
        log.info("开始请求:-------------"+roadScopeDto.toString());
        if (bindingResult.hasErrors()) return getValidateMsgs(bindingResult);
        if (roadScopeDto==null||roadScopeDto.getList()==null){
            return RespInfo.mobiError("参数为空！");
        }
        List<Map> list= roadPgrService.selectIntersectGeography(roadScopeDto);
        TreeSet<String> treeSet=new TreeSet<>();
        String linestring=roadScopeDto.getList().toString().substring(1,roadScopeDto.getList().toString().length()-1);
        linestring=linestring.replace(","," ").replace("[","").replace("]",",");
        linestring=linestring.substring(0,linestring.length()-1);//去掉后一个，
        linestring = "'LINESTRING("+linestring+")'";
        //System.out.println(linestring);
        Map map=new HashMap();
        map.put("geometry",linestring);
        list.forEach(item->{
            map.put("gid",item.get("gid"));
            String result=roadPgrService.selectSTIntersection(map);
            treeSet.add(result);
        });
        List<Object> objectList=new ArrayList<>();
        treeSet.forEach(item->{
            ResultVo resultVo=  JSON.parseObject(item, ResultVo.class);
            if ("Point".equals(resultVo.getType())){
                objectList.add(resultVo.getCoordinates());
            }else{
                //objectList.addAll(item.getCoordinates());
            }
        });
        //System.out.println(objectList.size()+"-------------------"+objectList);
        log.info("返回结果:"+objectList.size()+"--------------"+objectList);
        return RespInfo.mobiSuccess(objectList);
    }
}


