package game.animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Árvore Açaçu (Hura crepitans) — defensora animada.
 *
 * Estados:
 *   • Idle  → acacu1.png (árvore tranquila com folhagem)
 *   • Ataque → acacu2.png (vira monstro com cara feroz, brevemente)
 *
 * A transição idle→attack→idle dura ~400 ms e dispara em {@link #playAttack()}.
 * Inclui pequeno tremor (shake) durante o ataque pra dar peso visual.
 */
public class Acacu {

    private BufferedImage idleFrame;
    private BufferedImage attackFrame;

    private boolean attacking = false;
    private long attackStart = 0;
    private static final long ATTACK_DURATION_MS = 420;

    private final int DRAW_W = 64;
    private final int DRAW_H = 80;
    private final long birth = System.currentTimeMillis();

    public Acacu() {
        idleFrame   = loadFrame("acacu1.png");
        attackFrame = loadFrame("acacu2.png");
    }

    private BufferedImage loadFrame(String fileName) {
        String[] candidates = {
                fileName,
                "src/main/resources/public/" + fileName,
                "src/public/" + fileName,
                "public/" + fileName
        };
        for (String path : candidates) {
            try {
                File f = new File(path);
                if (f.exists()) return ImageIO.read(f);
            } catch (IOException ignored) {}
        }
        return null;
    }

    public void playAttack() {
        attacking = true;
        attackStart = System.currentTimeMillis();
    }

    public void update() {
        if (attacking && System.currentTimeMillis() - attackStart > ATTACK_DURATION_MS) {
            attacking = false;
        }
    }

    public void render(Graphics2D g, int cx, int cy) {
        BufferedImage frame = attacking ? attackFrame : idleFrame;
        if (frame == null) return;

        long now = System.currentTimeMillis();
        long age = now - birth;

        // Sway lento no idle (oscilação lateral suave)
        int swayX = attacking ? 0 : (int) (Math.sin(age * 0.002) * 1);

        // Shake durante o ataque
        int shakeX = 0, shakeY = 0;
        if (attacking) {
            long att = now - attackStart;
            float intensity = 1f - (att / (float) ATTACK_DURATION_MS);
            shakeX = (int) (Math.sin(att * 0.08) * 3 * intensity);
            shakeY = (int) (Math.cos(att * 0.09) * 2 * intensity);
        }

        // Bobbing leve no idle (respiração)
        int bob = attacking ? 0 : (int) (Math.sin(age * 0.003) * 1.5);

        int destX = cx - DRAW_W / 2 + swayX + shakeX;
        int destY = cy - DRAW_H / 2 + bob + shakeY - DRAW_H / 6; // sobe um pouco pra base ficar no centro do tile

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        // Sombra elíptica chão (sob o tronco)
        g2.setColor(new Color(0, 0, 0, 110));
        g2.fillOval(cx - DRAW_W / 3, cy + DRAW_H / 3 - 6, DRAW_W * 2 / 3, DRAW_H / 8);

        // Halo de raiva durante o ataque
        if (attacking) {
            float t = (now - attackStart) / (float) ATTACK_DURATION_MS;
            int alpha = (int)((1 - t) * 100);
            g2.setColor(new Color(255, 80, 40, Math.max(0, alpha)));
            int r = (int)(DRAW_W * 0.7 * (1 + t * 0.3));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        }

        g2.drawImage(frame, destX, destY, destX + DRAW_W, destY + DRAW_H,
                     0, 0, frame.getWidth(), frame.getHeight(), null);
        g2.dispose();
    }
}
