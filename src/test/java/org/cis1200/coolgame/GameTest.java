package org.cis1200.coolgame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameTest {
    private static final String IMG_FILE = "files/target.png";
    private static final String FAST_FILE = "files/fasttarget.png";
    private static BufferedImage img;
    private static BufferedImage fastimg;
    private static String[] textures = new String[]{"files/ghosttarget4.png", "files/ghosttarget3.png", "files/ghosttarget2.png", "files/ghosttarget.png"};
    private static String[] doubletexts = new String[]{"files/doubletarget1.png", "files/doubletarget2.png"};
    private static BufferedImage[] ghostfiles;
    private static BufferedImage[] doublefiles;

    public GameTest() {
    }

    public void durr() {
        try {
            if (img == null) {
                img = ImageIO.read(new File("files/target.png"));
                fastimg = ImageIO.read(new File("files/fasttarget.png"));

                int i;
                for(i = 0; i < textures.length; ++i) {
                    ghostfiles[i] = ImageIO.read(new File(textures[i]));
                }

                for(i = 0; i < doubletexts.length; ++i) {
                    doublefiles[i] = ImageIO.read(new File(doubletexts[i]));
                }
            }
        } catch (IOException var2) {
        }

    }

    @Test
    public void checkisHit() {
        this.durr();
        TargetObj b = new Normal(0, 0, 0.0, 0.0, img);
        Assertions.assertTrue(b.isHit(4, 4));
    }

    @Test
    public void checkisHitFalse() {
        this.durr();
        TargetObj b = new Normal(0, 0, 0.0, 0.0, img);
        Assertions.assertFalse(b.isHit(50, 50));
    }

    @Test
    public void checkIsCollat() {
        this.durr();
        TargetObj b = new Normal(0, 0, 0.0, 0.0, img);
        TargetObj c = new Normal(1, 1, 0.0, 0.0, img);
        Assertions.assertTrue(b.isHit(4, 4) & c.isHit(4, 4));
    }

    @Test
    public void checkMove() {
        this.durr();
        double vyi = 30.0;
        TargetObj b = new Normal(0, 0, 0.0, vyi, img);
        b.move();
        Assertions.assertEquals(b.getVY(), -vyi + 0.5);
    }

    @Test
    public void checkMoveDouble() {
        this.durr();
        double vyi = 30.0;
        TargetObj b = new DoubleTarget(0, 0, 0.0, vyi, 1, doublefiles);
        b.move();
        Assertions.assertEquals(b.getVY(), -vyi + 0.12);
    }

    @Test
    public void checkLifeLoss() {
        this.durr();
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.addTarget(new Normal(0, 599, 0.1, -2.0, img));
        game.tick();
        Assertions.assertEquals(4, game.returnLives());
    }

    @Test
    public void checkLifeLTwice() {
        this.durr();
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.addTarget(new Normal(0, 599, 0.1, -2.0, img));
        game.addTarget(new Normal(0, 599, 0.1, -2.0, img));
        game.tick();
        Assertions.assertEquals(3, game.returnLives());
    }

    @Test
    public void lifeDeath() {
        this.durr();
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.setLives(1);
        game.addTarget(new Normal(0, 599, 0.1, -2.0, img));
        game.tick();
        Assertions.assertEquals(0 == game.returnLives(), !game.isPlaying());
    }

    @Test
    public void checkLoaderTarget() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "1;0;400;60;50.0;50.0;4.0");
        ArrayList<TargetObj> stuffs = game.returntargs();
        Assertions.assertEquals(1, stuffs.size());
    }

    @Test
    public void checkLoaderDetail() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "0;0;400;60;50.0;50.0;4.0");
        ArrayList<TargetObj> stuffs = game.returndets();
        Assertions.assertEquals(1, stuffs.size());
    }

    @Test
    public void checkLoaderEmpty() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "");
        ArrayList<TargetObj> stuffs = game.returntargs();
        Assertions.assertEquals(0, stuffs.size());
    }

    @Test
    public void checkHighScoreHighStreak() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.setHighscore(40);
        game.setHighstreak(40);
        game.reset();
        Assertions.assertEquals(game.getHighscore() == 40, game.getHighstreak() == 40);
    }

    @Test
    public void checkSaveAndLoad() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.addTarget(new Normal(0, 40, 0.1, -2.0, (BufferedImage)null));
        game.saveGame();
        game.reset();
        game.loadGame(false);
        ArrayList<TargetObj> s = game.returntargs();
        Assertions.assertEquals(1, s.size());
    }

    @Test
    public void checkSaveNothing() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.saveGame();
        game.reset();
        game.loadGame(false);
        ArrayList<TargetObj> s = game.returntargs();
        Assertions.assertEquals(0, s.size());
    }

    @Test
    public void checkSaveAndLoadGeneral() {
        this.durr();
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.addTarget(new Normal(0, 40, 0.1, -2.0, img));
        game.addTarget(new Ghost(0, 40, 0.1, -2.0, 0.1, ghostfiles));
        game.addTarget(new Plate(0, 40, 0.1, -2.0, 10));
        game.saveGame();
        game.reset();
        game.loadGame(false);
        ArrayList<TargetObj> s = game.returntargs();
        ArrayList<TargetObj> d = game.returndets();
        Assertions.assertEquals(2 == s.size(), 1 == d.size());
    }

    static {
        ghostfiles = new BufferedImage[textures.length];
        doublefiles = new BufferedImage[doubletexts.length];
    }
}
