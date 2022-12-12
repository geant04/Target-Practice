package org.cis1200.coolgame;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.annotation.Target;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Math.max;

public class TargetCourt extends JPanel {

    // ------------- store all your stuff above this line

    private boolean run = false;
    private final JLabel status;
    private JLabel announce_label;
    private JLabel helpful_tip;
    private JLabel live_label;
    private JLabel score_label;
    private JLabel streak_label;
    private JLabel hs_label;
    private JLabel hstreak_label;

    public static String[] tips = {
        "eat lots of corn",
        "clicking the target makes you win",
        "get good",
        "get good aim",
        "have u tried using a better chair",
        "idk i forgot",
        "try to avoid hill dining",
        "click the target",
        "clicc",
        "thank you lord swap",
        "i coded this game badly so try aiming below the targets"
    };

    public static final int COURT_WIDTH = 600;
    public static final int COURT_HEIGHT = 600;
    public static int lives = 5;
    public static int score = 0;
    public static int streak = 0;

    public static int highscore = 0;
    public static int highstreak = 0;

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

    // images

    public final String funnybg = "files/think.png";
    public static final String IMG_FILE = "files/target.png";
    public static final String FAST_FILE = "files/fasttarget.png";
    public static BufferedImage img;
    public static BufferedImage fastimg;
    public static BufferedImage bg_img;
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

    //---- audio
    public static AudioInputStream shoot;
    public static AudioInputStream bgm_audio;
    public static Clip shoot_clip;
    public static Clip bgm;

    public static String s_path = "files/bong.wav";
    public static String impressive_path = "files/impressive.wav";
    public static String pew_path = "files/pew.wav";
    public static String bgm_path = "files/bgm.wav";

    // --- save file location

    public static final String save_path = "save/state.txt";

    public TargetCourt(JLabel status, boolean reset) {
        setLayout(null);

        announce_label = new JLabel("prepare to FIGHT");
        helpful_tip = new JLabel(
                "tip: " +
                        tips[(int) Math.floor(Math.random() * tips.length)]
        );
        score_label = new JLabel("score: " + score);
        live_label = new JLabel("lives: " + lives);
        streak_label = new JLabel("streak: " + streak);
        hs_label = new JLabel("highscore: " + highscore);
        hstreak_label = new JLabel("high streak: " + highstreak);

        announce_label.setFont(new Font("Papyrus", Font.PLAIN, 60));
        announce_label.setBounds(0, -240, COURT_WIDTH, COURT_HEIGHT);
        add(announce_label);

        helpful_tip.setFont(new Font("Papyrus", Font.PLAIN, 20));
        helpful_tip.setBounds(0, -190, COURT_WIDTH, COURT_HEIGHT);
        add(helpful_tip);

        hstreak_label.setFont(new Font("Papyrus", Font.PLAIN, 30));
        hstreak_label.setBounds(0, -120, COURT_WIDTH, COURT_HEIGHT);
        add(hstreak_label);

        hs_label.setFont(new Font("Papyrus", Font.PLAIN, 30));
        hs_label.setBounds(0, -90, COURT_WIDTH, COURT_HEIGHT);
        add(hs_label);

        streak_label.setFont(new Font("Papyrus", Font.PLAIN, 30));
        streak_label.setBounds(0, -50, COURT_WIDTH, COURT_HEIGHT);
        streak_label.setForeground(Color.BLACK);
        add(streak_label);

        score_label.setFont(new Font("Papyrus", Font.PLAIN, 30));
        score_label.setBounds(0, -20, COURT_WIDTH, COURT_HEIGHT);
        add(score_label);

        live_label.setText("lives: " + lives);
        live_label.setFont(new Font("Papyrus", Font.PLAIN, 30));
        live_label.setBounds(0, 10, COURT_WIDTH, COURT_HEIGHT);
        add(live_label);

        JLabel the = new JLabel();
        the.setIcon(new ImageIcon(IMG_FILE));
        add(the);

        try {
            shoot_clip = AudioSystem.getClip();
            shoot = AudioSystem.getAudioInputStream(new File(impressive_path));
            shoot_clip.open(shoot);

            bgm = AudioSystem.getClip();
            bgm_audio = AudioSystem.getAudioInputStream(new File(bgm_path));
            bgm.open(bgm_audio);
        } catch (Exception e) {
        }

        try {
            if (img == null) {
                img = ImageIO.read(new File(IMG_FILE));
                fastimg = ImageIO.read(new File(FAST_FILE));
                for (int i = 0; i < textures.length; i++) {
                    ghostfiles[i] = ImageIO.read(new File(textures[i]));
                }
                for (int i = 0; i < doubletexts.length; i++) {
                    doublefiles[i] = ImageIO.read(new File(doubletexts[i]));
                }
                bg_img = ImageIO.read(new File("files/think.png"));
            }
        } catch (IOException e) {
            //System.out.println("Internal Error:" + e.getMessage());
        }
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.status = status;
        bgm.setFramePosition(0);
        bgm.start();


        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (run) { // make sure we don't do anything funny if we lose
                    Point p = e.getPoint();

                    points = new ArrayList<>();
                    // updates the model given the coordinates of the mouseclick
                    points.add(p);

                    var targetstoRemove = new ArrayList<TargetObj>();
                    int targets_hit = 0;
                    String msg = "";

                    for (TargetObj target : targets) {
                        int tx = target.getPx();
                        int ty = target.getPy();

                        targetpoints.add(new Point(tx, ty));
                        if (target.isHit(p.x, p.y)) {
                            System.out.println("Hit!");
                            targetstoRemove.add(target);
                            targets_hit++;
                        }
                    }

                    for (TargetObj target_bye : targetstoRemove) {
                        score++;
                        streak++;

                        highscore = max(highscore, score);
                        highstreak = max(highstreak, streak);

                        int tx = target_bye.getPx();
                        int ty = target_bye.getPy();
                        int rad = target_bye.getRadius();

                        int bits = (int) Math.floor(Math.random() * 2) + 2;

                        for (int i = 0; i < bits; i++) { // make the plate shatter
                            double vy = Math.random() * 10 + 2;
                            double vx = Math.random() * 10 + target_bye.getVX() - 5;

                            TargetObj bit = new Plate(tx, ty, vx, vy, rad / bits);
                            details.add(bit);
                        }
                        targets.remove(target_bye);

                        if (streak % 5 == 0) {
                            streak_label.setForeground(Color.ORANGE);
                            shoot_clip.setFramePosition(0);
                            shoot_clip.start();
                        }
                    }
                    msg = "Score : " + score;
                    if (targets_hit > 1) {
                        msg += "; Collat! Nice!";
                    }
                    updateStatus(msg); // updates the status JLabel
                    score_label.setText("score: " + score);
                    streak_label.setText("streak: " + streak);
                    hs_label.setText("highscore: " + highscore);
                    hstreak_label.setText("high streak: " + highstreak);

                    repaint(); // repaints the game board
                }
            }
        });

        if(!reset){
            LoadGame(true);
        }

        Timer timer = new Timer(TICK_RATE, e -> tick());
        timer.start(); // MAKE SURE TO START THE TIMER!

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);
    }

    public void reset() {
        targets = new ArrayList<TargetObj>();
        details = new ArrayList<TargetObj>();

        lives = 5;
        score = 0;
        streak = 0;
        time = 0;
        netTime = 0;

        SPAWN_RATE = INIT_SPAWN_RATE;
        run = true;

        announce_label.setText("prepare to FIGHT");
        helpful_tip.setText(
                "tip: " +
                        tips[(int) Math.floor(Math.random() * tips.length)]
        );

        score_label.setText("score: " + score);
        live_label.setText("lives: " + lives);
        streak_label.setText("streak: " + streak);
        streak_label.setForeground(Color.BLACK);

        bgm.setFramePosition(0);
        bgm.start();

        updateStatus("Running...");
        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }
    public boolean isPlaying(){
        return run;
    }
    public void SaveGame(){
        // keybind "s" should save the game

        File file = new File(save_path);
        BufferedWriter bw;
        try{
            // 1. Log lives, streak, score, highest streak, highest score (can be done by a single line)
            // 2. Log in-game time, net time, run (same format as first, rates depend on time)
            // 3. Log target and detail positions (each line for each target)
            // first line format: int ; (separated by ;)
            // ex: 3;4;25;34;52
            // second
            // target types:
            //      1: default
            //      2: ghost
            //      3: fast
            //      4: double-1
            //      -4: double-2
            //      5: etc....???

            // 2,3 format: # for target type -- followed by ";", each part after represents data
            // ex: 1;x;y;rad;vx;vy
            //

            if(!file.createNewFile()){
                file = Paths.get(save_path).toFile();
            }
            bw = new BufferedWriter(new FileWriter(file));

            String scores = lives+";"+streak+";"+score+";"+highscore+";"+highstreak+"\n";
            String ingame_data = time+";"+netTime+";"+(run ? 1:0)+"\n"; // convert bool to int to make it easier

            bw.write(scores);
            bw.write(ingame_data);

            ArrayList<TargetObj> objs = new ArrayList<>();
            objs.addAll(targets);
            objs.addAll(details);

            for(TargetObj t : objs){
                String line = "";
                int type = t.getType();
                int x = t.getPx();
                int y = t.getPy();
                double vx = t.getVX();
                double vy = t.getVY() * -1;
                double maxV = t.getMaxV();
                int rad = t.getRadius();

                line += type + ";" + x + ";" + y + ";" + rad + ";"
                        + String.valueOf(vx) + ";"
                        + String.valueOf(vy) + ";"
                        + String.valueOf(maxV) +
                        "\n";
                bw.write(line);
            }
            bw.close();
        } catch (IOException e) {
        }
    }

    public void LoadGame(boolean def){
        // keybind "l" should load the game
        reset();

        File file = Paths.get(save_path).toFile();
        try{
            BufferedReader r = new BufferedReader(new FileReader(file));
            String line = r.readLine(); // the file shouldn't have any blank lines.
            while (line != null && line.isEmpty()) {
                line = r.readLine();
            } // given that the first line is blank, let's run through until we do find the first line
            targets = new ArrayList<>();
            points = new ArrayList<>();

            int indx = 0;
            String current = line;

            while(line != null){
                current = line;
                if(!current.isEmpty()){
                    loaditem(indx, line);
                    indx++;
                }
                line = r.readLine();
                if(def && indx > 1){
                    break;
                }
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        score_label.setText("score: " + score);
        streak_label.setText("streak: " + streak);
        if(streak >= 5){
            streak_label.setForeground(Color.ORANGE);
        }
        hs_label.setText("highscore: " + highscore);
        hstreak_label.setText("high streak: " + highstreak);
        helpful_tip.setText(
                "tip: " +
                        tips[(int) Math.floor(Math.random() * tips.length)]
        );
        live_label.setText("lives: " + lives);

        if(lives <= 0){
            announce_label.setText("you lost L");
        }else{
            announce_label.setText("prepare to FIGHT");
        }
        repaint(); // repaints the game board
    }
    public void loaditem(int indx, String data){
        if(data == null || data.isEmpty()){
            return;
        }

        String[] parsed_data = data.split(";");
        int len = parsed_data.length;
        int[] intdata = new int[len];

        if(len <= 4 || len > 7){
            return;
        }

        if(indx == 0){
            for(int i=0; i < len; i++){
                intdata[i] = Integer.parseInt(parsed_data[i]);
            }
            lives = intdata[0];
            streak = intdata[1];
            score = intdata[2];
            highscore = intdata[3];
            highstreak = intdata[4];
            return;
        }
        if(indx == 1){
            for(int i=0; i < len; i++){
                intdata[i] = Integer.parseInt(parsed_data[i]);
            }
            time = intdata[0];
            netTime = intdata[1];
            run = (intdata[2] == 1);
            return;
        }
        // i think we do other stuff now yes
        double[] ext = new double[3];

        for(int i=0; i<parsed_data.length; i++){
            if(i<4){
                intdata[i] = Integer.parseInt(parsed_data[i]);
            }else{
                ext[i-4] = Double.parseDouble(parsed_data[i]);
            }
        }

        TargetObj new_obj = loadTarget(
                intdata[0], intdata[1], intdata[2], intdata[3],
                ext[0], ext[1], ext[2]
        );
        if(new_obj != null){
            int t = intdata[0];
            if(t==0){
                details.add(new_obj);
                return;
            }
            targets.add(new_obj);
        }
    }

    public ArrayList<TargetObj> returntargs(){
        ArrayList<TargetObj> encaps = targets;
        return encaps;
    }
    public ArrayList<TargetObj> returndets(){
        ArrayList<TargetObj> encaps = details;
        return encaps;
    }
    public void addTarget(TargetObj obj){
        targets.add(obj);
    }

    public TargetObj loadTarget(int type, int x, int y, int rad, double vx, double vy, double mv) {
        if (type == 0)
            return new Plate(x,y,vx,vy*-1,rad);
        if (type == 1)
            return new Normal(x, y, vx, vy);
        if (type == 2)
            return new Ghost(x, y, vx, vy, mv);
        if (type == 3)
            return new Fast(x, y, vx, vy);
        if (type == 4)
            return new DoubleTarget(x, y, vx, vy, 0);
        if (type == -4)
            return new DoubleTarget(x, y, vx, vy, 1);
        return null;
    }


    void tick() {
        if (run) {
            repaint();

            SPAWN_RATE = (int) (100 + -(Math.log(netTime / 100 + 1) / Math.log(2)));
            FUNNY_RATE = 0.5 * Math.pow(-0.1 * netTime / 100 - 1, -1) + 0.5;

            time++;
            netTime++;

            movestuff(targets);
            movestuff(details);

            if (time >= SPAWN_RATE) {
                spawn();
                time = 0;
            }
            // update the display
        }
    }

    private void movestuff(ArrayList<TargetObj> master) {
        var toRemove = new ArrayList<TargetObj>();
        for (TargetObj obj : master) {
            obj.move();
            if (obj.getPy() > COURT_WIDTH && obj.getVY() >= 0) {
                toRemove.add(obj);
            }
        }
        for (TargetObj obj_gone : toRemove) {
            master.remove(obj_gone);
            if (!(obj_gone instanceof Plate)) {
                lives--;
                streak = 0;
                live_label.setText("lives: " + lives);
                streak_label.setText("streak: " + streak);
                streak_label.setForeground(Color.BLACK);

                if (lives <= 0) {
                    run = false;
                    announce_label.setText("you lost L");
                }
            }
        }
    }

    public TargetObj returnTarget(double chance, int x, int y, int vx, int vy) {
        if (chance < FUNNY_RATE * 0.4)
            return new Fast(x, y, vx, vy * 1.3);
        if (chance < FUNNY_RATE * 0.7)
            return new DoubleTarget(x, y, vx, vy, 0);
        if (chance < FUNNY_RATE)
            return new Ghost(x, y, vx, vy, vy); // mv is the same as vy since it just spawned
        return new Normal(x, y, vx, vy);
    }

    void spawn() {
        double chance = Math.random();
        TargetObj newTarget = null;

        int rand_x = (int) Math.floor(Math.random() * COURT_WIDTH);
        int vx = 2;
        int vy = (int) Math.floor(Math.random() * 12 + 12);
        int pos_y = COURT_HEIGHT;

        if (rand_x > COURT_WIDTH / 2) {
            vx *= -1;
        }

        newTarget = returnTarget(chance, rand_x, pos_y, vx, vy);

        if (newTarget != null) {
            targets.add(newTarget);
        }
    }

    void updateStatus(String msg) {
        status.setText(msg);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawImage(bg_img, 0, 0, bg_img.getWidth(), bg_img.getHeight() * 2, null);

        for (TargetObj target : targets) {
            target.draw(g);
        }
        for (TargetObj bit : details) {
            bit.draw(g);
        }
        for (Point p : points) {
            g.drawOval(p.x, p.y, 5, 5);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
}
