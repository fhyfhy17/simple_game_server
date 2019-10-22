package com.service.map.core.astar;


import lombok.Data;

/**
 * 格子信息
 */
@Data
public class GridTemplate {

    private int index;        //格子索引值
    private int x;            //格子位置坐标系的X坐标
    private int z;            //格子位置坐标系的Y坐标
    private float px;            //格子中心的世界X坐标
    private float py;            //格子中心的世界Y坐标
    private float pz;            //格子中心的世界Z坐标
    private int walk;            //是否通行 0: 不可通行  1: 可通行

}
