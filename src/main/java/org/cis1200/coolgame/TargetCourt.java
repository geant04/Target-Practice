package org.cis1200.coolgame;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.lang.Math.max;

public class TargetCourt extends JPanel {

    // ------------- store all your stuff above this line

    private boolean run = false;
    private final JLabel status;
    private JLabel announceLabel;
    private JLabel helpfulTip;
    private JLabel liveLabel;
    private JLabel scoreLabel;
    private JLabel streakLabel;
    private JLabel hsLabel;
    private JLabel hstreakLabel;

    private static String[] tips = {
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
        "try aiming below the targets"
    };

    public static final int COURT_WIDTH = 600;
    public static final int COURT_HEIGHT = 600;
    private static int lives = 5;
    private static int score = 0;
    private static int streak = 0;

    private static int highscore = 0;
    private static int highstreak = 0;

    // Update interval for timer, in milliseconds
    public static final int TICK_RATE = 15;
    public static final int INIT_SPAWN_RATE = 100;
    private static int time = 0;
    private static int netTime = 0;
    private static int spawnRate = 100;
    private static double funnyRate = 0;
    // this is the rate in which we spawn funny targets instead of normal ones
    private ArrayList<TargetObj> targets = new ArrayList<TargetObj>();
    private ArrayList<TargetObj> details = new ArrayList<TargetObj>();
    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<Point> targetpoints = new ArrayList<>();

    // images

    private static final String IMG_FILE = "files/target.png";
    private static final String FAST_FILE = "files/fasttarget.png";

    private static BufferedImage img;
    private static BufferedImage fastimg;

    private static String[] textures = {
        "files/ghosttarget4.png",
        "files/ghosttarget3.png",
        "files/ghosttarget2.png",
        "files/ghosttarget.png"
    };
    private static String[] doubletexts = {
        "files/doubletarget1.png",
        "files/doubletarget2.png"
    };
    private static BufferedImage[] ghostfiles = new BufferedImage[textures.length];
    private static BufferedImage[] doublefiles = new BufferedImage[doubletexts.length];

    // ---- audio
    private AudioInputStream shoot;
    private AudioInputStream bgmAudio;
    private Clip shootClip;
    private Clip bgm;

    private String impressivePath = "files/impressive.wav";
    private String bgmPath = "files/bgm.wav";

    // --- save file location

    public static final String SAVE_PATH = "files/state.txt";

    public TargetCourt(JLabel status, boolean reset) {
        setLayout(null);
        int relative_delta = -80;

        announceLabel = new JLabel("prepare to FIGHT");
        helpfulTip = new JLabel(
                "tip: " +
                        tips[(int) Math.floor(Math.random() * tips.length)]
        );
        scoreLabel = new JLabel("score: " + score);
        liveLabel = new JLabel("lives: " + lives);
        streakLabel = new JLabel("streak: " + streak);
        hsLabel = new JLabel("highscore: " + highscore);
        hstreakLabel = new JLabel("high streak: " + highstreak);

        announceLabel.setFont(new Font("Helvetica", Font.PLAIN, 60));
        announceLabel.setBounds(0, -240 + relative_delta, COURT_WIDTH, COURT_HEIGHT);
        //add(announceLabel);

        helpfulTip.setFont(new Font("Helvetica", Font.PLAIN, 20));
        helpfulTip.setBounds(0, -190 + relative_delta, COURT_WIDTH, COURT_HEIGHT);
        add(helpfulTip);

        hstreakLabel.setFont(new Font("Helvetica", Font.PLAIN, 30));
        hstreakLabel.setBounds(0, -120 + relative_delta, COURT_WIDTH, COURT_HEIGHT);
        add(hstreakLabel);

        hsLabel.setFont(new Font("Helvetica", Font.PLAIN, 30));
        hsLabel.setBounds(0, -90 + relative_delta, COURT_WIDTH, COURT_HEIGHT);
        add(hsLabel);

        streakLabel.setFont(new Font("Helvetica", Font.PLAIN, 30));
        streakLabel.setBounds(0, -50 + relative_delta, COURT_WIDTH, COURT_HEIGHT);
        streakLabel.setForeground(Color.BLACK);
        add(streakLabel);

        scoreLabel.setFont(new Font("Helvetica", Font.PLAIN, 30));
        scoreLabel.setBounds(0, -20 + relative_delta, COURT_WIDTH, COURT_HEIGHT);
        add(scoreLabel);

        liveLabel.setText("lives: " + lives);
        liveLabel.setFont(new Font("Helvetica", Font.PLAIN, 30));
        liveLabel.setBounds(0, 10 + relative_delta, COURT_WIDTH, COURT_HEIGHT);
        add(liveLabel);

        JLabel the = new JLabel();
        the.setIcon(new ImageIcon(IMG_FILE));
        add(the);

        try {
            shootClip = AudioSystem.getClip();
            shoot = AudioSystem.getAudioInputStream(new File(impressivePath));
            shootClip.open(shoot);
            FloatControl gain = (FloatControl) shootClip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue(-10.0f);

            bgm = AudioSystem.getClip();
            bgmAudio = AudioSystem.getAudioInputStream(new File(bgmPath));
            bgm.open(bgmAudio);

            FloatControl gain2 = (FloatControl) bgm.getControl(FloatControl.Type.MASTER_GAIN);
            gain2.setValue(-10.0f);

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
            }
        } catch (IOException e) {
            // System.out.println("Internal Error:" + e.getMessage());
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
                    int targetsHit = 0;
                    String msg = "";

                    for (TargetObj target : targets) {
                        int tx = target.getPx();
                        int ty = target.getPy();

                        targetpoints.add(new Point(tx, ty));
                        if (target.isHit(p.x, p.y)) {
                            System.out.println("Hit!");
                            targetstoRemove.add(target);
                            targetsHit++;
                        }
                    }

                    for (TargetObj targetBye : targetstoRemove) {
                        score++;
                        streak++;

                        highscore = max(highscore, score);
                        highstreak = max(highstreak, streak);

                        int tx = targetBye.getPx();
                        int ty = targetBye.getPy();
                        int rad = targetBye.getRadius();

                        int bits = (int) Math.floor(Math.random() * 2) + 2;

                        for (int i = 0; i < bits; i++) { // make the plate shatter
                            double vy = Math.random() * 10 + 2;
                            double vx = Math.random() * 10 + targetBye.getVX() - 5;

                            TargetObj bit = new Plate(tx, ty, vx, vy, rad / bits);
                            details.add(bit);
                        }
                        targets.remove(targetBye);

                        if (streak % 5 == 0) {
                            streakLabel.setForeground(Color.ORANGE);
                            shootClip.setFramePosition(0);
                            shootClip.start();
                        }
                    }
                    msg = "Score : " + score;
                    if (targetsHit > 1) {
                        msg += "; Collat! Nice!";
                    }
                    updateStatus(msg); // updates the status JLabel
                    scoreLabel.setText("score: " + score);
                    streakLabel.setText("streak: " + streak);
                    hsLabel.setText("highscore: " + highscore);
                    hstreakLabel.setText("high streak: " + highstreak);

                    repaint(); // repaints the game board
                }
            }
        });

        if (!reset) {
            loadGame(true);
        }

        Timer timer = new Timer(TICK_RATE, e -> tick());
        timer.start(); // MAKE SURE TO START THE TIMER!

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    System.out.println("save");
                    saveGame();
                }
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    System.out.println("load");
                    loadGame(false);
                }
            }
        });

    }

    public void reset() {
        targets = new ArrayList<TargetObj>();
        details = new ArrayList<TargetObj>();

        lives = 5;
        score = 0;
        streak = 0;
        time = 0;
        netTime = 0;

        spawnRate = INIT_SPAWN_RATE;
        run = true;

        announceLabel.setText("prepare to FIGHT");
        helpfulTip.setText(
                "tip: " +
                        tips[(int) Math.floor(Math.random() * tips.length)]
        );

        scoreLabel.setText("score: " + score);
        liveLabel.setText("lives: " + lives);
        streakLabel.setText("streak: " + streak);
        streakLabel.setForeground(Color.BLACK);

        bgm.setFramePosition(0);
        bgm.start();

        updateStatus("Running...");
        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }

    public boolean isPlaying() {
        return run;
    }

    public void saveGame() {
        // keybind "s" should save the game

        File file = new File(SAVE_PATH);
        BufferedWriter bw;
        try {
            // 1. Log lives, streak, score, highest streak, highest score (can be done by a
            // single line)
            // 2. Log in-game time, net time, run (same format as first, rates depend on
            // time)
            // 3. Log target and detail positions (each line for each target)
            // first line format: int ; (separated by ;)
            // ex: 3;4;25;34;52
            // second
            // target types:
            // 1: default
            // 2: ghost
            // 3: fast
            // 4: double-1
            // -4: double-2
            // 5: etc....???

            // 2,3 format: # for target type -- followed by ";", each part after represents
            // data
            // ex: 1;x;y;rad;vx;vy
            //

            if (!file.createNewFile()) {
                file = Paths.get(SAVE_PATH).toFile();
            }
            bw = new BufferedWriter(new FileWriter(file));

            String scores = lives + ";" + streak + ";" + score + ";" + highscore + ";" + highstreak
                    + "\n";
            String ingameData = time + ";" + netTime + ";" + (run ? 1 : 0) + "\n"; // convert bool
                                                                                   // to int to
                                                                                   // make it
                                                                                   // easier

            bw.write(scores);
            bw.write(ingameData);

            ArrayList<TargetObj> objs = new ArrayList<>();
            objs.addAll(targets);
            objs.addAll(details);

            for (TargetObj t : objs) {
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
            System.out.println("uh oh");
        }
        System.out.println("saved");
    }

    public void loadGame(boolean def) {
        // keybind "l" should load the game
        run = false;
        reset();

        File file = Paths.get(SAVE_PATH).toFile();
        try {
            BufferedReader r = new BufferedReader(new FileReader(file));
            String line = r.readLine(); // the file shouldn't have any blank lines.
            while (line != null && line.isEmpty()) {
                line = r.readLine();
            } // given that the first line is blank, let's run through until we do find the
              // first line
            targets = new ArrayList<>();
            points = new ArrayList<>();

            int indx = 0;
            String current = line;

            while (line != null) {
                current = line;
                if (!current.isEmpty()) {
                    loaditem(indx, line);
                    indx++;
                }
                line = r.readLine();
                if (def && indx > 1) {
                    break;
                }
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        scoreLabel.setText("score: " + score);
        streakLabel.setText("streak: " + streak);
        if (streak >= 5) {
            streakLabel.setForeground(Color.ORANGE);
        }
        hsLabel.setText("highscore: " + highscore);
        hstreakLabel.setText("high streak: " + highstreak);
        helpfulTip.setText(
                "tip: " +
                        tips[(int) Math.floor(Math.random() * tips.length)]
        );
        liveLabel.setText("lives: " + lives);

        if (lives <= 0) {
            announceLabel.setText("you lost L");
        } else {
            announceLabel.setText("prepare to FIGHT");
        }
        repaint(); // repaints the game board
    }

    public void loaditem(int indx, String data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        String[] pdata = data.split(";");
        int len = pdata.length;
        int[] intdata = new int[len];

        if (len <= 4 || len > 7) {
            return;
        }

        if (indx == 0) {
            for (int i = 0; i < len; i++) {
                intdata[i] = Integer.parseInt(pdata[i]);
            }
            lives = intdata[0];
            streak = intdata[1];
            score = intdata[2];
            highscore = intdata[3];
            highstreak = intdata[4];
            return;
        }
        if (indx == 1) {
            for (int i = 0; i < len; i++) {
                intdata[i] = Integer.parseInt(pdata[i]);
            }
            time = intdata[0];
            netTime = intdata[1];
            run = (intdata[2] == 1);
            return;
        }
        // i think we do other stuff now yes
        double[] ext = new double[3];

        for (int i = 0; i < pdata.length; i++) {
            if (i < 4) {
                intdata[i] = Integer.parseInt(pdata[i]);
            } else {
                ext[i - 4] = Double.parseDouble(pdata[i]);
            }
        }

        TargetObj newObj = loadTarget(
                intdata[0], intdata[1], intdata[2], intdata[3],
                ext[0], ext[1], ext[2]
        );
        if (newObj != null) {
            int t = intdata[0];
            if (t == 0) {
                details.add(newObj);
                return;
            }
            targets.add(newObj);
        }
    }

    public ArrayList<TargetObj> returntargs() {
        ArrayList<TargetObj> encaps = targets;
        return encaps;
    }

    public ArrayList<TargetObj> returndets() {
        ArrayList<TargetObj> encaps = details;
        return encaps;
    }

    public void addTarget(TargetObj obj) {
        targets.add(obj);
    }

    public int returnLives() {
        return lives;
    }

    public int getHighscore() {
        return highscore;
    }

    public int getStreak() {
        return streak;
    }

    public int getHighstreak() {
        return highstreak;
    }

    public void setHighscore(int v) {
        highscore = v;
    }

    public void setHighstreak(int v) {
        highstreak = v;
    }

    public void setLives(int v) {
        lives = v;
    }

    public TargetObj loadTarget(int type, int x, int y, int rad, double vx, double vy, double mv) {
        if (type == 0) {
            return new Plate(x, y, vx, vy * -1, rad);
        }
        if (type == 1) {
            return new Normal(x, y, vx, vy, img);
        }
        if (type == 2) {
            return new Ghost(x, y, vx, vy, mv, ghostfiles);
        }
        if (type == 3) {
            return new Fast(x, y, vx, vy, fastimg);
        }
        if (type == 4) {
            return new DoubleTarget(x, y, vx, vy, 0, doublefiles);
        }
        if (type == -4) {
            return new DoubleTarget(x, y, vx, vy, 1, doublefiles);
        }
        return null;
    }

    void tick() {
        if (run) {
            repaint();

            spawnRate = (int) (100 + -(Math.log(netTime / 100 + 1) / Math.log(2)));
            funnyRate = 0.5 * Math.pow(-0.1 * netTime / 100 - 1, -1) + 0.5;

            time++;
            netTime++;

            movestuff(targets);
            movestuff(details);

            if (time >= spawnRate) {
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
        for (TargetObj objgone : toRemove) {
            master.remove(objgone);
            if (!(objgone instanceof Plate)) {
                lives--;
                streak = 0;
                liveLabel.setText("lives: " + lives);
                streakLabel.setText("streak: " + streak);
                streakLabel.setForeground(Color.BLACK);

                if (lives <= 0) {
                    run = false;
                    helpfulTip.setText("Game Over!");
                    announceLabel.setText("you lost L");
                }
            }
        }
    }

    public TargetObj returnTarget(double chance, int x, int y, int vx, int vy) {
        if (chance < funnyRate * 0.4) {
            return new Fast(x, y, vx, vy * 1.3, fastimg);
        }
        if (chance < funnyRate * 0.7) {
            return new DoubleTarget(x, y, vx, vy, 0, doublefiles);
        }
        if (chance < funnyRate) {
            return new Ghost(x, y, vx, vy, vy, ghostfiles);
        } // mv is the same as vy since it just spawned
        return new Normal(x, y, vx, vy, img);
    }

    void spawn() {
        double chance = Math.random();

        int randx = (int) Math.floor(Math.random() * COURT_WIDTH);
        int vx = 2;
        int vy = (int) Math.floor(Math.random() * 12 + 12);

        if (randx > COURT_WIDTH / 2) {
            vx *= -1;
        }

        TargetObj newTarget = returnTarget(chance, randx, COURT_HEIGHT, vx, vy);

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
        // g.drawImage(bg_img, 0, 0, bg_img.getWidth(), bg_img.getHeight() * 2, null);

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
