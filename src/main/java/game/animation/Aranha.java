package game.animation;

import game.utils.GameClock;

import game.utils.Assets;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Aranha {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private long lastTime;
    private int speed = 180;

    private boolean attacking = false;
    private int attackTickCount = 0;

    private final int DRAW_SIZE = 48;

    public Aranha(String baseName) {
        frames = new BufferedImage[4];
        loadFrames(baseName);
        lastTime = GameClock.now();
    }

    private void loadFrames(String baseName) {
        for (int i = 0; i < 4; i++) {
            BufferedImage img = Assets.loadImage("public/" + baseName + (i + 1) + ".png");
            frames[i] = (img != null) ? img : new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public void playAttack() {
        if (!attacking) {
            attacking = true;
            currentFrame = 2;
            attackTickCount = 0;
            lastTime = GameClock.now();
        }
    }

    public void update() {
        long now = GameClock.now();
        if (now - lastTime > speed) {
            if (attacking) {
                currentFrame = (currentFrame == 2) ? 3 : 2;
                attackTickCount++;
                if (attackTickCount >= 3) {
                    attacking = false;
                    currentFrame = 0;
                }
            } else {
                currentFrame = (currentFrame + 1) % 2;
            }
            lastTime = now;
        }
    }

    public void render(Graphics g, int x, int y) {
        BufferedImage img = frames[currentFrame];
        if (img == null) return;

        long t = GameClock.now();
        // Tremor lateral leve no idle (aranha "balança")
        int sway = attacking ? 0 : (int) (Math.sin(t * 0.012) * 2);
        // Escala +20% no ataque
        float scale = attacking ? 1.2f : 1.0f;
        int size = (int) (DRAW_SIZE * scale);

        int destX = x - size / 2 + sway;
        int destY = y - size / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        // Sombra chão
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(x - size / 3, y + size / 2 - 4, (size * 2) / 3, size / 5);

        g2.drawImage(img, destX, destY, destX + size, destY + size,
                    0, 0, img.getWidth(), img.getHeight(), null);
        g2.dispose();
    }
}
