package com.ihooyah.roadgis.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 路网基础数据
 */
@Getter
@Setter
public class Road {

    private Integer gid;

    private String geom;

    private String name;

    private Integer source;

    private Integer traget;

    private Double length;


}
