package Model;

import java.awt.*;

public class Node {
    Node parent;
    int col;
    int row;
    int gCost;
    int hCost;
    int fCost;
    boolean start;
    boolean goal;
    boolean solid;
    boolean open;
    boolean checked;
    boolean path;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }
    public void setAsStart() {
        start = true;
    }
    public void setAsGoal() {
        goal = true;
    }
    public void setAsSolid() {
        solid = true;
    }
    public void setAsOpen() {
        open = true;
    }
    public void setAsChecked() {
        checked = true;
    }
    public void setAsPath() {
        path = true;
    }

}
