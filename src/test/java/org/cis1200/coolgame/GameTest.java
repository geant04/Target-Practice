package org.cis1200.coolgame;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * You can use this file (and others) to test your
 * implementation.
 */

// check isHit works DONE
// check isHit doesn't work DONE
// check move works for normal target DONE
// check move works for double target after getting hit once DONE
// check collat DONE
// check losing one life after one falls DONE
// check losing two live after two fall DONE
// check losing game after one falls and no lives DONE
// check highscore and highstreak DONE
// check text parser DONE (this kind of goes under reader)
// check text reader DONE
// check text reader empty line DONE
// check saving something DONE
// check saving nothing and then loading DONE
// check loading in general
// check loading on an empty file

public class GameTest {

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

    public void durr() {
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
    }

    @Test
    public void checkisHit() {
        durr();
        TargetObj b = new Normal(0, 0, 0, 0, img);
        assertTrue(b.isHit(4, 4));
    }

    @Test
    public void checkisHitFalse() {
        durr();
        TargetObj b = new Normal(0, 0, 0, 0, img);
        assertFalse(b.isHit(50, 50));
    }

    @Test
    public void checkIsCollat() {
        durr();
        TargetObj b = new Normal(0, 0, 0, 0, img);
        TargetObj c = new Normal(1, 1, 0, 0, img);
        assertTrue(b.isHit(4, 4) & c.isHit(4, 4));
    }

    @Test
    public void checkMove() {
        durr();

        double vyi = 30;
        TargetObj b = new Normal(0, 0, 0, vyi, img);
        b.move();
        assertEquals(b.getVY(), -vyi + 0.5);
    }

    @Test
    public void checkMoveDouble() {
        durr();

        double vyi = 30;
        TargetObj b = new DoubleTarget(0, 0, 0, vyi, 1, doublefiles);
        b.move();
        assertEquals(b.getVY(), -vyi + 0.12); // gravity is reduced for this case
    }

    @Test
    public void checkLifeLoss() {
        durr();

        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();

        game.addTarget(new Normal(0, 599, 0.1, -2, img));
        // game.loaditem(2, "1;0;400;60;50.0; 50.0;4.0");
        game.tick();

        assertEquals(4, game.returnLives());
    }

    @Test
    public void checkLifeLTwice() {
        durr();

        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();

        game.addTarget(new Normal(0, 599, 0.1, -2, img));
        game.addTarget(new Normal(0, 599, 0.1, -2, img));

        // game.loaditem(2, "1;0;400;60;50.0; 50.0;4.0");
        game.tick();

        assertEquals(3, game.returnLives());
    }

    @Test
    public void lifeDeath() {
        durr();

        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.setLives(1);

        game.addTarget(new Normal(0, 599, 0.1, -2, img));

        // game.loaditem(2, "1;0;400;60;50.0; 50.0;4.0");
        game.tick();

        assertEquals(0 == game.returnLives(), !game.isPlaying());
    }

    @Test
    public void checkLoaderTarget() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "1;0;400;60;50.0;50.0;4.0");

        ArrayList<TargetObj> stuffs = game.returntargs();

        assertEquals(1, stuffs.size());
    }

    @Test
    public void checkLoaderDetail() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "0;0;400;60;50.0;50.0;4.0");

        ArrayList<TargetObj> stuffs = game.returndets();

        assertEquals(1, stuffs.size());
    }

    @Test
    public void checkLoaderEmpty() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "");

        ArrayList<TargetObj> stuffs = game.returntargs();

        assertEquals(0, stuffs.size());
    }

    @Test
    public void checkHighScoreHighStreak() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();

        game.setHighscore(40);
        game.setHighstreak(40);

        game.reset();
        // the point is that it should still be the same after reset

        assertEquals(game.getHighscore() == 40, game.getHighstreak() == 40);
    }

    @Test
    public void checkSaveAndLoad() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.addTarget(new Normal(0, 40, 0.1, -2, null));

        game.saveGame();
        game.reset();
        game.loadGame(false);

        ArrayList<TargetObj> s = game.returntargs();

        assertEquals(1, s.size());
    }

    @Test
    public void checkSaveNothing() {
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.saveGame();
        game.reset();
        game.loadGame(false);

        ArrayList<TargetObj> s = game.returntargs();

        assertEquals(0, s.size());
    }

    @Test
    public void checkSaveAndLoadGeneral() {
        durr();

        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.addTarget(new Normal(0, 40, 0.1, -2, img));
        game.addTarget(new Ghost(0, 40, 0.1, -2, 0.1, ghostfiles));
        game.addTarget(new Plate(0, 40, 0.1, -2, 10));

        game.saveGame();
        game.reset();
        game.loadGame(false);

        ArrayList<TargetObj> s = game.returntargs();
        ArrayList<TargetObj> d = game.returndets();

        assertEquals(2 == s.size(), 1 == d.size());
    }

}
