package com.service.map.core.astar;

/**
 * Created by g on 2016/6/7.
 * <p>
 * y = ax + b;
 */
public class StraightLine {
    private final Point point1, point2;

    private int startX, startY, endX, endY;

    private float a;
    private float b;

    private final int funId;

    private int forAxis;

    public StraightLine(Point p1, Point p2) {
        this.point1 = p1;
        this.point2 = p2;
        funId = getFunId();
    }

    public StraightLine(int sX, int sY, int eX, int eY) {
        this.point1 = new Point(sX + 0.5f, sY + 0.5f);
        this.point2 = new Point(eX + 0.5f, eY + 0.5f);

        this.startX = sX;
        this.startY = sY;
        this.endX = eX;
        this.endY = eY;

        _forAxis();
        funId = getFunId();
    }

    private void _forAxis() {
        float disX = Math.abs(startX - endX), disY = Math.abs(startY - endY);
        this.forAxis = disX > disY ? 0 : 1;
    }

    private int getFunId() {
        int type = this.forAxis;

        if (point1.getX() == point2.getX()) {
            if (type == 0) {
                return 0;
            } else if (type == 1) {
                return 1;
            }
        } else if (point1.getY() == point2.getY()) {
            if (type == 0) {
                return 2;
            } else if (type == 1) {
                return 0;
            }
        }

        a = (point1.getY() - point2.getY()) / (point1.getX() - point2.getX());
        b = point1.getY() - a * point1.getX();

        if (type == 0)
            return 3;
        else if (type == 1)
            return 4;
        return 0;
    }

    public Point getLoopPoint() {
        if (this.forAxis == 0) {
            int loopStart = Math.min(startX, endX);
            int loopEnd = Math.max(startX, endX);
            return new Point(loopStart, loopEnd);
        } else {
            int loopStart = Math.min(startY, endY);
            int loopEnd = Math.max(startY, endY);
            return new Point(loopStart, loopEnd);
        }
    }

    public Point getLinePoint(float v) {
        if (this.forAxis == 0) {
            return new Point(v, calcYPos(v));
        } else {
            return new Point(calcXPos(v), v);
        }
    }

    public float calcYPos(float x) {
        float r = 0;
        switch (funId) {
            case 2:
                r = function2(x);
                break;
            case 3:
                r = function3(x);
                break;
        }
        return r;
    }

    public float calcXPos(float y) {
        float r = 0;
        switch (funId) {
            case 1:
                r = function1(y);
                break;
            case 4:
                r = function4(y);
                break;
        }
        return r;
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


}
