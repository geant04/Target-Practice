package org.cis1200.coolgame;

import java.awt.*;

public class Plate extends TargetObj {

    public static final int INIT_RAD = 10;

    public static final int INIT_POS_X = 170;
    public static final int INIT_POS_Y = 170;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 30;

    public Plate(int px, int py, double vx, double vy, int rad) {
        super(px, py, rad, vx, vy);
        this.setType(0);
    }

    @Override
    public void draw(Graphics g) {
        g.drawRect(
                this.getPx() - getRadius() / 2, this.getPy() - getRadius() / 2,
                getRadius(), getRadius()
        );
    }
}
