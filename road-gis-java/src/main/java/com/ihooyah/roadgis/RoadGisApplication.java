package com.ihooyah.roadgis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ihooyah.roadgis.mapper")
public class RoadGisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoadGisApplication.class, args);
    }

}
