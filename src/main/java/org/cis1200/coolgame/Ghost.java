package org.cis1200.coolgame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Ghost extends TargetObj {

    private BufferedImage[] ghostfiles = new BufferedImage[4];
    public static final int INIT_RAD = 60;

    public static final int INIT_POS_X = 170;
    public static final int INIT_POS_Y = 170;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 30;

    private double maxV = -1;

    private BufferedImage ghost; // apparently this is very computation heavy

    public Ghost(int px, int py, double vx, double vy, double mv, BufferedImage[] ghostfiles) {
        super(px, py, INIT_RAD, vx, vy);
        maxV = mv;
        this.ghostfiles = ghostfiles;

        this.setType(2);

        if (ghost == null) {
            ghost = ghostfiles[0];
        }
    }

    @Override
    public void draw(Graphics g) {
        int indx = Math.min(returnState(getVY()), 3);

        if (indx >= 0) {
            ghost = ghostfiles[indx];

            g.drawImage(
                    ghost, this.getPx() - getRadius() / 2, this.getPy() - getRadius() / 2,
                    this.getRadius(), this.getRadius(), null
            );
        }
    }

    @Override
    public void move() {
        setX((int) (getPx() + getVX()));
        setY((int) (getPy() + getVY()));
        setVY(getVY() + 0.5);
    }

    public int returnState(double vy) {
        double normalized = (vy + maxV) / (2 * maxV);
        return (int) Math.floor(normalized * 6) - 1; // -1 because if it's at 0, then there
                                                     // shouldn't be a texture
    }
}
