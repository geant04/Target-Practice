package org.cis1200.coolgame;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;

public class TargetCourt extends JPanel{

    //------------- store all your stuff above this line

    private boolean run = false;
    private final JLabel status;
    private JLabel announce_label;
    private JLabel live_label;
    private JLabel score_label;

    public static final int COURT_WIDTH = 600;
    public static final int COURT_HEIGHT = 600;
    public static int lives = 5;
    public static int score = 0;
    public static int streak = 0;


    // Update interval for timer, in milliseconds
    public static final int TICK_RATE = 15;
    public static final int INIT_SPAWN_RATE = 100;
    public static int time = 0;
    public static int netTime = 0;
    public static int SPAWN_RATE = 100;
    public static double FUNNY_RATE = 0;
    // this is the rate in which we spawn funny targets instead of normal ones
    private ArrayList<TargetObj> targets = new ArrayList<TargetObj>();
    private ArrayList<TargetObj> details = new ArrayList<TargetObj>();
    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<Point> targetpoints = new ArrayList<>();

    public static final String IMG_FILE = "files/target.png";
    public static final String FAST_FILE = "files/fasttarget.png";
    public static BufferedImage img;
    public static BufferedImage fastimg;
    public static String[] textures = {
            "files/ghosttarget4.png",
            "files/ghosttarget3.png",
            "files/ghosttarget2.png",
            "files/ghosttarget.png"
    };
    public static String[] doubletexts = {
            "files/doubletarget1.png",
            "files/doubletarget2.png"
    };
    public static BufferedImage[] ghostfiles = new BufferedImage[textures.length];
    public static BufferedImage[] doublefiles = new BufferedImage[doubletexts.length];

    //
    public static AudioInputStream shoot;
    public static Clip shoot_clip;

    public static String s_path = "files/bong.wav";
    public static String impressive_path = "files/impressive.wav";
    public static String pew_path = "files/pew.wav";

    public TargetCourt(JLabel status) {
        setLayout(null);

        announce_label = new JLabel("prepare to FIGHT");
        score_label = new JLabel("score: " + score);
        live_label = new JLabel("lives: " + lives);

        announce_label.setFont(new Font("Papyrus", Font.PLAIN, 60));
        announce_label.setBounds(0, -240, COURT_WIDTH, COURT_HEIGHT);
        add(announce_label);

        score_label.setFont(new Font("Papyrus", Font.PLAIN, 30));
        score_label.setBounds(0, -20, COURT_WIDTH, COURT_HEIGHT);
        add(score_label);

        live_label.setText("lives: " + lives);
        live_label.setFont(new Font("Papyrus", Font.PLAIN, 30));
        live_label.setBounds(0, 10, COURT_WIDTH, COURT_HEIGHT);
        add(live_label);

        try {
            if (img == null) {
                img = ImageIO.read(new File(IMG_FILE));
                fastimg = ImageIO.read(new File(FAST_FILE));
                for(int i=0; i< textures.length; i++){
                    ghostfiles[i] = ImageIO.read(new File(textures[i]));
                }
                for(int i=0; i< doubletexts.length; i++){
                    doublefiles[i] = ImageIO.read(new File(doubletexts[i]));
                }
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // The timer is an object which triggers an action periodically with the
        // given INTERVAL. We register an ActionListener with this timer, whose
        // actionPerformed() method is called each time the timer triggers. We
        // define a helper method called tick() that actually does everything
        // that should be done in a single time step.
        System.out.println("yeah");

        this.status = status;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                play(pew_path);
                System.out.println("mouse_click");
                Point p = e.getPoint();

                //targetstoRemove = new ArrayList<TargetObj>();
                points = new ArrayList<>();
                targetpoints = new ArrayList<>();

                // updates the model given the coordinates of the mouseclick
                points.add(p);

                var targetstoRemove = new ArrayList<TargetObj>();
                int targets_hit = 0;
                String msg = "";

                for(TargetObj target : targets){
                    int tx = target.getPx();
                    int ty = target.getPy();

                    targetpoints.add(new Point(tx, ty));
                    if(target.isHit(p.x , p.y)){
                        System.out.println("Hit!");
                        targetstoRemove.add(target);
                        targets_hit++;
                    }
                }

                for(TargetObj target_bye : targetstoRemove){
                    score++;
                    streak++;

                    int tx = target_bye.getPx();
                    int ty = target_bye.getPy();
                    int rad = target_bye.getRadius();

                    int bits = (int)Math.floor(Math.random() * 3 ) + 3;

                    for(int i=0; i<bits; i++){ // make the plate shatter
                        double vy = Math.random()*10 + 2;
                        double vx = Math.random()*10 + target_bye.getVX() - 5;

                        TargetObj bit = new Plate(tx, ty, vx, vy, rad / bits);
                        details.add(bit);
                    }
                    targets.remove(target_bye);

                    if(streak % 5 == 0){
                        play(impressive_path);
                    }
                }
                msg = "Score : " + score;
                if(targets_hit > 1){
                    msg += "; Collat! Nice!";
                }
                updateStatus(msg); // updates the status JLabel
                score_label.setText("score: " + score);
                repaint(); // repaints the game board
            }
        });

        Timer timer = new Timer(TICK_RATE, e -> tick());
        timer.start(); // MAKE SURE TO START THE TIMER!

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);
    }

    public void reset() {
        targets = new ArrayList<TargetObj>();
        lives = 5;
        score = 0;
        streak = 0;
        time = 0;
        netTime = 0;

        SPAWN_RATE = INIT_SPAWN_RATE;
        run = true;

        announce_label.setText("prepare to FIGHT");
        score_label.setText("score: " + score);
        live_label.setText("lives: " + lives);

        updateStatus("Running...");
        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }

    public static synchronized void play(String song_path){
        new Thread(new Runnable() {
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(song_path));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                }
            }
        }).start();
    }

    void tick(){
        if (run) {
            repaint();

            SPAWN_RATE = (int) (100 + -(Math.log(netTime/100+1) / Math.log(2)) );
            FUNNY_RATE = 0.5*Math.pow(-0.1*netTime/100 - 1, -1) + 0.5;

            time++;
            netTime++;
            
            movestuff(targets);
            movestuff(details);

            if(time >= SPAWN_RATE){
                spawn();
                time = 0;
            }
            // update the display
        }
    }
    private void movestuff(ArrayList<TargetObj> master) {
        var toRemove = new ArrayList<TargetObj>();
        for(TargetObj obj : master){
            obj.move();
            if(obj.getPy() > COURT_WIDTH && obj.getVY() >= 0){
                toRemove.add(obj);
            }
        }
        for(TargetObj obj_gone : toRemove){
            master.remove(obj_gone);
            if(!(obj_gone instanceof Plate)){
                lives--;
                streak = 0;
                live_label.setText("lives: " + lives);
                if(lives <= 0){
                    run = false;
                    announce_label.setText("you lost L");
                }
            }
        }
    }
    public TargetObj returnTarget(double chance, int x, int y, int vx, int vy){
        if(chance < FUNNY_RATE * 0.4) return new Fast(x,y, vx, vy*1.3);
        if(chance < FUNNY_RATE * 0.7) return new DoubleTarget(x,y, vx, vy);
        if(chance < FUNNY_RATE) return new Ghost(x,y, vx, vy);
        return new Normal(x,y,vx,vy);
    }
    void spawn(){
        double chance = Math.random();
        TargetObj newTarget = null;

        int rand_x = (int)Math.floor(Math.random() * COURT_WIDTH);
        int vx = 2;
        int vy = (int)Math.floor(Math.random() * 12 + 12);
        int pos_y = COURT_HEIGHT;

        if(rand_x > COURT_WIDTH/2){
            vx *= -1;
        }

        newTarget = returnTarget(chance, rand_x, pos_y, vx, vy);

        if(newTarget != null){
            targets.add(newTarget);
        }
    }
    void updateStatus(String msg){
        status.setText(msg);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for(TargetObj target : targets){
            target.draw(g);
        }
        for(TargetObj bit : details){
            bit.draw(g);
        }
        for(Point p : points){
            g.drawOval(p.x, p.y, 5,5);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
}
