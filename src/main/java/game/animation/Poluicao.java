package game.animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Sprite estático da poluição com flutuação suave (bobbing) e leve pulso de opacidade.
 * Usa o arquivo poluicao.png como única imagem.
 */
public class Poluicao {

    private BufferedImage img;
    private final int DRAW_W = 38;
    private final int DRAW_H = 38;
    private final long birth = System.currentTimeMillis();

    public Poluicao() {
        String[] candidates = {
                "poluicao.png",
                "src/main/resources/public/poluicao.png",
                "src/public/poluicao.png",
                "public/poluicao.png"
        };
        for (String path : candidates) {
            try {
                File f = new File(path);
                if (f.exists()) { img = ImageIO.read(f); break; }
            } catch (IOException ignored) {}
        }
    }

    public void render(Graphics g, int x, int y) {
        if (img == null) return;
        Graphics2D g2 = (Graphics2D) g.create();
        long t = System.currentTimeMillis() - birth;
        int bob = (int) (Math.sin(t * 0.005) * 3);
        float alpha = 0.85f + 0.15f * (float) Math.sin(t * 0.006);
        g2.setComposite(java.awt.AlphaComposite.getInstance(
                java.awt.AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));
        int destX = x - DRAW_W / 2;
        int destY = y - DRAW_H / 2 + bob;
        g2.drawImage(img, destX, destY, destX + DRAW_W, destY + DRAW_H,
                     0, 0, img.getWidth(), img.getHeight(), null);
        g2.dispose();
    }
}
