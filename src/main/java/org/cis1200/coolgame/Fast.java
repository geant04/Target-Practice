package org.cis1200.coolgame;

import java.awt.*;

import static org.cis1200.coolgame.TargetCourt.fastimg;
import static org.cis1200.coolgame.TargetCourt.img;

public class Fast extends TargetObj {

    public static final int INIT_RAD = 60;

    public Fast(int px, int py, double vx, double vy) {
        super(px, py, INIT_RAD, vx, vy);
        this.setType(3);
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
                fastimg, this.getPx() - getRadius() / 2, this.getPy() - getRadius() / 2,
                this.getRadius(), this.getRadius(), null
        );

    }
}
