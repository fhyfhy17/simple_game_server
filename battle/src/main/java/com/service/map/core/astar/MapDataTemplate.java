package com.service.map.core.astar;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图网格数据
 */
@Data
public class MapDataTemplate {
    private String mapId;            //地图ID
    private float deviationX;        //X轴坐标偏移量(网格信息只是整张地图的一部分可活动范围)
    private float deviationZ;        //Z轴坐标偏移量(二维世界中的Y轴)
    private int cols;
    private int rows;
    private int cellSize;            //格子大小
    private List<GridTemplate> nodeList = new ArrayList<>();    //格子信息
}
