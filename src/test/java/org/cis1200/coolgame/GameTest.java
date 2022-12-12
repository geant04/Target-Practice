package org.cis1200.coolgame;

import com.beust.ah.A;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.lang.annotation.Target;
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

    @Test
    public void checkisHit(){
        TargetObj b = new Normal(0,0,0,0);
        assertTrue(b.isHit(4,4));
    }

    @Test
    public void checkisHitFalse(){
        TargetObj b = new Normal(0,0,0,0);
        assertFalse(b.isHit(50,50));
    }

    @Test
    public void checkIsCollat(){
        TargetObj b = new Normal(0,0,0,0);
        TargetObj c = new Normal(1,1,0,0);
        assertTrue(b.isHit(4,4) & c.isHit(4,4));
    }

    @Test
    public void checkMove(){
        double vy_i = 30;
        TargetObj b = new Normal(0,0, 0, vy_i);
        b.move();
        assertTrue(b.getVY() == -vy_i + 0.5);
    }

    @Test
    public void checkMoveDouble(){
        double vy_i = 30;
        TargetObj b = new DoubleTarget(0,0, 0, vy_i, 1);
        b.move();
        assertTrue(b.getVY() == -vy_i + 0.12); // gravity is reduced for this case
    }

    @Test
    public void checkLifeLoss(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();

        game.addTarget(new Normal(0,599,0.1,-2));
        //game.loaditem(2, "1;0;400;60;50.0; 50.0;4.0");
        game.tick();

        assertTrue(4 == game.lives);
    }

    @Test
    public void checkLifeLTwice(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();

        game.addTarget(new Normal(0,599,0.1,-2));
        game.addTarget(new Normal(0,599,0.1,-2));

        //game.loaditem(2, "1;0;400;60;50.0; 50.0;4.0");
        game.tick();

        assertTrue(3 == game.lives);
    }

    @Test
    public void lifeDeath(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.lives = 1;

        game.addTarget(new Normal(0,599,0.1,-2));

        //game.loaditem(2, "1;0;400;60;50.0; 50.0;4.0");
        game.tick();

        assertTrue(0 == game.lives && !game.isPlaying());
    }

    @Test
    public void checkLoaderTarget(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "1;0;400;60;50.0;50.0;4.0");

        ArrayList<TargetObj> stuffs = game.returntargs();

        assertTrue(1 == stuffs.size());
    }

    @Test
    public void checkLoaderDetail(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "0;0;400;60;50.0;50.0;4.0");

        ArrayList<TargetObj> stuffs = game.returndets();

        assertTrue(1 == stuffs.size());
    }

    @Test
    public void checkLoaderEmpty(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.loaditem(2, "");

        ArrayList<TargetObj> stuffs = game.returntargs();

        assertTrue(0 == stuffs.size());
    }

    @Test
    public void checkHighScoreHighStreak(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();

        game.highscore = 40;
        game.highstreak = 40;

        game.reset();
        // the point is that it should still be the same after reset

        assertTrue(game.highscore == 40 && game.highstreak == 40);
    }

    @Test
    public void checkSaveAndLoad(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.addTarget(new Normal(0,40,0.1,-2));

        game.SaveGame();
        game.reset();
        game.LoadGame(false);

        ArrayList<TargetObj> s = game.returntargs();

        assertTrue(1 == s.size());
    }

    @Test
    public void checkSaveNothing(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.SaveGame();
        game.reset();
        game.LoadGame(false);

        ArrayList<TargetObj> s = game.returntargs();

        assertTrue(0 == s.size());
    }

    @Test
    public void checkSaveAndLoadGeneral(){
        TargetCourt game = new TargetCourt(new JLabel(""), true);
        game.reset();
        game.addTarget(new Normal(0,40,0.1,-2));
        game.addTarget(new Ghost(0,40,0.1,-2, 0.1));
        game.addTarget(new Plate(0,40,0.1,-2, 10));


        game.SaveGame();
        game.reset();
        game.LoadGame(false);

        ArrayList<TargetObj> s = game.returntargs();
        ArrayList<TargetObj> d = game.returndets();

        assertTrue(2 == s.size() && 1 == d.size());
    }


}
