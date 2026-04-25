package game.animation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Arara {

    private BufferedImage[] frames;
    private int currentFrame = 0;
    private int totalFrames;
    private long lastTime;
    private int speed = 120; // ms por frame

    public Arara(String fileName, int frameCount) {
        this.totalFrames = frameCount;
        load(fileName);
        lastTime = System.currentTimeMillis();
    }

    public Arara (String fileName) {
        this(fileName, 4);
    }

    private void load(String fileName) {
        String[] candidates = {
                fileName,
                "forrest_defend/" + fileName,
                "forrest_defend/public/" + fileName,
                "public/" + fileName
        };

        BufferedImage sheet = null;
        for (String path : candidates) {
            try {
                File f = new File(path);
                if (f.exists()) { sheet = ImageIO.read(f); break; }
            } catch (IOException ignored) {}
        }

        frames = new BufferedImage[totalFrames];

        if (sheet == null) {
            // Placeholder animado: aro azul pulsando
            for (int i = 0; i < totalFrames; i++) {
                frames[i] = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = frames[i].createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int alpha = 120 + i * 35;
                g.setColor(new Color(60, 130, 220, Math.min(alpha, 255)));
                g.fillOval(1, 1, 26, 26);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString("A", 9, 19);
                g.dispose();
            }
            return;
        }

        int w = sheet.getWidth() / totalFrames;
        int h = sheet.getHeight();
        for (int i = 0; i < totalFrames; i++) {
            frames[i] = sheet.getSubimage(i * w, 0, w, h);
        }
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastTime > speed) {
            currentFrame = (currentFrame + 1) % totalFrames;
            lastTime = now;
        }
    }

    public void render(Graphics g, int x, int y) {
        if (frames != null && frames[currentFrame] != null)
            g.drawImage(frames[currentFrame], x, y, null);
    }

    public int getFrameWidth()  { return frames != null && frames[0] != null ? frames[0].getWidth()  : 28; }
    public int getFrameHeight() { return frames != null && frames[0] != null ? frames[0].getHeight() : 28; }
}