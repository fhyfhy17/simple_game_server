
package com.service.map.core.astar;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class Grid implements Cloneable {


    private Map<Integer, List<Node>> nodeMap;

    private int cols;
    private int rows;
    private float deviationX; // X 轴坐标偏移量（网格信息只是整张地图的一部分可活动范围）
    private float deviationZ; // Z 轴坐标偏移量（二维世界中的 Y 轴）
    private int cellSize;     // 格子大小

    /**
     * 通过格子索引坐标寻找格子对象
     *
     * @param x
     * @param y
     * @return
     */
    public Node getNode(int x, int y) {
        if (x < 0 || y < 0) {
            return null;
        }
        List<Node> yNodes = nodeMap.get(x);
        if (yNodes == null || y >= yNodes.size()) {
            return null;
        }
        return yNodes.get(y);
    }

    /**
     * 根据世界坐标得到所在的节点
     *
     * @param xPos 世界 X 坐标
     * @param zPos 世界 Z 坐标
     * @return
     */
    public Node getNodeByWoldPoint(float xPos, float zPos) {
        int x = worldToGridPosX(xPos);
        int y = worldToGridPosY(zPos);

        return this.getNode(x, y);
    }

    /**
     * 获取附近的四个节点
     *
     * @param xPos 世界坐标
     * @param yPos 世界坐标
     * @return
     */
    public List<Node> getNearByNodes(float xPos, float yPos) {
        int intX = worldToGridPosX(xPos);
        int intY = worldToGridPosY(yPos);

        return getNearByNodes(intX, intY);
    }

    /**
     * 获取附近的八个节点
     *八方向会穿过两个block格子连接的角。。
     * @param x 网格坐标
     * @param y 网格坐标
     * @return
     */
    public List<Node> getNearByNodes(int x, int y) {
        List<Node> result = new ArrayList<>();

        this.buildNodes(getNode(x, y - 1), result);
        //this.buildNodes(getNode(x + 1, y - 1), result);
        this.buildNodes(getNode(x + 1, y), result);
        //this.buildNodes(getNode(x + 1, y + 1), result);
        this.buildNodes(getNode(x, y + 1), result);
        //this.buildNodes(getNode(x - 1, y + 1), result);
        this.buildNodes(getNode(x - 1, y), result);
        //this.buildNodes(getNode(x - 1, y - 1), result);
        return result;
    }

    private void buildNodes(Node node, List<Node> returnList) {
        if (node == null) {
            return;
        }
        returnList.add(node);
    }

    /**
     * 得到一个点下的所有节点
     *
     * @param xPos       点的横向位置
     * @param yPos       点的纵向位置
     * @param exceptList 例外格，若其值不为空，则在得到一个点下的所有节点后会排除这些例外格
     * @return 共享此点的所有节点
     */
    private List<Node> getNodesByPoint(float xPos, float yPos, List<Node> exceptList) {
        List<Node> result = new ArrayList<>();

        boolean xIsInt = xPos % 1 == 0;
        boolean yIsInt = yPos % 1 == 0;

        if (xIsInt && yIsInt) {
            // 点由四节点共享情况
            int intX = (int) xPos;
            int intY = (int) yPos;
            result.add(getNode(intX - 1, intY - 1));
            result.add(getNode(intX, intY - 1));
            result.add(getNode(intX - 1, intY));
            result.add(getNode(intX, intY));
        } else if (xIsInt && !yIsInt) {
            // 点由2节点共享情况
            // 点落在两节点左右临边上
            int intX = (int) xPos;
            int intY = (int) yPos;
            result.add(getNode(intX - 1, intY));
            result.add(getNode(intX, intY));
        } else if (!xIsInt && yIsInt) {
            // 点落在两节点上下临边上
            int intX = (int) xPos;
            int intY = (int) yPos;
            result.add(getNode(intX, intY - 1));
            result.add(getNode(intX, intY));
        } else {
            // 点由一节点独享情况
            result.add(getNode((int) xPos, (int) yPos));
        }
        // 在返回结果前检查结果中是否包含例外点，若包含则排除掉
        if (exceptList != null && exceptList.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                if (exceptList.contains(result.get(i))) {
                    result.remove(i);
                    i--;
                }
            }
        }
        return result;
    }

    private List<Node> getNodesUnderPoint(float x, float y) {
        return getNodesByPoint(x, y, null);
    }

    /**
     * 世界坐标转换为格子坐标
     */
    private int worldToGridPosX(float worldposX) {
        worldposX -= deviationX;
        if (worldposX < 0)
            worldposX = 0;
        return (int) (worldposX / cellSize);
    }

    /**
     * 世界坐标Y（三维Z）转换为格子坐标
     */
    private int worldToGridPosY(float worldposY) {
        worldposY -= deviationZ;
        if (worldposY < 0)
            worldposY = 0;
        return (int) (worldposY / cellSize);
    }

    /**
     * 判断两点之间是否有障碍物
     *
     * @param starXPos 世界坐标
     * @param starYPos
     * @param endXPos
     * @param endYPos
     * @return true为有阻挡不可行走
     */
    public  List<Node> hadBarrier(float starXPos, float starYPos, float endXPos, float endYPos) {
        int startX = this.worldToGridPosX(starXPos);
        int startY = this.worldToGridPosY(starYPos);
        int endX = this.worldToGridPosX(endXPos);
        int endY = this.worldToGridPosY(endYPos);
    
        // 如果起点终点是同一个点那傻子都知道它们间是没有障碍物的
        //if (startX == endX && startY == endY) {
        //    return false;
        //}
    
        // 两节点中心位置
        Point point1 = new Point((float) (startX + 0.5), (float) (startY + 0.5));
        Point point2 = new Point((float) (endX + 0.5), (float) (endY + 0.5));
    
        // 起始点高度
        final Node startNode = getNode(startX, startY);
        final Node endNode = getNode(endX, endY);
    
        // float startHeight = getNode(startX, startY).getPy();
        // float endHeight = getNode(endX, endY).getPy();
    
        // 根据起点终点间横纵向距离的大小来判断遍历方向
        float distX = Math.abs(endX - startX);
        float distY = Math.abs(endY - startY);
        // 遍历方向，为true则为横向遍历，否则为纵向遍历
        boolean loopDirection = distX > distY;
        // 起始点与终点的连线方程
        LineFunction lineFunction;
        // 循环递增量
        float i;
        // 循环起始值
        float loopStart;
        // 循环终结值
        float loopEnd;
        // 起终点连线所经过的节点
        List<Node> passedNodeList=new ArrayList<>();
    
        final float selfHeightOffset = 2f; // 自身高度偏移量
        final float blockTheshold = 0f; // 阻挡距离阈值，越高 AI 越容易在坡度地形击中玩家
    
        // 为了运算方便，以下运算全部假设格子尺寸为1，格子坐标就等于它们的行、列号
        if (loopDirection) {
            lineFunction = new LineFunction(point1, point2, 0);
            loopStart = Math.min(startX, endX);
            loopEnd = Math.max(startX, endX);
        
            // 开始横向遍历起点与终点间的节点看是否存在障碍(不可移动点)
            for (i = loopStart; i <= loopEnd; i++) {
                // 由于线段方程是根据终起点中心点连线算出的，所以对于起始点来说需要根据其中心点w
                // 位置来算，而对于其他点则根据左上角来算
                if (i == loopStart) {
                    i += .5;
                }
                // 根据x得到直线上的y值
                float yPos = lineFunction.function(i);
            
                // 检查经过的节点是否有障碍物，若有则返回true
                passedNodeList.addAll(getNodesUnderPoint(i, yPos));
            
                // 两点式
                // y - y1 x - x1
                // ------ = ------
                // y2 - y1 x2 - x1
                //
                // |
                // v
                //
                // Ax + By + C = 0
            
                final float A = (float) ((endNode.getPy() - startNode.getPy()) * 1.0
                        / (endNode.getPx() - startNode.getPx()));
                final float B = -1;
                final float C = endNode.getPy() + selfHeightOffset - A * endNode.getPx();
            
                for (Node passedNode : passedNodeList) {
                    //if (passedNode.isWalkable() == false) {
                    //    return true;
                    //}
                    //// (x0, y0) 表示地图实际高度图坐标
                    //float x0 = passedNode.getPx();
                    //float y0 = passedNode.getPy();
                    //// y1 表示弹道直线上的坐标
                    //float y1 = (-A * x0 - C) / B;
                    //// 点到直线距离
                    //float H = (float) Math.abs((A * x0 + B * y0 + C) / Math.sqrt(A * A + B * B));
                    //
                    //// y0 > y1 表示有阻挡，阈值越大 AI 在坡度地形越容易击中玩家
                    //// 阻挡距离大于阈值，判定阻挡
                    //if (y0 - y1 > blockTheshold && H > blockTheshold) {
                    //    return true;
                    //}
                }
                // for (Node passedNode : passedNodeList) {
                // if (passedNode.isWalkable() == false) {
                // return true;
                // }
                // // 判断高度
                // if (!passedNode.isParallel(startHeight)) {
                // return true;
                // }
                // }
                if (i == loopStart + .5) {
                    i -= .5;
                }
            }
        } else {
            lineFunction = new LineFunction(point1, point2, 1);
            loopStart = Math.min(startY, endY);
            loopEnd = Math.max(startY, endY);
        
            // 开始纵向遍历起点与终点间的节点看是否存在障碍(不可移动点)
            for (i = loopStart; i <= loopEnd; i++) {
                if (i == loopStart) {
                    i += .5;
                }
                // 根据y得到直线上的x值
                float xPos = lineFunction.function(i);
                passedNodeList.addAll(getNodesUnderPoint(xPos, i));
            
                final float A = (float) ((endNode.getPy() - startNode.getPy()) * 1.0
                        / (endNode.getPx() - startNode.getPx()));
                final float B = -1;
                final float C = endNode.getPy() + selfHeightOffset - A * endNode.getPx();
            
                for (Node passedNode : passedNodeList) {
                    //if (passedNode.isWalkable() == false) {
                    //    return true;
                    //}
                    //float x0 = passedNode.getPx();
                    //float y0 = passedNode.getPy();
                    //float y1 = (-A * x0 - C) / B;
                    //// 点到直线距离
                    //float H = (float) Math.abs((A * x0 + B * y0 + C) / Math.sqrt(A * A + B * B));
                    //
                    //// y0 > y1 表示有阻挡，阈值越大 AI 在坡度地形越容易击中玩家
                    //// 阻挡距离大于阈值，判定阻挡
                    //if (y0 - y1 > blockTheshold && H > blockTheshold) {
                    //    return true;
                    //}
                }
                // for (Node passedNode : passedNodeList) {
                // if (passedNode.isWalkable() == false) {
                // return true;
                // }
                // // 判断高度
                // if (!passedNode.isParallel(startHeight)) {
                // return true;
                // }
                // }
                if (i == loopStart + .5) {
                    i -= .5;
                }
            }
        }
        return passedNodeList;
    }
    
    //下面两个算法，以输入的两个点为基准，每个格子为长宽1，进行格子的搜索
    //所以以世界坐标比用格子index坐标要细致很多，但是从结果上看，两者基本是一致的，
    //supercover覆盖面比较广，通过四个格子的连接点，会把四个格子都算进去，bresenham是通过格子画一条线，所以格子数较少。 所以supercover更严格，bresenham更宽松
    
    //基于  bresenham 算法
    public List<Node> bresenham(int x0, int y0, int x1, int y1) {
        List<Node> line = new ArrayList<>();
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        
        int err = dx-dy;
        int e2;
        int currentX = x0;
        int currentY = y0;
        
        while(true) {
            line.add(getNode(currentX,currentY));
            
            if(currentX == x1 && currentY == y1) {
                break;
            }
            e2 = 2*err;
            if(e2 > -1 * dy) {
                err = err - dy;
                currentX = currentX + sx;
            }
            if(e2 < dx) {
                err = err + dx;
                currentY = currentY + sy;
            }
        }
        
        return line;
    }
    
    //基于  supercover line 算法
    public List<Node> supercover(int x1, int y1, int x2, int y2) {
        final List<Node> nodes = new ArrayList<>();
        
        int ystep, xstep; // the step on y and x axis
        int error; // the error accumulated during the increment
        int errorprev; // *vision the previous value of the error variable
        int y = y1, x = x1; // the line points
        int ddy, ddx; // compulsory variables: the double values of dy and dx
        int dx = x2 - x1;
        int dy = y2 - y1;
        
        nodes.add(getNode(x1, y1)); // first point
        // NB the last point can't be here, because of its previous point (which has to be verified)
        if(dy<0){
            ystep=-1;
            dy=-dy;
        }else{
            ystep=1;
        }
        if(dx<0){
            xstep=-1;
            dx=-dx;
        }else{
            xstep=1;
        }
        
        ddy = 2 * dy; // work with double values for full precision
        ddx = 2 * dx;
        if (ddx >= ddy) { // first octant (0 <= slope <= 1)
            // compulsory initialization (even for errorprev, needed when dx==dy)
            errorprev = error = dx; // start in the middle of the square
            for (int i = 0; i < dx; i++) { // do not use the first point(already done)
                x += xstep;
                error += ddy;
                if (error > ddx) { // increment y if AFTER the middle ( > )
                    y += ystep;
                    error -= ddx;
                    // three cases (octant == right->right-top for directions below):
                    if(error + errorprev<ddx) {// bottom square also
                        nodes.add(getNode(x,y - ystep));
                    }else if(error + errorprev>ddx) {// left square also
                        nodes.add(getNode(x - xstep,y));
                    }else{ // corner: bottom and left squares also
                        nodes.add(getNode(x,y - ystep));
                        nodes.add(getNode(x - xstep,y));
                    }
                }
                nodes.add(getNode(x, y));
                errorprev = error;
            }
        } else { // the same as above
            errorprev = error = dy;
            for (int i = 0; i < dy; i++) {
                y += ystep;
                error += ddx;
                if (error > ddy) {
                    x += xstep;
                    error -= ddy;
                    if(error + errorprev<ddy){
                        nodes.add(getNode(x - xstep,y));
                    }
                    else if(error + errorprev>ddy){
                        nodes.add(getNode(x,y - ystep));
                    }
                    else{
                        nodes.add(getNode(x - xstep,y));
                        nodes.add(getNode(x,y - ystep));
                    }
                }
                nodes.add(getNode(x, y));
                errorprev = error;
            }
        }
        
        return nodes;
    }
    
    
    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setDeviationX(float deviationX) {
        this.deviationX = deviationX;
    }

    public void setDeviationZ(float deviationZ) {
        this.deviationZ = deviationZ;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    @Override
    public Object clone() {
        try {
            Grid grid = (Grid) super.clone();
            grid.setNodeMap(new HashMap<>(nodeMap));
            return grid;
        } catch (Exception e) {
            log.error("深度复制对象发送异常...", e);
        }
        return null;
    }

    public void setNodeMap(Map<Integer, List<Node>> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public int getNumCols() {
        return this.cols;
    }

    public int getNumRows() {
        return this.rows;
    }

    /**
     * 根据两坐标点，获取n1点到n2的方向
     *
     * @param start
     * @param end
     * @return
     */
    public NodeDirection getDirection(Node start, Node end) {
        if (start == null || end == null)
            return NodeDirection.None;

        int x = end.getX() - start.getX(), y = end.getY() - start.getY();
        double angle = Math.atan2(y, x) * (-180) / Math.PI;
        if (angle < 0) {
            angle = angle + 360;
        }

        return NodeDirection.getDirection(angle);
    }

    public void showTankOccupyNodes() {
        int count = 0;
        String str = "";
        for (Iterator<Map.Entry<Integer, List<Node>>> initIt = this.nodeMap.entrySet().iterator(); initIt.hasNext(); ) {
            Map.Entry<Integer, List<Node>> entry = initIt.next();
            for (Node node : entry.getValue()) {
                if (node.isTankOccupy()) {
                    count++;
                    str += "[" + node.getX() + "," + node.getY() + "] ";
                }
            }
        }
        log.debug("不可通行 count {}, {}", count, str);

    }

    public Node getNodeWithDirection(Node selfNode, float x, float y) {
        NodeDirection dir = NodeDirection.None;
        if (x == 0) {
            dir = y > 0 ? NodeDirection.Top : NodeDirection.Bottom;
        } else {
            double angle = Math.atan2(y, x) * (-180) / Math.PI;
            if (angle < 0) {
                angle = angle + 360;
            }
            dir = NodeDirection.getDirection(angle);
        }
        return dir.getNode(this, selfNode);
    }

    public void iter(Graphics g, Image floorImage, Image wallImage) {
        for (Map.Entry<Integer, List<Node>> entry : nodeMap.entrySet()) {
            List<Node> value = entry.getValue();
            for (Node node : value) {
                g.drawImage(node.isWalkable() ? floorImage : wallImage, node.getX() * 8, node.getY() * 8, null);
            }
        }
    }
}
