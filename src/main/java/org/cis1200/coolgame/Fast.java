package org.cis1200.coolgame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Fast extends TargetObj {

    public static final int INIT_RAD = 60;
    private BufferedImage img;

    public Fast(int px, int py, double vx, double vy, BufferedImage img) {
        super(px, py, INIT_RAD, vx, vy);
        this.setType(3);
        this.img = img;
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
