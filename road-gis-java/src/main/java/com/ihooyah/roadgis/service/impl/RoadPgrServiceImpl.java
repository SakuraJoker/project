package com.ihooyah.roadgis.service.impl;

import com.ihooyah.roadgis.dto.RoadScopeDto;
import com.ihooyah.roadgis.entity.IntersectionPoint;
import com.ihooyah.roadgis.entity.RoadLinePgr;
import com.ihooyah.roadgis.entity.RoadPointPgr;
import com.ihooyah.roadgis.mapper.RoadPointPgrMapper;
import com.ihooyah.roadgis.service.RoadPgrService;
import com.ihooyah.roadgis.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RoadPgrServiceImpl implements RoadPgrService {


    @Autowired
    private RoadPointPgrMapper roadPointPgrMapper;

    @Override
    public List<RoadPointPgr> selectRoadPointPgrs(RoadScopeDto roadScopeDto) {
        return roadPointPgrMapper.selectRoadPointPgrs(roadScopeDto.getLat(),roadScopeDto.getLng(),roadScopeDto.getDistance());
    }

    @Override
    public List<RoadLinePgr> selectRoadLinePgrs(Integer pointId, Integer distance) {
        return roadPointPgrMapper.selectRoadLinePgrs(pointId,distance);
    }

    @Override
    public Integer selectNearbyRoadPoint(String lat, String lng) {
        return roadPointPgrMapper.selectNearbyRoadPoint(lat,lng);
    }

    @Override
    public String selectSTIntersects(Map map) {
        return roadPointPgrMapper.selectSTIntersects(map);
    }

    @Override
    public Integer selectTotal(Map map) {
        return roadPointPgrMapper.selectTotal(map);
    }

    @Override
    public String selectSTasgeojson(Map map) {
        return roadPointPgrMapper.selectSTasgeojson(map);
    }

    @Override
    public String selectSTIntersection(Map map) {
        return roadPointPgrMapper.selectSTIntersection(map);
    }

    @Override
    public List<IntersectionPoint> selectIntersectionPointGroupByLngLat(IntersectionPoint intersectionPoint) {
        return roadPointPgrMapper.selectIntersectionPointGroupByLngLat(intersectionPoint);
    }

    @Override
    public int selectCountByLntLat(IntersectionPoint intersectionPoint) {
        return roadPointPgrMapper.selectCountByLntLat(intersectionPoint);
    }

    @Override
    public List<Map> selectGisLineList(Map map) {
        return roadPointPgrMapper.selectGisLineList(map);
    }

    @Override
    public String selectSTastext(Map map) {
        return roadPointPgrMapper.selectSTastext(map);
    }

    @Override
    public String STLineInterpolatePoints(Map map) {
        return roadPointPgrMapper.STLineInterpolatePoints(map);
    }

    @Override
    public double getShapeLenght(Map map) {
        return roadPointPgrMapper.getShapeLenght(map);
    }

    @Override
    public  List<Map> selectIntersectGeography(RoadScopeDto roadScopeDto) {
        return roadPointPgrMapper.selectIntersectGeography(roadScopeDto);
    }

    @Override
    public float STDistance(Map map) {
        return roadPointPgrMapper.STDistance(map);
    }
}
