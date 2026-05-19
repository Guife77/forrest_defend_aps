package game.animation;

import game.utils.GameClock;

import game.utils.Assets;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Arara {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private long lastTime;
    private int speed = 150; // ms por quadro

    private boolean attacking = false;
    private int attackTickCount = 0;

    private final int DRAW_SIZE = 44;

    public Arara(String baseName) {
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
                currentFrame++;
                if (currentFrame > 3) currentFrame = 2;
                
                attackTickCount++;
                if (attackTickCount >= 2) {
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
        BufferedImage currentImage = frames[currentFrame];
        if (currentImage == null) return;

        int imgW = currentImage.getWidth();
        int imgH = currentImage.getHeight();

        int srcX, srcY, srcW, srcH;

        if (currentFrame == 0) {
            srcX = (int) (imgW * 0.0625);
            srcY = (int) (imgH * 0.3582);
            srcW = (int) (imgW * 0.1836);
            srcH = (int) (imgH * 0.2364);
        } else if (currentFrame == 1) {
            srcX = (int) (imgW * 0.3047);
            srcY = (int) (imgH * 0.3625);
            srcW = (int) (imgW * 0.1656);
            srcH = (int) (imgH * 0.2851);
        } else {
            srcX = 0; srcY = 0; srcW = imgW; srcH = imgH;
        }

        // Bobbing leve no idle
        long t = GameClock.now();
        int bob = attacking ? 0 : (int) (Math.sin(t * 0.005) * 2);
        // Escala leve no ataque (golpe)
        float scale = attacking ? 1.15f : 1.0f;
        int size = (int) (DRAW_SIZE * scale);

        int destX = x - size / 2;
        int destY = y - size / 2 + bob;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        // Sombra elíptica chão
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillOval(x - size / 3, y + size / 2 - 4, (size * 2) / 3, size / 5);

        g2.drawImage(currentImage,
                    destX, destY, destX + size, destY + size,
                    srcX, srcY, srcX + srcW, srcY + srcH,
                    null);
        g2.dispose();
    }

    public int getFrameWidth() { return DRAW_SIZE; }
    public int getFrameHeight() { return DRAW_SIZE; }
}