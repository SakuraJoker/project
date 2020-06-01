package com.ihooyah.roadgis.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RoadScopeDto {

    /**
     * 经度
     */
    @NotBlank(message = "经度不能为空")
    private String lat;

    /**
     * 纬度
     */
    @NotBlank(message = "纬度不能为空")
    private String lng;

    /**
     * 距离
     */
    @NotNull(message = "距离不能为空")
    private Integer distance;

    private Object list;

}

