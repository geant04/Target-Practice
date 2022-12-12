package org.cis1200.coolgame;

import java.awt.*;

import static org.cis1200.coolgame.TargetCourt.img;

public class Normal extends TargetObj {

    public static final int INIT_RAD = 60;

    public static final int INIT_POS_X = 170;
    public static final int INIT_POS_Y = 170;
    public static final int INIT_VEL_X = 0;
    public static final int INIT_VEL_Y = 30;

    public Normal(int px, int py, double vx, double vy) {
        super(px, py, INIT_RAD, vx, vy);
        this.setType(1);
        /*
         * try {
         * if (img == null) {
         * img = ImageIO.read(new File(path));
         * }
         * } catch (IOException e) {
         * System.out.println("Internal Error:" + e.getMessage());
         * }
         */
    }

    @Override
    public void draw(Graphics g) {

        // g.setColor(this.color);
        // g.fillOval(this.getPx(), this.getPy(), getRadius(), getRadius());

        g.drawImage(
                img, this.getPx() - getRadius() / 2, this.getPy() - getRadius() / 2,
                this.getRadius(), this.getRadius(), null
        );

    }
}
