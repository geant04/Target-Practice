package org.cis1200.coolgame;

import javax.swing.*;
import java.awt.*;

public class RunTarget implements Runnable {
    // have an instructions page
    // have a basic level page
    // game loop: every timer (timer changes as the game goes on), a target spawns
    // update the positions of every target every second, if target.Y < 0 &&
    // velocity < 0, -1 life
    // upon mouse click, loop through all targets, if mouse.pos < the pos < radius
    // of the circle, dub
    private boolean gui = false;

    public void run() {
        // NOTE : recall that the 'final' keyword notes immutability even for
        // local variables.

        // Top-level frame in which game components live.
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("Target Practice!");
        frame.setLocation(300, 300);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Running...");
        status_panel.add(status);

        // Main playing area
        final TargetCourt court = new TargetCourt(status, false);
        frame.add(court, BorderLayout.CENTER);

        // Reset button
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> court.reset());
        control_panel.add(reset);

        final JButton save = new JButton("Save");
        save.addActionListener(e -> court.saveGame());
        control_panel.add(save);
        save.setFocusable(false);

        final JButton load = new JButton("Load");
        load.addActionListener(e -> court.loadGame(false));
        control_panel.add(load);

        final JButton instructions = new JButton("Instructions");
        instructions.addActionListener(e -> makeInstructions());
        control_panel.add(instructions);
        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);

        // Start game
        court.reset();
    }

    public void makeInstructions() {
        JFrame nFrame = new JFrame("Target Practice Instructions");
        nFrame.setLocation(300, 300);
        int w = 800;
        int h = 300;

        nFrame.setSize(w, h);
        nFrame.setLayout(new FlowLayout());

        String[] lines = {
            "Click on the targets as they fly in the sky!",
            "Use the left mouse button to click, and move your mouse",
            "to where the targets will land.",
            "If the targets fall and hit the ground, you lose a life!",
            "The game ends when you lose all five lives.",
            "Watch out for different types of targets (normal, ghost, fast, double)!",
            "You'll know what they do in-game.",
            "Extra controls: ",
            "Save: press s",
            "Load: press d"
        };

        for (int i = 0; i < lines.length; i++) {
            JLabel l = new JLabel(lines[i] + "\n");
            l.setFont(new Font("Helvetica", Font.PLAIN, 20));
            // l.setBounds(0, init + 20*i, w, h);
            nFrame.add(l);
        }

        // nFrame.setDefaultCloseOperation(nFrame.EXIT_ON_CLOSE);
        nFrame.setVisible(true);
        nFrame.setResizable(false);
    }
}
