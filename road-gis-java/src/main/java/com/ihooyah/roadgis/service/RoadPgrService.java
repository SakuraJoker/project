package com.ihooyah.roadgis.service;


import com.ihooyah.roadgis.dto.RoadScopeDto;
import com.ihooyah.roadgis.entity.IntersectionPoint;
import com.ihooyah.roadgis.entity.RoadLinePgr;
import com.ihooyah.roadgis.entity.RoadPointPgr;
import com.ihooyah.roadgis.vo.ResultVo;

import java.util.List;
import java.util.Map;

public interface RoadPgrService {

    /**
     * 查询点位范围走向
     * @return
     */
    public List<RoadPointPgr> selectRoadPointPgrs(RoadScopeDto roadScopeDto);


    /**
     * 查询点位轨迹走向
     * @return
     */
    public List<RoadLinePgr> selectRoadLinePgrs(Integer pointId, Integer distance);


    /**
     * 查询最近节点id
     * @param lat
     * @param lng
     * @return
     */
    public Integer selectNearbyRoadPoint(String lat, String lng);

    /**
     * 判断是否相交
     * @param map
     * @return
     */
    String selectSTIntersects(Map map);

    /**
     * 统计总数
     * @return
     */
    Integer selectTotal(Map map);

    /**
     * 求两geometry相交部分
     * @param map
     * @return
     */
    String selectSTasgeojson(Map map);

    /**
     * 求两geometry相交部分
     * @param map
     * @return
     */
    String selectSTIntersection(Map map);

    /**
     * 根据经纬度分组查询数据
     * @param intersectionPoint
     * @return
     */
    List<IntersectionPoint> selectIntersectionPointGroupByLngLat(IntersectionPoint intersectionPoint);

    /**
     * 根据经纬度统计个数
     * @param intersectionPoint
     * @return
     */
    int selectCountByLntLat(IntersectionPoint intersectionPoint);

    /**
     * 查询路网数据列表
     * @param map
     * @return
     */
    List<Map> selectGisLineList(Map map);

    /**
     * 将geom转成经纬度坐标
     * @param map
     * @return
     */
    String selectSTastext(Map map);

    /**
     * 将geometry 分成n个路段
     * @param map
     * @return
     */
    String STLineInterpolatePoints(Map map);

    /**
     * 统计路网长度
     * @param map
     * @return
     */
    double getShapeLenght(Map map);

    /**
     * 查询r半径内所有路网
     * @param roadScopeDto
     * @return
     */
    List<Map> selectIntersectGeography(RoadScopeDto roadScopeDto);

    /**
     * 查询两geometry最短距离
     * @param map
     * @return
     */
    float STDistance(Map map);

}
