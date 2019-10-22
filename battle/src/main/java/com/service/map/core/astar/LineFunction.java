package com.service.map.core.astar;

public class LineFunction {
    private final Point point1;
    private final Point point2;
    private final int type;

    private float a;
    private float b;

    private final int funid;

    public LineFunction(Point point1, Point point2, int type) {
        this.point1 = point1;
        this.point2 = point2;
        this.type = type;
        funid = getFunctionId();
    }

    private float function1(float y) {
        return point1.getX();
    }

    private float function2(float x) {
        return point1.getY();
    }

    private float function3(float x) {
        return a * x + b;
    }

    private float function4(float y) {
        return (y - b) / a;
    }

    private int getFunctionId() {
        // 先考虑两点在一条垂直于坐标轴直线的情况，此时直线方程为 y = a 或者 x = a 的形式
        if (point1.getX() == point2.getX()) {
            if (type == 0) {
                // 两点所确定直线垂直于y轴，不能根据x值得到y值
                return 0;
            } else if (type == 1) {
                return 1;
            }
        } else if (point1.getY() == point2.getY()) {
            if (type == 0) {
                return 2;
            } else if (type == 1) {
                // 两点所确定直线垂直于x轴，不能根据y值得到x值
                return 0;
            }
        }

        // 当两点确定直线不垂直于坐标轴时直线方程设为 y = ax + b
        // 根据
        // y1 = ax1 + b
        // y2 = ax2 + b
        // 上下两式相减消去b, 得到 a = ( y1 - y2 ) / ( x1 - x2 )
        a = (point1.getY() - point2.getY()) / (point1.getX() - point2.getX());
        // 将a的值代入任一方程式即可得到b
        b = point1.getY() - a * point1.getX();
        // 把a,b值代入即可得到结果函数
        if (type == 0) {
            return 3;
        } else if (type == 1) {
            return 4;
        }
        return 0;
    }

    public float function(float value) {
        switch (funid) {
            case 1:
                return function1(value);
            case 2:
                return function2(value);
            case 3:
                return function3(value);
            case 4:
                return function4(value);
            default:
                break;
        }
        return 0;
    }
}
