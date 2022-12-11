package org.cis1200.coolgame;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.cis1200.coolgame.TargetCourt.*;

public class DoubleTarget extends TargetObj {

    public static final int INIT_RAD = 60;

    public static final int INIT_POS_X = 170;
    public static final int INIT_POS_Y = 170;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 30;

    public static double maxV = -1;
    private int hits = 0;
    private double gravity = 0.5;

    private BufferedImage ghost; // apparently this is very computation heavy

    public DoubleTarget(int px, int py, int vx, int vy, int h) {
        super(px, py, INIT_RAD, vx, vy);
        maxV = vy;
        setType((h>0) ? -4 : 4);

        if (ghost == null) {
            ghost = ghostfiles[0];
        }
    }

    @Override
    public void draw(Graphics g) {
        int indx = min(1, max(0, hits));
        g.drawImage(
                doublefiles[indx], this.getPx() - getRadius() / 2, this.getPy() - getRadius() / 2,
                this.getRadius(), this.getRadius(), null
        );
    }

    @Override
    public boolean isHit(int x, int y) {
        int px = getPx();
        int py = getPy();
        int radius = getRadius();

        int x_sq = (x - px) * (x - px);
        int y_sq = (y - py) * (y - py);
        double dist = Math.sqrt(x_sq + y_sq);

        boolean hitDub = false;

        if (dist <= radius) {
            if (hits == 0) {
                System.out.println("pow");
                gravity = 0.12;
                setVY(-10); // launch it up a bit
                setVX(0);
                setType(-4);
                hits++;
            } else { // assuming hits == 1 now since there are two lives
                hitDub = true;
            }
        }
        return hitDub;
    }

    @Override
    public void move() {
        setX((int) (getPx() + getVX()));
        setY((int) (getPy() + getVY()));
        setVY(getVY() + gravity);
    }
}
