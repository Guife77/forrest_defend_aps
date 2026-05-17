package game.animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Escavadeira {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private int totalFrames;
    private long lastTime;
    private int speed = 120; // ms por quadro

    private boolean facingLeft = false;

    // Tamanho de desenho calibrado pela proporção real da escavadeira (sem achatar)
    private final int DRAW_W = 36;
    private final int DRAW_H = 33;

    public Escavadeira(String baseName, int frameCount) {
        this.totalFrames = frameCount;
        frames = new BufferedImage[frameCount];
        loadFrames(baseName);
        lastTime = System.currentTimeMillis();
    }

    private void loadFrames(String baseName) {
        for (int i = 0; i < totalFrames; i++) {
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
                    if (f.exists()) { img = ImageIO.read(f); break; }
                } catch (IOException ignored) {}
            }
            frames[i] = (img != null) ? img : new BufferedImage(DRAW_W, DRAW_H, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public void setDirection(String direction) {
        facingLeft = direction.equalsIgnoreCase("esquerda");
    }

    public void update() {
        long now = System.currentTimeMillis();
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

        if (facingLeft) {
            // Flip horizontal: inverte o destino X sem criar BufferedImage extra
            g.drawImage(img,
                destX + DRAW_W, destY,   // dest x1 = direita
                destX,          destY + DRAW_H, // dest x2 = esquerda (negativo = espelha)
                srcX, srcY, srcX + srcW, srcY + srcH,
                null);
        } else {
            g.drawImage(img,
                destX, destY, destX + DRAW_W, destY + DRAW_H,
                srcX, srcY, srcX + srcW, srcY + srcH,
                null);
        }
    }

    public int getDrawWidth()  { return DRAW_W; }
    public int getDrawHeight() { return DRAW_H; }
}