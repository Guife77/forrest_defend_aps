package game.map;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Renderiza a base do jogador como uma fortaleza de madeira 2D:
 *   • Paliçada de troncos em arco em torno do recinto
 *   • Torre de vigilância central com telhado de palha
 *   • Fogueira animada à frente
 *   • Pequena horta plantada ao lado
 *
 * Sem dependências externas — tudo desenhado com primitivas Java2D.
 */
public final class WoodenFortress {

    private WoodenFortress() {}

    // ── Paleta ─────────────────────────────────────────────
    private static final Color GROUND_DIRT   = new Color(150, 110, 75);
    private static final Color GROUND_RING   = new Color(110,  78,  50);
    private static final Color LOG_BASE      = new Color(125,  82,  45);
    private static final Color LOG_HIGHLIGHT = new Color(170, 120,  75);
    private static final Color LOG_SHADOW    = new Color( 70,  45,  25);
    private static final Color TOWER_WALL    = new Color(140,  92,  55);
    private static final Color TOWER_DARK    = new Color( 95,  60,  35);
    private static final Color WINDOW_GLOW   = new Color(255, 200, 100, 220);
    private static final Color THATCH_BASE   = new Color(190, 152,  78);
    private static final Color THATCH_DARK   = new Color(140, 105,  50);
    private static final Color THATCH_LIGHT  = new Color(230, 200, 120);

    private static final Color BARREL_WOOD   = new Color(120,  78,  40);
    private static final Color BARREL_RIM    = new Color( 70,  45,  20);

    private static final Color GARDEN_SOIL   = new Color( 95,  65,  40);
    private static final Color GARDEN_LEAF   = new Color( 60, 145,  55);
    private static final Color GARDEN_FRUIT  = new Color(220,  60,  50);

    private static final Color FLAME_OUTER   = new Color(255, 110,  30, 220);
    private static final Color FLAME_MID     = new Color(255, 180,  50, 230);
    private static final Color FLAME_CORE    = new Color(255, 230, 140, 240);
    private static final Color EMBER         = new Color(255, 110,  40, 200);
    private static final Color SMOKE         = new Color(120, 120, 120, 50);

    private static final Color SHADOW        = new Color(0, 0, 0, 110);

    /**
     * Desenha a fortaleza centrada em (cx, cy). O `radius` define o "diâmetro"
     * do recinto em pixels (recomendado: tileSize * 4).
     */
    public static void render(Graphics2D g, int cx, int cy, int radius) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        long now = System.currentTimeMillis();

        drawGroundRing(g2, cx, cy, radius);
        drawGarden(g2, cx - radius * 3 / 5, cy + radius / 6);
        drawBarrels(g2, cx + radius * 3 / 5, cy + radius / 3);
        drawCampfire(g2, cx, cy + radius * 2 / 5, radius / 6, now);
        drawPalisade(g2, cx, cy, radius);
        drawWatchtower(g2, cx, cy - radius / 6, radius);

        g2.dispose();
    }

    // ── Chão de terra batida ────────────────────────────────

    private static void drawGroundRing(Graphics2D g, int cx, int cy, int radius) {
        int r = radius;
        g.setColor(SHADOW);
        g.fillOval(cx - r + 4, cy - r / 2 + 8, r * 2, r);

        g.setColor(GROUND_DIRT);
        g.fillOval(cx - r, cy - r * 3 / 5, r * 2, (int)(r * 1.3));
        g.setColor(GROUND_RING);
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(cx - r, cy - r * 3 / 5, r * 2, (int)(r * 1.3));
    }

    // ── Paliçada (troncos em arco) ──────────────────────────

    private static void drawPalisade(Graphics2D g, int cx, int cy, int radius) {
        int logs = 22;
        int logW = 9;
        int logH = (int)(radius * 0.55);

        // Desenha de trás pra frente — troncos do "fundo" primeiro, ocultados pelos da frente
        for (int i = 0; i < logs; i++) {
            double angle = Math.PI + Math.PI * i / (logs - 1.0);
            int x = (int)(cx + Math.cos(angle) * radius * 0.85);
            int y = (int)(cy + Math.sin(angle) * radius * 0.45);
            drawLog(g, x, y, logW, logH, angle);
        }
    }

    private static void drawLog(Graphics2D g, int x, int yTop, int w, int h, double angle) {
        // Sombra
        g.setColor(LOG_SHADOW);
        g.fillRoundRect(x - w / 2 + 2, yTop + 2, w, h, w, w);

        // Corpo
        g.setColor(LOG_BASE);
        g.fillRoundRect(x - w / 2, yTop, w, h, w, w);

        // Realce vertical
        g.setColor(LOG_HIGHLIGHT);
        g.fillRect(x - w / 2 + 2, yTop + 3, 2, h - 6);

        // Topo pontudo (estaca)
        Path2D.Double tip = new Path2D.Double();
        tip.moveTo(x - w / 2.0, yTop);
        tip.lineTo(x, yTop - w / 2.0);
        tip.lineTo(x + w / 2.0, yTop);
        tip.closePath();
        g.setColor(LOG_BASE);
        g.fill(tip);
        g.setColor(LOG_SHADOW);
        g.draw(tip);
    }

    // ── Torre de vigilância ─────────────────────────────────

    private static void drawWatchtower(Graphics2D g, int cx, int cy, int radius) {
        int towerW = radius * 4 / 5;
        int towerH = radius * 7 / 8;
        int x = cx - towerW / 2;
        int y = cy - towerH / 2;

        // Sombra projetada
        g.setColor(SHADOW);
        g.fillOval(cx - towerW * 2 / 5, y + towerH - 4, towerW * 4 / 5, towerH / 6);

        // Pilar / parede
        g.setColor(TOWER_WALL);
        g.fillRoundRect(x, y, towerW, towerH, 8, 8);

        // Tábuas verticais (textura)
        g.setColor(TOWER_DARK);
        g.setStroke(new BasicStroke(1.4f));
        int planks = 5;
        for (int i = 1; i < planks; i++) {
            int px = x + i * towerW / planks;
            g.drawLine(px, y + 4, px, y + towerH - 4);
        }

        // Sombra interna na parte inferior
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRect(x, y + towerH - 8, towerW, 8);

        // Janela com brilho dourado (lampião)
        int winW = towerW / 3;
        int winH = towerH / 3;
        int wx = cx - winW / 2;
        int wy = y + towerH / 4;
        g.setColor(TOWER_DARK);
        g.fillRoundRect(wx - 2, wy - 2, winW + 4, winH + 4, 6, 6);
        g.setColor(WINDOW_GLOW);
        g.fillRoundRect(wx, wy, winW, winH, 4, 4);
        // Cruzeta da janela
        g.setColor(TOWER_DARK);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(wx + winW / 2, wy, wx + winW / 2, wy + winH);
        g.drawLine(wx, wy + winH / 2, wx + winW, wy + winH / 2);

        // Telhado de palha
        drawThatchRoof(g, cx, y, towerW + 12, towerH * 2 / 5);

        g.setStroke(new BasicStroke(1));
    }

    private static void drawThatchRoof(Graphics2D g, int cx, int baseY, int roofW, int roofH) {
        int x1 = cx - roofW / 2;
        int x2 = cx + roofW / 2;
        int peakY = baseY - roofH;

        Path2D.Double roof = new Path2D.Double();
        roof.moveTo(x1, baseY);
        roof.lineTo(cx, peakY);
        roof.lineTo(x2, baseY);
        roof.closePath();

        // Sombra interna
        g.setColor(THATCH_DARK);
        g.fill(roof);

        // Camadas de palha em faixas horizontais
        for (int i = 0; i < 4; i++) {
            float t1 = i / 4f;
            float t2 = (i + 1) / 4f;
            int yA = (int)(peakY + (baseY - peakY) * t1);
            int yB = (int)(peakY + (baseY - peakY) * t2);
            int xA1 = (int)(cx + (x1 - cx) * t1);
            int xA2 = (int)(cx + (x2 - cx) * t1);
            int xB1 = (int)(cx + (x1 - cx) * t2);
            int xB2 = (int)(cx + (x2 - cx) * t2);

            Path2D.Double band = new Path2D.Double();
            band.moveTo(xA1, yA);
            band.lineTo(xA2, yA);
            band.lineTo(xB2, yB);
            band.lineTo(xB1, yB);
            band.closePath();
            g.setColor(i % 2 == 0 ? THATCH_BASE : THATCH_DARK);
            g.fill(band);
        }

        // Realce no topo
        g.setColor(THATCH_LIGHT);
        g.setStroke(new BasicStroke(2.2f));
        g.drawLine(cx - 4, peakY + 4, cx + 4, peakY + 4);

        // Borda
        g.setColor(THATCH_DARK);
        g.setStroke(new BasicStroke(2f));
        g.draw(roof);
        g.setStroke(new BasicStroke(1));
    }

    // ── Fogueira animada ────────────────────────────────────

    private static void drawCampfire(Graphics2D g, int cx, int cy, int size, long now) {
        // Pedras ao redor
        g.setColor(new Color(110, 110, 110));
        for (int i = 0; i < 6; i++) {
            double a = Math.PI * 2 * i / 6;
            int sx = (int)(cx + Math.cos(a) * size);
            int sy = (int)(cy + Math.sin(a) * size / 2);
            g.fillOval(sx - 4, sy - 3, 8, 6);
            g.setColor(new Color(70, 70, 70));
            g.drawOval(sx - 4, sy - 3, 8, 6);
            g.setColor(new Color(110, 110, 110));
        }

        // Lenha
        g.setColor(BARREL_WOOD);
        g.fillRoundRect(cx - size, cy - 2, size * 2, 4, 3, 3);
        g.setColor(LOG_SHADOW);
        g.drawRoundRect(cx - size, cy - 2, size * 2, 4, 3, 3);

        // Chamas oscilantes (3 camadas)
        float t = now * 0.006f;
        int flameH = (int)(size * 1.8 + Math.sin(t) * 2);
        int flameW = (int)(size * 1.2);

        g.setColor(FLAME_OUTER);
        fillFlame(g, cx, cy - 2, flameW, flameH, t);

        g.setColor(FLAME_MID);
        fillFlame(g, cx, cy - 2, (int)(flameW * 0.65), (int)(flameH * 0.75), t * 1.3f);

        g.setColor(FLAME_CORE);
        fillFlame(g, cx, cy - 2, (int)(flameW * 0.35), (int)(flameH * 0.45), t * 1.6f);

        // Fumaça subindo (3 puffs)
        for (int i = 0; i < 3; i++) {
            float phase = t + i * 1.7f;
            int sx = cx + (int)(Math.sin(phase * 0.7) * 5);
            int sy = cy - flameH - 6 - (int)(((phase * 12) % 30));
            int sr = 6 + i * 2;
            g.setColor(SMOKE);
            g.fillOval(sx - sr, sy - sr, sr * 2, sr * 2);
        }

        // Brilho/luz no chão ao redor
        java.awt.RadialGradientPaint glow = new java.awt.RadialGradientPaint(
                cx, cy, size * 3,
                new float[]{0f, 1f},
                new Color[]{new Color(255, 180, 60, 60), new Color(255, 180, 60, 0)}
        );
        g.setPaint(glow);
        g.fillOval(cx - size * 3, cy - size * 3, size * 6, size * 6);

        // Brasas piscando
        for (int i = 0; i < 4; i++) {
            float phase = t + i * 0.9f;
            int ex = cx + (int)(Math.sin(phase) * size);
            int ey = cy + (int)(Math.cos(phase * 1.4) * size / 2);
            int alpha = (int)(100 + 100 * Math.abs(Math.sin(phase * 2)));
            g.setColor(new Color(EMBER.getRed(), EMBER.getGreen(), EMBER.getBlue(), Math.min(255, alpha)));
            g.fillOval(ex - 1, ey - 1, 2, 2);
        }
    }

    private static void fillFlame(Graphics2D g, int cx, int baseY, int w, int h, float phase) {
        Path2D.Double flame = new Path2D.Double();
        int curl = (int)(Math.sin(phase) * w * 0.15);
        flame.moveTo(cx - w / 2.0, baseY);
        flame.curveTo(cx - w / 2.0, baseY - h * 0.6,
                      cx - w / 4.0 + curl, baseY - h * 0.8,
                      cx, baseY - h);
        flame.curveTo(cx + w / 4.0 + curl, baseY - h * 0.8,
                      cx + w / 2.0, baseY - h * 0.6,
                      cx + w / 2.0, baseY);
        flame.closePath();
        g.fill(flame);
    }

    // ── Barris ─────────────────────────────────────────────

    private static void drawBarrels(Graphics2D g, int cx, int cy) {
        drawBarrel(g, cx, cy, 12, 16);
        drawBarrel(g, cx - 10, cy + 3, 10, 14);
    }

    private static void drawBarrel(Graphics2D g, int cx, int cy, int w, int h) {
        g.setColor(SHADOW);
        g.fillOval(cx - w / 2 + 1, cy + h / 2, w, h / 4);

        g.setColor(BARREL_WOOD);
        g.fillRoundRect(cx - w / 2, cy - h / 2, w, h, 4, 4);
        g.setColor(BARREL_RIM);
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(cx - w / 2, cy - h / 3, cx + w / 2, cy - h / 3);
        g.drawLine(cx - w / 2, cy + h / 3, cx + w / 2, cy + h / 3);
        g.drawRoundRect(cx - w / 2, cy - h / 2, w, h, 4, 4);
        g.setStroke(new BasicStroke(1));
    }

    // ── Horta plantada ─────────────────────────────────────

    private static void drawGarden(Graphics2D g, int cx, int cy) {
        int w = 26, h = 14;
        g.setColor(GARDEN_SOIL);
        g.fillRoundRect(cx - w / 2, cy - h / 2, w, h, 3, 3);
        g.setColor(new Color(60, 40, 25));
        g.drawRoundRect(cx - w / 2, cy - h / 2, w, h, 3, 3);

        // Mudas em fileiras
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                int px = cx - w / 2 + 4 + col * 6;
                int py = cy - h / 2 + 4 + row * 5;
                g.setColor(GARDEN_LEAF);
                g.fillOval(px - 1, py - 1, 4, 4);
                if ((row + col) % 3 == 0) {
                    g.setColor(GARDEN_FRUIT);
                    g.fillOval(px, py, 2, 2);
                }
            }
        }
    }
}
