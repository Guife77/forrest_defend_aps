package game.renderer;

import game.defenses.BarrierDefense;
import game.defenses.BirdDefense;
import game.defenses.TreeDefense;
import game.entities.Tower;

import java.awt.*;
import java.util.List;

public class TowerRenderer {

    private static final int R = 13;

    public void render(Graphics2D g, List<Tower> towers, boolean showRanges) {
        // Ranges primeiro (ficam abaixo)
        if (showRanges) {
            for (Tower t : towers) {
                if (!t.isAlive() || t.getRange() <= 0) continue;
                int tx = (int) t.getX(), ty = (int) t.getY();
                int rng = (int) t.getRange();
                g.setColor(new Color(255, 255, 80, 25));
                g.fillOval(tx - rng, ty - rng, rng * 2, rng * 2);
                g.setColor(new Color(255, 255, 80, 100));
                g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1, new float[]{4, 3}, 0));
                g.drawOval(tx - rng, ty - rng, rng * 2, rng * 2);
                g.setStroke(new BasicStroke(1));
            }
        }

        // Torres
        for (Tower t : towers) {
            if (!t.isAlive()) continue;
            int tx = (int) t.getX(), ty = (int) t.getY();

            Color c = color(t);

            if (t instanceof BarrierDefense) {
                // Barreira: quadrado marrom
                g.setColor(c);
                g.fillRect(tx - R, ty - R, R * 2, R * 2);
                g.setColor(c.darker());
                g.setStroke(new BasicStroke(2));
                g.drawRect(tx - R, ty - R, R * 2, R * 2);
            } else {
                // Árvore / Arara: círculo com sombra
                g.setColor(new Color(0, 0, 0, 50));
                g.fillOval(tx - R + 2, ty - R + 2, R * 2, R * 2);
                g.setColor(c);
                g.fillOval(tx - R, ty - R, R * 2, R * 2);
                g.setColor(c.darker());
                g.setStroke(new BasicStroke(2));
                g.drawOval(tx - R, ty - R, R * 2, R * 2);

                // Para BirdDefense, usa o próprio render dela (sprite)
                if (t instanceof BirdDefense) {
                    g.setStroke(new BasicStroke(1));
                    t.render(g); // delega para o render da BirdDefense
                    drawHp(g, t, tx, ty);
                    continue;
                }
            }

            g.setStroke(new BasicStroke(1));
            // Ícone
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString(icon(t), tx - 4, ty + 4);

            drawHp(g, t, tx, ty);
        }
    }

    private Color color(Tower t) {
        if (t instanceof BirdDefense)    return new Color(50, 120, 210);
        if (t instanceof BarrierDefense) return new Color(110, 70, 30);
        return new Color(30, 110, 30); // Tree
    }

    private String icon(Tower t) {
        if (t instanceof BirdDefense)    return "A";
        if (t instanceof BarrierDefense) return "B";
        return "T";
    }

    private void drawHp(Graphics2D g, Tower t, int tx, int ty) {
        int bw = 24, bh = 4;
        int bx = tx - bw / 2, by = ty - R - 9;
        double ratio = Math.max(0, t.getHp() / t.getMaxHp());
        g.setColor(new Color(140, 20, 20));
        g.fillRect(bx, by, bw, bh);
        g.setColor(new Color(50, 210, 60));
        g.fillRect(bx, by, (int)(bw * ratio), bh);
        g.setColor(Color.BLACK);
        g.drawRect(bx, by, bw, bh);
    }
}