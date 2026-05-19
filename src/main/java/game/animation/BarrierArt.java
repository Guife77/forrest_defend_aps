package game.animation;

import java.awt.*;

/**
 * Paliçada de madeira: 4 estacas verticais com pontas afiadas e travessas amarradas.
 * Estado visual reflete o HP (% de vida) — quando dano cresce, a madeira escurece,
 * rachaduras aparecem e as estacas balançam de leve.
 */
public final class BarrierArt {

    private static final Color WOOD_LIGHT   = new Color(155, 110, 60);
    private static final Color WOOD_MID     = new Color(120,  82,  42);
    private static final Color WOOD_DARK    = new Color( 78,  52,  28);
    private static final Color WOOD_BURNT   = new Color( 50,  35,  22);
    private static final Color ROPE         = new Color(180, 150,  85);
    private static final Color CRACK        = new Color( 25,  18,  10);
    private static final Color SHADOW       = new Color(  0,   0,   0, 110);

    private BarrierArt() {}

    /**
     * @param cx, cy centro do sprite
     * @param hpRatio 0..1 — quanto vida resta
     * @param phase tempo em segundos para sway leve
     */
    public static void render(Graphics2D g, int cx, int cy, float hpRatio, float phase) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int w = 28;
        int h = 30;
        int top = cy - h / 2;
        int left = cx - w / 2;

        // Sombra projetada no chão
        g2.setColor(SHADOW);
        g2.fillOval(cx - w / 2, top + h - 4, w, 6);

        // 4 estacas. Cada uma tem sway pequeno fora de fase
        int stakeCount = 4;
        int stakeW = 5;
        int gap = (w - stakeCount * stakeW) / (stakeCount - 1);

        // Cores escurecem conforme HP cai
        Color body = blend(WOOD_MID, WOOD_BURNT, 1f - hpRatio);
        Color edge = blend(WOOD_DARK, WOOD_BURNT, 1f - hpRatio);
        Color tip  = blend(WOOD_LIGHT, WOOD_DARK,  1f - hpRatio);

        for (int i = 0; i < stakeCount; i++) {
            int sway = (int) (Math.sin(phase * 1.5 + i * 0.7) * (1 - hpRatio) * 1.6);
            int sx = left + i * (stakeW + gap) + sway;

            // Estaca (retângulo principal)
            g2.setColor(body);
            g2.fillRect(sx, top + 6, stakeW, h - 8);
            // Sombra lateral
            g2.setColor(edge);
            g2.fillRect(sx + stakeW - 1, top + 6, 1, h - 8);
            g2.fillRect(sx, top + h - 3, stakeW, 1);

            // Ponta afiada (triângulo)
            int[] xs = { sx, sx + stakeW / 2, sx + stakeW };
            int[] ys = { top + 6, top - 2, top + 6 };
            g2.setColor(tip);
            g2.fillPolygon(xs, ys, 3);
            g2.setColor(edge);
            g2.drawPolygon(xs, ys, 3);

            // Rachaduras conforme dano
            if (hpRatio < 0.66f) {
                g2.setColor(CRACK);
                g2.drawLine(sx + 1, top + 9, sx + 2, top + 14);
            }
            if (hpRatio < 0.33f) {
                g2.setColor(CRACK);
                g2.drawLine(sx + stakeW - 2, top + 12, sx + stakeW - 3, top + 20);
                g2.drawLine(sx + 1, top + 18, sx + 2, top + 24);
            }
        }

        // Travessa horizontal (corda + plank)
        int crossY1 = top + 11;
        int crossY2 = top + h - 7;

        g2.setColor(WOOD_DARK);
        g2.fillRect(left - 1, crossY1, w + 2, 2);
        g2.fillRect(left - 1, crossY2, w + 2, 2);

        // Corda enrolada
        g2.setColor(ROPE);
        for (int i = 0; i < stakeCount; i++) {
            int sx = left + i * (stakeW + gap);
            g2.fillRect(sx - 1, crossY1 - 1, stakeW + 2, 1);
            g2.fillRect(sx - 1, crossY2 + 2, stakeW + 2, 1);
        }

        g2.dispose();
    }

    private static Color blend(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed()   * (1 - t) + b.getRed()   * t);
        int g = (int) (a.getGreen() * (1 - t) + b.getGreen() * t);
        int bl= (int) (a.getBlue()  * (1 - t) + b.getBlue()  * t);
        return new Color(r, g, bl);
    }
}
