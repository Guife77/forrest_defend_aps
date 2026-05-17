package game.animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Arara {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private long lastTime;
    private int speed = 150; // ms por quadro

    private boolean attacking = false;
    private int attackTickCount = 0;

    // ── ALTERAÇÃO AQUI: Diminuído de 48 para 36 para ficar mais compacta no mapa ──
    private final int DRAW_SIZE = 36; 

    public Arara(String baseName) {
        frames = new BufferedImage[4];
        loadFrames(baseName);
        lastTime = System.currentTimeMillis();
    }

    private void loadFrames(String baseName) {
        for (int i = 0; i < 4; i++) {
            String fileName = baseName + (i + 1) + ".png";
            String[] candidates = {
                    fileName,
                    "src/main/resources/public/" + fileName,
                    "public/" + fileName
            };

            BufferedImage img = null;
            for (String path : candidates) {
                try {
                    File f = new File(path);
                    if (f.exists()) { 
                        img = ImageIO.read(f); 
                        break; 
                    }
                } catch (IOException ignored) {}
            }

            if (img != null) {
                frames[i] = img;
            } else {
                frames[i] = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            }
        }
    }

    public void playAttack() {
        if (!attacking) {
            attacking = true;
            currentFrame = 2;
            attackTickCount = 0;
            lastTime = System.currentTimeMillis();
        }
    }

    public void update() {
        long now = System.currentTimeMillis();
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
            // ARARA 1
            srcX = (int) (imgW * 0.0625);
            srcY = (int) (imgH * 0.3582);
            srcW = (int) (imgW * 0.1836);
            srcH = (int) (imgH * 0.2364);
        } else if (currentFrame == 1) {
            // ARARA 2
            srcX = (int) (imgW * 0.3047);
            srcY = (int) (imgH * 0.3625);
            srcW = (int) (imgW * 0.1656);
            srcH = (int) (imgH * 0.2851);
        } else {
            // Frames de ataque
            srcX = 0; srcY = 0; srcW = imgW; srcH = imgH;
        }

        int destX = x - (DRAW_SIZE / 2);
        int destY = y - (DRAW_SIZE / 2);

        g.drawImage(currentImage, 
                    destX, destY, destX + DRAW_SIZE, destY + DRAW_SIZE, 
                    srcX, srcY, srcX + srcW, srcY + srcH, 
                    null);
    }

    public int getFrameWidth() { return DRAW_SIZE; }
    public int getFrameHeight() { return DRAW_SIZE; }
}