package game.ui;

import java.awt.*;

public class Menu {

    public void render(Graphics2D g, int w, int h) {
        // Fundo verde escuro (tema Amazónia)
        g.setColor(new Color(15, 40, 20));
        g.fillRect(0, 0, w, h);

        // Título Principal
        g.setFont(new Font("Arial", Font.BOLD, 56));
        g.setColor(new Color(80, 255, 120));
        drawCentered(g, "FORREST DEFEND", w, h / 2 - 50);

        // Instrução para começar
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.setColor(Color.WHITE);
        drawCentered(g, "Pressione [ENTER] para proteger a floresta", w, h / 2 + 30);

        // Créditos do projeto
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(new Color(120, 150, 120));
        drawCentered(g, "Projeto APS", w, h - 30);
    }

    // Método auxiliar para centralizar o texto no ecrã perfeitamente
    private void drawCentered(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}