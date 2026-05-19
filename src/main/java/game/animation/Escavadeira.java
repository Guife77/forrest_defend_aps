package game.animation;

import game.utils.GameClock;

import game.utils.Assets;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Escavadeira {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private int totalFrames;
    private long lastTime;
    private int speed = 120; // ms por quadro

    private boolean facingLeft = false;

    // Tamanho de desenho calibrado pela proporção real da escavadeira (sem achatar)
    private final int DRAW_W = 46;
    private final int DRAW_H = 42;

    public Escavadeira(String baseName, int frameCount) {
        this.totalFrames = frameCount;
        frames = new BufferedImage[frameCount];
        loadFrames(baseName);
        lastTime = GameClock.now();
    }

    private void loadFrames(String baseName) {
        for (int i = 0; i < totalFrames; i++) {
            BufferedImage img = Assets.loadImage("public/" + baseName + (i + 1) + ".png");
            frames[i] = (img != null) ? img : new BufferedImage(DRAW_W, DRAW_H, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public void setDirection(String direction) {
        facingLeft = direction.equalsIgnoreCase("esquerda");
    }

    public void update() {
        long now = GameClock.now();
        if (now - lastTime > speed) {
            currentFrame = (currentFrame + 1) % totalFrames;
            lastTime = now;
        }
    }

    public void render(Graphics g, int x, int y) {
        BufferedImage img = frames[currentFrame];
        if (img == null) return;

        int imgW = img.getWidth();   // sempre 169
        int imgH = img.getHeight();  // sempre 369

        // Crop exato medido por pixel para cada frame (remove fundo preto sobrando)
        int srcX, srcY, srcW, srcH;
        switch (currentFrame) {
            case 0: // escavadeira1: bbox=(23,120)..(168,254)
                srcX = (int)(imgW * 0.1361);
                srcY = (int)(imgH * 0.3252);
                srcW = (int)(imgW * 0.8580);
                srcH = (int)(imgH * 0.3631);
                break;
            case 1: // escavadeira2: bbox=(0,138)..(168,254)
                srcX = (int)(imgW * 0.0000);
                srcY = (int)(imgH * 0.3740);
                srcW = (int)(imgW * 0.9941);
                srcH = (int)(imgH * 0.3144);
                break;
            case 2: // escavadeira3: bbox=(0,143)..(167,254)
                srcX = (int)(imgW * 0.0000);
                srcY = (int)(imgH * 0.3875);
                srcW = (int)(imgW * 0.9882);
                srcH = (int)(imgH * 0.3008);
                break;
            default: // escavadeira4: bbox=(11,117)..(164,254)
                srcX = (int)(imgW * 0.0651);
                srcY = (int)(imgH * 0.3171);
                srcW = (int)(imgW * 0.9053);
                srcH = (int)(imgH * 0.3713);
                break;
        }

        int destX = x - (DRAW_W / 2);
        int destY = y - (DRAW_H / 2);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        // Sombra
        g2.setColor(new Color(0, 0, 0, 95));
        g2.fillOval(x - DRAW_W / 3, y + DRAW_H / 2 - 4, (DRAW_W * 2) / 3, DRAW_H / 5);

        if (facingLeft) {
            g2.drawImage(img,
                destX + DRAW_W, destY,
                destX,          destY + DRAW_H,
                srcX, srcY, srcX + srcW, srcY + srcH,
                null);
        } else {
            g2.drawImage(img,
                destX, destY, destX + DRAW_W, destY + DRAW_H,
                srcX, srcY, srcX + srcW, srcY + srcH,
                null);
        }
        g2.dispose();
    }

    public int getDrawWidth()  { return DRAW_W; }
    public int getDrawHeight() { return DRAW_H; }
}