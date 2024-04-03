package EuchreGame;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

public class GUI extends JFrame {
    /**
     * Constructor for the main gui window
     */
    public GUI() {
        super();
        setTitle("Euchre Game");
        setSize(900, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(Euchre.handPanel, BorderLayout.SOUTH);
        add(Euchre.scorePanel, BorderLayout.NORTH);
        add(Euchre.trickPanel, BorderLayout.CENTER);
        setVisible(true);

        Euchre.handPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        Euchre.handPanel.setBackground(Color.LIGHT_GRAY);

        remove(Euchre.trickPanel);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        add(Euchre.trickPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}