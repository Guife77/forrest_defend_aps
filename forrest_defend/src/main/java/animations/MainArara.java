package animations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainArara extends JPanel {
    private animation_arara arara;
    private Escavadeira escavadeira;
    private Macaco macaco;

    // Controle de exibição: 0=Tudo, 1=Arara, 2=Escavadeira, 3=Macaco
    private int viewMode = 0;

    public MainArara() {
        // Carrega frames individuais: arara1.png, arara2.png, arara3.png, arara4.png
        arara = new animation_arara("arara", 4);
        // Carrega frames individuais: escavadeira1.png ... escavadeira4.png
        escavadeira = new Escavadeira("escavadeira", 4);
        // Macaco não possui assets ainda; carrega sem exibir nada
        macaco = new Macaco("macaco", 4);

        // Timer de 60 FPS
        Timer timer = new Timer(16, e -> {
            update();
            repaint();
        });
        timer.start();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_1) viewMode = 1;
                if (e.getKeyCode() == KeyEvent.VK_2) viewMode = 2;
                if (e.getKeyCode() == KeyEvent.VK_3) viewMode = 3;
                if (e.getKeyCode() == KeyEvent.VK_0) viewMode = 0;
            }
        });
    }

    private void update() {
        if (viewMode == 0 || viewMode == 1) if (arara != null) arara.update();
        if (viewMode == 0 || viewMode == 2) if (escavadeira != null) escavadeira.update();
        if (viewMode == 0 || viewMode == 3) if (macaco != null) macaco.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundo Verde Floresta
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Instruções
        g2d.setColor(Color.WHITE);
        g2d.drawString("Teclas: [0] Todos | [1] Arara | [2] Escavadeira | [3] Macaco", 10, 20);

        int centerX = getWidth() / 2;

        // Renderização
        if (viewMode == 0 || viewMode == 1) {
            if (arara != null) arara.render(g2d, centerX - 50, 50);
        }

        if (viewMode == 0 || viewMode == 2) {
            if (escavadeira != null) escavadeira.render(g2d, centerX - 100, 200);
        }

        if (viewMode == 0 || viewMode == 3) {
            if (macaco != null) {
                macaco.render(g2d, centerX - (macaco.getFrameWidth() / 2), 400);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Forrest Defend - Visualizador de Sprites");
        MainArara gamePanel = new MainArara();
        frame.add(gamePanel);
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}