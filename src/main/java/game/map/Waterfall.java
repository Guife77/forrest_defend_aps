package game.map;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Random;

/**
 * Cachoeira decorativa — desenhada inteiramente em código.
 *
 * Visual reformulado:
 *   • Penhasco rochoso com topo irregular (não mais retangular)
 *   • Lâmina de água (sheet) em trapézio que alarga ao cair
 *   • Listras finas e curvadas de fluxo animadas internamente
 *   • Espuma no topo + nuvem de splash arredondada no impacto
 *   • Poça com ondas concêntricas + pedras envolvendo
 */
public final class Waterfall {

    // ── Paleta ──────────────────────────────────────────────
    private static final Color ROCK_DARK     = new Color( 78,  73,  68);
    private static final Color ROCK_MID      = new Color(120, 114, 102);
    private static final Color ROCK_LIGHT    = new Color(168, 160, 142);
    private static final Color ROCK_OUTLINE  = new Color( 45,  40,  35);
    private static final Color MOSS          = new Color( 70, 130,  55);

    private static final Color WATER_DEEP    = new Color( 55, 130, 195);
    private static final Color WATER_MID     = new Color(115, 195, 240);
    private static final Color WATER_TOP     = new Color(170, 220, 250);
    private static final Color FOAM          = new Color(255, 255, 255, 230);
    private static final Color FOAM_SOFT     = new Color(255, 255, 255, 100);
    private static final Color FLOW_LINE     = new Color(255, 255, 255, 180);

    private static final Color POOL_BORDER   = new Color( 30,  80, 125);

    private final int cx, cyTop;
    private final int width, height;
    private final int cliffH;
    private final int fallTop;
    private final int fallBottom;
    private final int poolCenterY;
    private final int poolW, poolH;
    private final int topW, bottomW;

    private final int[] cliffPeaks;
    private final int[] flowSpeedOffsets;
    private final float[] flowSpeedMults;
    private final int[] flowXOffsets;

    private final int[] stoneOffsetX;
    private final int[] stoneOffsetY;
    private final int[] stoneSize;
    private final int[] stoneTone;

    public Waterfall(int cx, int cyTop, int width, int height) {
        this.cx = cx;
        this.cyTop = cyTop;
        this.width = width;
        this.height = height;
        this.cliffH = height / 5;
        this.fallTop = cyTop + cliffH;
        this.fallBottom = cyTop + height * 4 / 5;
        this.poolCenterY = cyTop + height * 9 / 10;
        this.poolW = (int) (width * 1.4);
        this.poolH = height / 4;

        // Largura da queda: estreita no topo, mais larga no fundo (fluxo natural)
        this.topW = (int) (width * 0.55);
        this.bottomW = (int) (width * 0.80);

        Random rng = new Random(73);

        // Picos irregulares do penhasco (topo). 7 pontos de altura aleatória.
        cliffPeaks = new int[7];
        for (int i = 0; i < cliffPeaks.length; i++) {
            cliffPeaks[i] = 4 + rng.nextInt(10);
        }

        // 4 listras de fluxo interno
        int streams = 4;
        flowSpeedOffsets = new int[streams];
        flowSpeedMults = new float[streams];
        flowXOffsets = new int[streams];
        for (int i = 0; i < streams; i++) {
            flowXOffsets[i] = -topW / 3 + (topW * 2 / 3 * i) / (streams - 1);
            flowSpeedOffsets[i] = rng.nextInt(40);
            flowSpeedMults[i] = 0.85f + rng.nextFloat() * 0.55f;
        }

        // Pedras ao redor da poça (arco frontal)
        int n = 11;
        stoneOffsetX = new int[n];
        stoneOffsetY = new int[n];
        stoneSize = new int[n];
        stoneTone = new int[n];
        for (int i = 0; i < n; i++) {
            double a = Math.PI + Math.PI * i / (n - 1.0);
            double r = 0.88 + rng.nextDouble() * 0.22;
            stoneOffsetX[i] = (int) (Math.cos(a) * poolW / 2 * r);
            stoneOffsetY[i] = (int) (Math.sin(a) * poolH / 2 * r) + poolH / 8;
            stoneSize[i] = 6 + rng.nextInt(5);
            stoneTone[i] = rng.nextInt(3);
        }
    }

    public void render(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        long now = System.currentTimeMillis();
        float t = now * 0.001f;

        drawPool(g2, t);
        drawSurroundingStones(g2);
        drawWaterRibbon(g2, t);
        drawSplash(g2, t);
        drawCliff(g2);
        drawMist(g2, t);

        g2.dispose();
    }

    // ── Penhasco rochoso (com topo irregular) ──────────────

    private void drawCliff(Graphics2D g) {
        int cliffW = (int)(width * 1.05);
        int left = cx - cliffW / 2;
        int right = cx + cliffW / 2;
        int top = cyTop;
        int bottom = fallTop;

        // Constrói o polígono da rocha: lateral direita, fundo curvado, lateral esquerda, topo irregular
        Path2D.Double rock = new Path2D.Double();
        rock.moveTo(left, top);
        // Topo serrilhado (sobe e desce)
        int steps = cliffPeaks.length;
        for (int i = 0; i < steps; i++) {
            double x = left + (right - left) * (i + 1.0) / (steps + 1);
            double y = top - cliffPeaks[i];
            rock.lineTo(x, y);
        }
        rock.lineTo(right, top);
        // Lateral direita
        rock.lineTo(right - 3, bottom - 4);
        // Base com leve curva (saída de água)
        rock.curveTo(cx + bottomW / 4.0, bottom + 4,
                     cx - bottomW / 4.0, bottom + 4,
                     left + 3, bottom - 4);
        rock.closePath();

        // Sombra projetada
        g.setColor(new Color(0, 0, 0, 70));
        Path2D.Double shadow = (Path2D.Double) rock.clone();
        shadow.transform(java.awt.geom.AffineTransform.getTranslateInstance(3, 5));
        g.fill(shadow);

        // Corpo da rocha
        g.setColor(ROCK_MID);
        g.fill(rock);

        // Sombras horizontais (estratos)
        g.setColor(ROCK_DARK);
        for (int i = 0; i < 3; i++) {
            int y = top + cliffH * (i + 1) / 4;
            g.fillRect(left + 5, y, cliffW - 10, 2);
        }

        // Realces no topo dos picos
        g.setColor(ROCK_LIGHT);
        for (int i = 0; i < steps; i++) {
            int x = (int)(left + (right - left) * (i + 1.0) / (steps + 1));
            int y = top - cliffPeaks[i];
            g.fillOval(x - 3, y - 1, 5, 3);
        }

        // Borda escura
        g.setColor(ROCK_OUTLINE);
        g.setStroke(new BasicStroke(1.6f));
        g.draw(rock);
        g.setStroke(new BasicStroke(1));

        // Musgo nos picos
        g.setColor(MOSS);
        for (int i = 1; i < steps; i += 2) {
            int x = (int)(left + (right - left) * (i + 1.0) / (steps + 1));
            int y = top - cliffPeaks[i];
            g.fillOval(x - 4, y - 2, 8, 3);
        }
    }

    // ── Lâmina de água ──────────────────────────────────────

    private void drawWaterRibbon(Graphics2D g, float t) {
        // Trapézio que alarga descendo
        int topLeft  = cx - topW / 2;
        int topRight = cx + topW / 2;
        int botLeft  = cx - bottomW / 2;
        int botRight = cx + bottomW / 2;

        Path2D.Double ribbon = new Path2D.Double();
        ribbon.moveTo(topLeft, fallTop);
        ribbon.lineTo(topRight, fallTop);
        ribbon.lineTo(botRight, fallBottom);
        ribbon.lineTo(botLeft, fallBottom);
        ribbon.closePath();

        // Camada profunda (azul escuro)
        g.setColor(WATER_DEEP);
        g.fill(ribbon);

        // Camada média (azul claro), pouco mais estreita
        Path2D.Double inner = new Path2D.Double();
        inner.moveTo(topLeft + 2, fallTop);
        inner.lineTo(topRight - 2, fallTop);
        inner.lineTo(botRight - 3, fallBottom);
        inner.lineTo(botLeft + 3, fallBottom);
        inner.closePath();
        g.setColor(WATER_MID);
        g.fill(inner);

        // Listras de fluxo (linhas finas curvas animadas descendo)
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < flowXOffsets.length; i++) {
            float speed = flowSpeedMults[i] * 220f;
            int offsetY = (int)((t * speed + flowSpeedOffsets[i] * 10) % 40);
            int x = cx + flowXOffsets[i];
            for (int y = fallTop - 20; y < fallBottom; y += 40) {
                int y1 = y + offsetY;
                int y2 = y1 + 18;
                if (y2 < fallTop || y1 > fallBottom) continue;
                int cy1 = Math.max(fallTop, y1);
                int cy2 = Math.min(fallBottom, y2);
                // Alarga horizontalmente o X conforme desce (acompanha o trapézio)
                float prog1 = (cy1 - fallTop) / (float)(fallBottom - fallTop);
                float prog2 = (cy2 - fallTop) / (float)(fallBottom - fallTop);
                int x1 = (int)(x * (1 - prog1) + (cx + flowXOffsets[i] * (float)bottomW / topW) * prog1);
                int x2 = (int)(x * (1 - prog2) + (cx + flowXOffsets[i] * (float)bottomW / topW) * prog2);
                g.setColor(FLOW_LINE);
                g.drawLine(x1, cy1, x2, cy2);
            }
        }
        g.setStroke(new BasicStroke(1));

        // Brilho central — listra mais clara no meio
        Path2D.Double shine = new Path2D.Double();
        shine.moveTo(cx - topW / 6, fallTop);
        shine.lineTo(cx + topW / 6, fallTop);
        shine.lineTo(cx + bottomW / 8, fallBottom);
        shine.lineTo(cx - bottomW / 8, fallBottom);
        shine.closePath();
        g.setColor(new Color(WATER_TOP.getRed(), WATER_TOP.getGreen(), WATER_TOP.getBlue(), 90));
        g.fill(shine);

        // Espuma branca colada na borda de saída do penhasco
        g.setColor(FOAM);
        g.fillRoundRect(topLeft - 3, fallTop - 4, topW + 6, 7, 7, 7);
        g.setColor(FOAM_SOFT);
        g.fillOval(topLeft - 6, fallTop - 6, 14, 10);
        g.fillOval(topRight - 8, fallTop - 6, 14, 10);
    }

    // ── Splash na entrada da poça ───────────────────────────

    private void drawSplash(Graphics2D g, float t) {
        int sw = (int)(bottomW * 1.3);
        int sy = fallBottom;

        // Cobertura grande de espuma
        g.setColor(FOAM_SOFT);
        g.fillOval(cx - sw / 2, sy - 14, sw, 22);

        // Espuma densa próxima do impacto
        g.setColor(FOAM);
        g.fillOval(cx - sw / 3, sy - 7, sw * 2 / 3, 14);
        g.fillOval(cx - bottomW / 2, sy - 4, bottomW, 8);

        // Gotas saltando ao redor
        for (int i = 0; i < 5; i++) {
            float phase = t * 4 + i * 1.3f;
            float lift = (float) Math.abs(Math.sin(phase));
            int dx = (int)(Math.cos(phase * 0.7) * bottomW / 2);
            int dy = (int)(-lift * 14);
            int alpha = (int)(220 - lift * 180);
            int sz = 3 + (i % 2);
            g.setColor(new Color(255, 255, 255, Math.max(0, alpha)));
            g.fillOval(cx + dx - sz / 2, sy + dy - sz / 2, sz, sz);
        }
    }

    // ── Poça com ondas ──────────────────────────────────────

    private void drawPool(Graphics2D g, float t) {
        // Sombra
        g.setColor(new Color(0, 0, 0, 75));
        g.fillOval(cx - poolW / 2 + 3, poolCenterY - poolH / 2 + 4, poolW, poolH);

        // Água profunda
        g.setColor(WATER_DEEP);
        g.fillOval(cx - poolW / 2, poolCenterY - poolH / 2, poolW, poolH);

        // Brilho médio
        g.setColor(WATER_MID);
        g.fillOval(cx - poolW / 3, poolCenterY - poolH / 3, poolW * 2 / 3, poolH * 2 / 3);

        // Highlight superior
        g.setColor(WATER_TOP);
        g.fillOval(cx - poolW / 5, poolCenterY - poolH / 3, poolW * 2 / 5, poolH / 4);

        // Ondas concêntricas
        g.setStroke(new BasicStroke(1.3f));
        for (int i = 0; i < 3; i++) {
            float phase = ((t * 0.5f + i * 0.34f) % 1f);
            int rw = (int)(poolW * phase);
            int rh = (int)(poolH * phase);
            int alpha = (int)(180 * (1 - phase));
            g.setColor(new Color(255, 255, 255, Math.max(0, alpha)));
            g.drawOval(cx - rw / 2, poolCenterY - rh / 2, rw, rh);
        }

        // Borda da poça
        g.setColor(POOL_BORDER);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(cx - poolW / 2, poolCenterY - poolH / 2, poolW, poolH);
        g.setStroke(new BasicStroke(1));
    }

    // ── Pedras envolvendo ──────────────────────────────────

    private void drawSurroundingStones(Graphics2D g) {
        Color[] palette = { ROCK_LIGHT, ROCK_MID, ROCK_DARK };
        for (int i = 0; i < stoneOffsetX.length; i++) {
            int sx = cx + stoneOffsetX[i];
            int sy = poolCenterY + stoneOffsetY[i];
            int sz = stoneSize[i];
            g.setColor(new Color(0, 0, 0, 85));
            g.fillOval(sx - sz + 1, sy - sz / 2 + 1, sz * 2, sz);
            g.setColor(palette[stoneTone[i]]);
            g.fillOval(sx - sz, sy - sz / 2, sz * 2, sz);
            g.setColor(new Color(255, 255, 255, 70));
            g.fillOval(sx - sz + 1, sy - sz / 2, sz - 1, sz / 2);
        }
    }

    // ── Névoa ──────────────────────────────────────────────

    private void drawMist(Graphics2D g, float t) {
        for (int i = 0; i < 4; i++) {
            float phase = t + i * 1.4f;
            int mx = cx + (int)(Math.sin(phase * 0.6) * width / 4);
            int my = fallBottom + (int)(Math.cos(phase * 0.8) * 5) - 10;
            int mr = 12 + i * 3;
            int alpha = (int)(45 + Math.abs(Math.sin(phase)) * 35);
            g.setColor(new Color(255, 255, 255, alpha));
            g.fillOval(mx - mr, my - mr / 2, mr * 2, mr);
        }
    }
}
