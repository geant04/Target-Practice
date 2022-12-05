package org.cis1200.coolgame;

import java.awt.*;


public abstract class TargetObj {
    private int px;
    private int py;
    private int radius;
    private double vx;
    private double vy;

    private double g = 0.5;

    public TargetObj(
            int px, int py, int radius, double vx, double vy
    ){
        this.px = px;
        this.py = py;
        this.radius = radius;
        this.vx = vx;
        this.vy = -1 * vy; // assume that vy positive means it's going up
    }
    // getters
    public int getPx(){
        return px;
    }
    public int getPy(){
        return py;
    }
    public int getRadius(){
        return radius;
    }
    public double getVX(){
        return vx;
    }
    public double getVY(){
        return vy;
    }

    // setters
    public void setX(int x){
        this.px = x;
    }
    public void setY(int y){
        this.py = y;
    }
    public void setVX(int vx){
        this.vx = vx;
    }
    public void setVY(int vy){
        this.vy = vy;
    }
    public void setRadius(int r){
        this.radius = r;
    }

    // stuff that updates stuff
    public void move(){
        this.px += this.vx;
        //time to calculate gravity lol
        this.py += this.vy; // positive is down?
        this.vy += g;
    }

    public boolean isHit(int x, int y){
        int x_sq = (x - px) * (x - px);
        int y_sq = (y - py) * (y - py);
        double dist = Math.sqrt(x_sq + y_sq);
        if(dist <= radius){
            System.out.println(
                    "Mouse x: " + x + ", compare to: " + px + "\n"
                            + "Mouse y: " + y + ", compare to : " + py + "\n"
                            + "Distance: " + dist
            );
        }
        return dist <= radius;
    }

    public abstract void draw(Graphics g);
}
