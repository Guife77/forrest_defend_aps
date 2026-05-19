package game.renderer;

import game.enemies.Excavator;
import game.enemies.Lumberjack;
import game.enemies.Pollution;
import game.entities.Enemy;

import java.awt.*;
import java.util.List;

public class EnemyRenderer {

    private static final int R = 13; // Raio padrão para os círculos dos inimigos normais

    public void render(Graphics2D g, List<Enemy> enemies) {
        // Ativar suavização para as barras e círculos ficarem perfeitos
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;

            int ex = (int) e.getX();
            int ey = (int) e.getY();

            // 1. Intercepta inimigos com sprite
            if (e instanceof Excavator) {
                g.setStroke(new BasicStroke(1));
                ((Excavator) e).render(g);
                drawHp(g, e, ex, ey);
                continue;
            }
            if (e instanceof Pollution) {
                g.setStroke(new BasicStroke(1));
                ((Pollution) e).render(g);
                drawHp(g, e, ex, ey);
                continue;
            }
            if (e instanceof Lumberjack) {
                g.setStroke(new BasicStroke(1));
                ((Lumberjack) e).render(g);
                drawHp(g, e, ex, ey);
                continue;
            }

            // ── 2. RENDERIZAÇÃO PADRÃO (Para os outros inimigos, ex: Lenhador) ──
            // Sombra projetada sutil
            g.setColor(new Color(0, 0, 0, 45));
            g.fillOval(ex - R + 2, ey - R + 2, R * 2, R * 2);

            // Círculo principal do Inimigo (Vermelho Flat de combate)
            g.setColor(new Color(231, 76, 60)); 
            g.fillOval(ex - R, ey - R, R * 2, R * 2);
            
            // Borda do círculo
            g.setColor(new Color(192, 41, 43));
            g.setStroke(new BasicStroke(2));
            g.drawOval(ex - R, ey - R, R * 2, R * 2);

            // Texto/Ícone interno (Pega dinamicamente a primeira letra do nome do inimigo, ex: "L")
            g.setStroke(new BasicStroke(1));
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            String icon = (e.getName() != null && !e.getName().isEmpty()) 
                          ? e.getName().substring(0, 1).toUpperCase() 
                          : "E";
            g.drawString(icon, ex - 4, ey + 4);

            // Barra de vida para os inimigos normais
            drawHp(g, e, ex, ey);
        }
    }

    // Desenha a barra de HP perfeitamente alinhada acima do inimigo
    private void drawHp(Graphics2D g, Enemy e, int ex, int ey) {
        int bw = 24, bh = 4;
        int bx = ex - bw / 2, by = ey - R - 9;
        
        // Calcula a proporção da vida atual do monstro
        double ratio = Math.max(0, e.getHp() / e.getMaxHp());
        
        // Fundo vermelho escuro (vida perdida)
        g.setColor(new Color(140, 20, 20));
        g.fillRect(bx, by, bw, bh);
        
        // Barra verde (vida restante)
        g.setColor(new Color(50, 210, 60));
        g.fillRect(bx, by, (int)(bw * ratio), bh);
        
        // Borda preta de acabamento
        g.setColor(Color.BLACK);
        g.drawRect(bx, by, bw, bh);
    }
}