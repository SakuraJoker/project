package com.ihooyah.roadgis.enums;

/**
 * 路口类型
 */
public enum IntersectionType {
    CROSS("十字"),T("T型"),L("L型"),OTHER("其他"),MOREROADS("五叉");
    private String type;
    IntersectionType(String type){
        this.type=type;
    }

    public String getType(){
        return type;
    }
}
