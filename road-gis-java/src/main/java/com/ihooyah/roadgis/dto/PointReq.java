package com.ihooyah.roadgis.dto;

import lombok.Data;

@Data
public class PointReq {
    //间隔m
    private int metre;
    //道路类型
    private String enticlassi;
    //数量
    private int num;
    //道路类型
    private String type;

    private String area;
}
