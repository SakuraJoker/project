package com.ihooyah.roadgis.vo;

import lombok.Data;

import java.util.List;

@Data
public class ResultVo {
    private String type;
    private Object geometries;
    private List<Object> coordinates;
}
