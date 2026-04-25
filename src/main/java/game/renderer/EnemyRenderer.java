package game.renderer;

import game.enemies.Excavator;
import game.enemies.Lumberjack;
import game.enemies.Pollution;
import game.entities.Enemy;

import java.awt.*;
import java.util.List;

public class EnemyRenderer {

    private static final int R = 10; // raio

    public void render(Graphics2D g, List<Enemy> enemies) {
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            int ex = (int) e.getX();
            int ey = (int) e.getY();

            Color body = bodyColor(e);
            // Sombra
            g.setColor(new Color(0, 0, 0, 60));
            g.fillOval(ex - R + 2, ey - R + 2, R * 2, R * 2);
            // Corpo
            g.setColor(body);
            g.fillOval(ex - R, ey - R, R * 2, R * 2);
            g.setColor(body.darker());
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(ex - R, ey - R, R * 2, R * 2);
            g.setStroke(new BasicStroke(1));
            // Letra
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString(icon(e), ex - 4, ey + 4);
            // HP bar
            drawHp(g, e, ex, ey);
        }
    }

    private Color bodyColor(Enemy e) {
        if (e instanceof Excavator) return new Color(180, 110, 20);
        if (e instanceof Pollution) return new Color(80,  170, 50);
        return new Color(210, 50, 50); // Lumberjack
    }

    private String icon(Enemy e) {
        if (e instanceof Excavator) return "E";
        if (e instanceof Pollution) return "P";
        return "L";
    }

    private void drawHp(Graphics2D g, Enemy e, int ex, int ey) {
        int bw = 22, bh = 4;
        int bx = ex - bw / 2, by = ey - R - 8;
        double ratio = Math.max(0, e.getHp() / e.getMaxHp());
        g.setColor(new Color(140, 20, 20));
        g.fillRect(bx, by, bw, bh);
        g.setColor(new Color(50, 210, 60));
        g.fillRect(bx, by, (int)(bw * ratio), bh);
        g.setColor(Color.BLACK);
        g.drawRect(bx, by, bw, bh);
    }
}