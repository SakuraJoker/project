package com.ihooyah.roadgis.mapper;

import com.ihooyah.roadgis.dto.RoadScopeDto;
import com.ihooyah.roadgis.entity.IntersectionPoint;
import com.ihooyah.roadgis.entity.RoadLinePgr;
import com.ihooyah.roadgis.entity.RoadPointPgr;
import com.ihooyah.roadgis.vo.ResultVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoadPointPgrMapper {

    public List<RoadPointPgr> selectRoadPointPgrs(@Param("lat") String lat, @Param("lng") String lng, @Param("distance") Integer distance);

    public List<RoadLinePgr> selectRoadLinePgrs(@Param("pointId") Integer pointId, @Param("distance") Integer distance);

    /**
     * 查询离点位最近得道路节点
     * @param lat
     * @param lng
     * @return
     */
    Integer selectNearbyRoadPoint(@Param("lat") String lat, @Param("lng") String lng);

    String selectSTIntersects(Map map);

    Integer selectTotal(Map map);

    String selectSTasgeojson(Map map);

    String selectSTIntersection(Map map);

    List<IntersectionPoint> selectIntersectionPointGroupByLngLat(IntersectionPoint intersectionPoint);

    int selectCountByLntLat(IntersectionPoint intersectionPoint);


    List<Map> selectGisLineList(Map map);

    String selectSTastext(Map map);

    String STLineInterpolatePoints(Map map);

    double getShapeLenght(Map map);

    List<Map> selectIntersectGeography(RoadScopeDto roadScopeDto);

    float STDistance(Map map);



}
