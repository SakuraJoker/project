package com.ihooyah.roadgis.dto;

import lombok.Data;

@Data
public class CrossType {
    //五字路口
    private int moreroads;
    //十字路口
    private int crossroads;
    //三叉字路口
    private int trident;
    //L型路口
    private int cross;

    private int other;
    //道路类型
    private String enticlassi;
}
