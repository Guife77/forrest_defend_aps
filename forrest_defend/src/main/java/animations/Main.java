package animations;

import javax.swing.*;
import java.awt.*;

public class Main extends JPanel {
    private animation_arara anim;

    public Main() {
        // Carrega a animação da arara usando o caminho correto
        // "public/arara.png" assume que você está rodando o programa da raiz da pasta 'forrest_defend'
        anim = new animation_arara("public/arara.png");

        // Timer para atualizar e repintar o painel a cada ~16ms (60 FPS)
        Timer timer = new Timer(16, e -> {
            anim.update();
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Desenha a animação centralizada horizontalmente e bem no topo (y=10)
        if (anim != null) {
            // Centro exato: (Janela / 2) - (Largura da Arara / 2)
            int x = (getWidth() - anim.getFrameWidth()) / 2;
            int y = 10; // Quase encostado no topo
            anim.render(g, x, y);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Animação da Arara");
        Main mainPanel = new Main();

        frame.add(mainPanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
