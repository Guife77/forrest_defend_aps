package game.ui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Menu {

    private final List<Firefly> fireflies = new ArrayList<>();
    private final List<Leaf> leaves = new ArrayList<>();
    private final Random rng = new Random(42);
    private long startTime = System.currentTimeMillis();
    private long lastFrameTime = System.currentTimeMillis();
    private boolean initialized = false;

    public void render(Graphics2D g, int w, int h) {
        if (!initialized) {
            spawnParticles(w, h);
            initialized = true;
        }

        long now = System.currentTimeMillis();
        float dt = Math.min(0.05f, (now - lastFrameTime) / 1000f);
        lastFrameTime = now;
        float t = (now - startTime) / 1000f;

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g, w, h, t);
        drawDistantTrees(g, w, h);
        updateAndDrawFireflies(g, w, h, dt);
        drawForegroundTrees(g, w, h);
        updateAndDrawLeaves(g, w, h, dt);
        drawTitle(g, w, h, t);
        drawEnterPrompt(g, w, h, t);
        drawCommandsCard(g, w, h);
        drawFooter(g, w, h);
        drawVignette(g, w, h);
    }

    private void spawnParticles(int w, int h) {
        for (int i = 0; i < 45; i++) {
            fireflies.add(new Firefly(
                    rng.nextFloat() * w,
                    rng.nextFloat() * h,
                    (rng.nextFloat() - 0.5f) * 12f,
                    -8f - rng.nextFloat() * 14f,
                    1.2f + rng.nextFloat() * 2.6f,
                    rng.nextFloat() * (float) Math.PI * 2f
            ));
        }
        for (int i = 0; i < 14; i++) {
            leaves.add(new Leaf(
                    rng.nextFloat() * w,
                    rng.nextFloat() * h - h,
                    18f + rng.nextFloat() * 22f,
                    rng.nextFloat() * (float) Math.PI * 2f,
                    (rng.nextFloat() - 0.5f) * 1.4f,
                    8f + rng.nextFloat() * 6f
            ));
        }
    }

    private void drawBackground(Graphics2D g, int w, int h, float t) {
        GradientPaint jungle = new GradientPaint(
                0, 0, new Color(8, 28, 16),
                0, h, new Color(2, 6, 4)
        );
        g.setPaint(jungle);
        g.fillRect(0, 0, w, h);

        float pulse = 0.5f + 0.5f * (float) Math.sin(t * 0.6);
        int glowAlpha = (int) (30 + 25 * pulse);
        RadialGradientPaint moonGlow = new RadialGradientPaint(
                w / 2f, h * 0.35f, w * 0.55f,
                new float[]{0f, 1f},
                new Color[]{new Color(80, 180, 120, glowAlpha), new Color(0, 0, 0, 0)}
        );
        g.setPaint(moonGlow);
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(40, 65, 48, 100));
        g.setStroke(new BasicStroke(4));
        g.drawRect(4, 4, w - 8, h - 8);
        g.setStroke(new BasicStroke(1));
    }

    private void drawDistantTrees(Graphics2D g, int w, int h) {
        g.setColor(new Color(12, 30, 20, 200));
        int baseY = h - 90;
        int treeCount = w / 28 + 2;
        for (int i = 0; i < treeCount; i++) {
            int cx = i * 28 - 10;
            int height = 60 + (int) (Math.sin(i * 1.3) * 18) + (i % 3) * 8;
            Polygon p = new Polygon(
                    new int[]{cx - 14, cx, cx + 14},
                    new int[]{baseY, baseY - height, baseY},
                    3
            );
            g.fillPolygon(p);
        }
    }

    private void drawForegroundTrees(Graphics2D g, int w, int h) {
        g.setColor(new Color(4, 14, 8, 245));
        int baseY = h - 20;
        int treeCount = w / 50 + 2;
        for (int i = 0; i < treeCount; i++) {
            int cx = i * 50 - 20;
            int height = 100 + (int) (Math.sin(i * 0.9 + 1.5) * 28) + (i % 2) * 15;
            int trunkW = 8;
            g.fillRect(cx - trunkW / 2, baseY - 30, trunkW, 35);
            Polygon crown = new Polygon(
                    new int[]{cx - 26, cx, cx + 26},
                    new int[]{baseY - 10, baseY - height, baseY - 10},
                    3
            );
            g.fillPolygon(crown);
            Polygon crown2 = new Polygon(
                    new int[]{cx - 20, cx, cx + 20},
                    new int[]{baseY - 30, baseY - height + 18, baseY - 30},
                    3
            );
            g.fillPolygon(crown2);
        }
    }

    private void updateAndDrawFireflies(Graphics2D g, int w, int h, float dt) {
        for (Firefly f : fireflies) {
            f.phase += dt * 2.4f;
            f.x += f.vx * dt + (float) Math.sin(f.phase) * 6f * dt;
            f.y += f.vy * dt;
            if (f.y < -10) {
                f.y = h + 10;
                f.x = rng.nextFloat() * w;
            }
            if (f.x < -10) f.x = w + 10;
            if (f.x > w + 10) f.x = -10;

            float glow = 0.5f + 0.5f * (float) Math.sin(f.phase * 1.3f);
            int alpha = (int) (90 + 140 * glow);
            float r = f.radius;
            RadialGradientPaint halo = new RadialGradientPaint(
                    f.x, f.y, r * 4f,
                    new float[]{0f, 1f},
                    new Color[]{
                            new Color(180, 255, 150, alpha / 3),
                            new Color(180, 255, 150, 0)
                    }
            );
            g.setPaint(halo);
            g.fillOval((int) (f.x - r * 4), (int) (f.y - r * 4), (int) (r * 8), (int) (r * 8));

            g.setColor(new Color(220, 255, 180, alpha));
            g.fillOval((int) (f.x - r), (int) (f.y - r), (int) (r * 2), (int) (r * 2));
        }
    }

    private void updateAndDrawLeaves(Graphics2D g, int w, int h, float dt) {
        AffineTransform old = g.getTransform();
        for (Leaf l : leaves) {
            l.y += l.fallSpeed * dt;
            l.x += (float) Math.sin(l.y * 0.02f + l.swayPhase) * l.swayAmp * dt * 6f;
            l.rotation += l.spin * dt;
            if (l.y > h + 30) {
                l.y = -30;
                l.x = rng.nextFloat() * w;
            }

            g.setTransform(old);
            g.translate(l.x, l.y);
            g.rotate(l.rotation);
            g.setColor(new Color(70, 130, 70, 200));
            g.fillOval(-6, -3, 12, 6);
            g.setColor(new Color(40, 90, 45, 220));
            g.drawLine(-6, 0, 6, 0);
        }
        g.setTransform(old);
    }

    private void drawTitle(Graphics2D g, int w, int h, float t) {
        String title = "FORREST DEFEND";
        float breathe = 1f + 0.015f * (float) Math.sin(t * 1.8);
        int titleY = h / 2 - 80;

        AffineTransform old = g.getTransform();
        g.translate(w / 2f, titleY);
        g.scale(breathe, breathe);
        g.translate(-w / 2f, -titleY);

        g.setFont(new Font("Arial", Font.BOLD, 64));
        FontMetrics fm = g.getFontMetrics();
        int titleW = fm.stringWidth(title);
        int titleX = (w - titleW) / 2;

        g.setColor(new Color(46, 204, 113, 60));
        for (int i = 8; i > 0; i -= 2) {
            g.setColor(new Color(46, 204, 113, 14));
            g.drawString(title, titleX - i, titleY);
            g.drawString(title, titleX + i, titleY);
            g.drawString(title, titleX, titleY - i);
            g.drawString(title, titleX, titleY + i);
        }

        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(title, titleX + 4, titleY + 4);
        g.setColor(new Color(30, 80, 45));
        g.drawString(title, titleX - 2, titleY - 2);
        g.setColor(new Color(46, 204, 113));
        g.drawString(title, titleX, titleY);

        Shape oldClip = g.getClip();
        g.setClip(titleX, titleY - fm.getAscent(), titleW, fm.getAscent() + fm.getDescent());
        float sweep = ((t * 0.35f) % 1f) * (titleW + 200) - 100;
        GradientPaint shine = new GradientPaint(
                titleX + sweep - 60, 0, new Color(255, 255, 255, 0),
                titleX + sweep, 0, new Color(255, 255, 255, 120),
                false
        );
        GradientPaint shine2 = new GradientPaint(
                titleX + sweep, 0, new Color(255, 255, 255, 120),
                titleX + sweep + 60, 0, new Color(255, 255, 255, 0),
                false
        );
        g.setPaint(shine);
        g.drawString(title, titleX, titleY);
        g.setPaint(shine2);
        g.drawString(title, titleX, titleY);
        g.setClip(oldClip);

        g.setTransform(old);

        int underlineW = (int) (titleW * (0.6f + 0.3f * (float) Math.sin(t * 1.2)));
        int ux = (w - underlineW) / 2;
        int uy = titleY + 18;
        GradientPaint underline = new GradientPaint(
                ux, uy, new Color(46, 204, 113, 0),
                ux + underlineW / 2f, uy, new Color(46, 204, 113, 220),
                true
        );
        g.setPaint(underline);
        g.fillRect(ux, uy, underlineW, 2);
    }

    private void drawEnterPrompt(Graphics2D g, int w, int h, float t) {
        int alpha = (int) (130 + 125 * Math.sin(t * 4));
        alpha = Math.max(0, Math.min(255, alpha));
        float scale = 1f + 0.04f * (float) Math.sin(t * 4);

        AffineTransform old = g.getTransform();
        int y = h / 2 + 20;
        g.translate(w / 2f, y);
        g.scale(scale, scale);
        g.translate(-w / 2f, -y);

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(new Color(255, 255, 255, alpha));
        FontMetrics fm = g.getFontMetrics();
        String msg = "Pressione [ ENTER ] para Iniciar a Defesa";
        g.drawString(msg, (w - fm.stringWidth(msg)) / 2, y);

        g.setTransform(old);
    }

    private void drawCommandsCard(Graphics2D g, int w, int h) {
        int cardW = 460;
        int cardH = 100;
        int cardX = (w - cardW) / 2;
        int cardY = h - cardH - 60;

        g.setColor(new Color(20, 30, 24, 200));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 12, 12);
        g.setColor(new Color(55, 85, 65, 180));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(new Color(130, 160, 140));
        drawCentered(g, "GUIA RÁPIDO DE COMANDOS", w, cardY + 22);

        g.setFont(new Font("Arial", Font.PLAIN, 13));
        g.setColor(new Color(210, 220, 215));
        drawCentered(g, "• [Clique Esquerdo] Construir a Defesa Selecionada", w, cardY + 46);
        drawCentered(g, "• [T] [A] [S] [B] Alternar Torres  |  [R] Mostrar Alcances", w, cardY + 66);
        drawCentered(g, "• [Espaço] Próxima Onda  |  [C] Curiosidades  |  [ESC] Sair", w, cardY + 86);
    }

    private void drawFooter(Graphics2D g, int w, int h) {
        g.setFont(new Font("Arial", Font.ITALIC, 12));
        g.setColor(new Color(90, 115, 100));
        drawCentered(g, "Projeto APS — UNIP 2026", w, h - 25);
    }

    private void drawVignette(Graphics2D g, int w, int h) {
        RadialGradientPaint vignette = new RadialGradientPaint(
                w / 2f, h / 2f, Math.max(w, h) * 0.75f,
                new float[]{0.55f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 180)}
        );
        g.setPaint(vignette);
        g.fillRect(0, 0, w, h);
    }

    private void drawCentered(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private static class Firefly {
        float x, y, vx, vy, radius, phase;
        Firefly(float x, float y, float vx, float vy, float radius, float phase) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
            this.radius = radius; this.phase = phase;
        }
    }

    private static class Leaf {
        float x, y, fallSpeed, rotation, spin, swayAmp, swayPhase;
        Leaf(float x, float y, float fallSpeed, float rotation, float spin, float swayAmp) {
            this.x = x; this.y = y; this.fallSpeed = fallSpeed;
            this.rotation = rotation; this.spin = spin; this.swayAmp = swayAmp;
            this.swayPhase = rotation;
        }
    }
}
