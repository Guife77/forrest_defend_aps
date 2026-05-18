package game.animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Casa principal — renderiza Casa_Principal.png como a base do jogo.
 * Tamanho de desenho ampliado (ocupa ~3×3 tiles) e ancorado no centro do tile da base.
 */
public class CasaPrincipal {

    private static BufferedImage img;
    private static boolean loaded = false;

    private static void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        String[] candidates = {
                "Casa_Principal.png",
                "src/main/resources/public/Casa_Principal.png",
                "src/public/Casa_Principal.png",
                "public/Casa_Principal.png"
        };
        for (String path : candidates) {
            try {
                File f = new File(path);
                if (f.exists()) { img = ImageIO.read(f); return; }
            } catch (IOException ignored) {}
        }
        System.out.println("[CasaPrincipal] Imagem não encontrada");
    }

    /**
     * Desenha a casa centrada nas coordenadas (cx, cy) do tile da base.
     * O tamanho efetivo é grande o suficiente para "transbordar" alguns tiles, dando peso visual à base.
     */
    public static void render(Graphics2D g, int cx, int cy, int tileSize) {
        ensureLoaded();
        if (img == null) return;

        // Casa 5×5 tiles centrada no BASE. Visualmente domina o fim do caminho.
        int drawW = tileSize * 5;
        int drawH = tileSize * 5;
        int x = cx - drawW / 2;
        // Levanta um pouco para que o "chão" da casa (base do PNG) caia no centro do tile,
        // dando sensação de que ela está plantada no final do caminho.
        int y = cy - drawH * 3 / 4;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        // Sombra elíptica chão
        int shadowW = drawW * 3 / 4;
        int shadowH = tileSize / 3;
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillOval(cx - shadowW / 2, cy + tileSize / 3 - shadowH / 2, shadowW, shadowH);

        // Aura sutil dourada atrás da casa, indicando que é o objetivo
        int auraR = drawW;
        java.awt.RadialGradientPaint aura = new java.awt.RadialGradientPaint(
                cx, cy, auraR,
                new float[]{0f, 1f},
                new Color[]{new Color(255, 220, 130, 60), new Color(255, 220, 130, 0)}
        );
        g2.setPaint(aura);
        g2.fillOval(cx - auraR, cy - auraR, auraR * 2, auraR * 2);

        g2.drawImage(img, x, y, x + drawW, y + drawH,
                     0, 0, img.getWidth(), img.getHeight(), null);
        g2.dispose();
    }
}
