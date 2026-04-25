package game.ui;

import game.engine.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ponto de entrada do jogo.
 * Rode este main() para jogar.
 */
public class GameWindow extends JPanel {

    private final GameEngine engine;

    public GameWindow() {
        engine = new GameEngine();
        setPreferredSize(new Dimension(GameEngine.SCREEN_W, GameEngine.SCREEN_H));
        setBackground(Color.BLACK);
        setFocusable(true);

        addMouseListener(engine);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                engine.onKeyPressed(e.getKeyCode());
            }
        });

        // Loop 60 FPS
        new Timer(1000 / 60, e -> {
            engine.update();
            repaint();
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        engine.render(g);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Forrest Defend");
            GameWindow panel = new GameWindow();
            frame.add(panel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}