package com.service.map.core.astar;

public enum NodeDirection {

    None(0),
    Right(1),
    RightBottom(2),
    Bottom(3),
    LeftBottom(4),
    Left(5),
    LeftTop(6),
    Top(7),
    RightTop(8);

    private int direct;

    NodeDirection(int direct) {
        this.direct = direct;
    }

    public int getDirect() {
        return this.direct;
    }

    public static NodeDirection getDirection(double a) {
        if (a == 0)
            return Right;

        if (a > 0 && a < 90)
            return RightBottom;

        if (a == 90)
            return Bottom;

        if (a > 90 && a < 180)
            return LeftBottom;

        if (a == 180)
            return Left;

        if (a > 180 && a < 270)
            return LeftTop;

        if (a == 270)
            return Top;

        if (a > 270 && a < 360)
            return RightTop;

        return None;
    }

    public Node getNode(Grid grid, Node center) {
        switch (this) {
            case Right:
                return grid.getNode(center.getX() + 2, center.getY());
            case RightBottom:
                return grid.getNode(center.getX() + 2, center.getY() - 2);
            case RightTop:
                return grid.getNode(center.getX() + 2, center.getY() + 2);
            case Left:
                return grid.getNode(center.getX() - 2, center.getY());
            case LeftBottom:
                return grid.getNode(center.getX() - 2, center.getY() - 2);
            case LeftTop:
                return grid.getNode(center.getX() - 2, center.getY() + 2);
            case Top:
                return grid.getNode(center.getX(), center.getY() + 2);
            case Bottom:
                return grid.getNode(center.getX(), center.getY() - 2);
            default:
                return center;
        }
    }
}
