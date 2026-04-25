package animations;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class animation_arara {
    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private int currentFrame = 0;
    private int totalFrames = 4;

    private long lastTime, timer;
    private int speed = 120;

    public animation_arara(String fileName) {
        try {
            File file = new File(fileName);

            // Se não encontrar no caminho padrão, tenta com o prefixo do projeto
            if (!file.exists()) {
                file = new File("forrest_defend/" + fileName);
            }

            if (!file.exists()) {
                System.err.println("ERRO: Arquivo não encontrado em: " + file.getAbsolutePath());
                throw new IOException("Não foi possível encontrar o arquivo: " + fileName);
            }

            spriteSheet = ImageIO.read(file);

            if (spriteSheet == null) {
                throw new IOException("Arquivo de imagem inválido: " + fileName);
            }

            frames = new BufferedImage[totalFrames];
            int widthPerFrame = spriteSheet.getWidth() / totalFrames;
            int height = spriteSheet.getHeight();

            for (int i = 0; i < totalFrames; i++) {
                frames[i] = spriteSheet.getSubimage(i * widthPerFrame, 0, widthPerFrame, height);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem: " + fileName);
            e.printStackTrace();
        }
        lastTime = System.currentTimeMillis();
    }

    public int getFrameWidth() {
        if (frames != null && frames[0] != null) return frames[0].getWidth();
        return 0;
    }

    public int getFrameHeight() {
        if (frames != null && frames[0] != null) return frames[0].getHeight();
        return 0;
    }

    public void update() {
        long now = System.currentTimeMillis();
        timer += now - lastTime;
        lastTime = now;

        if (timer > speed) {
            currentFrame++;
            timer = 0;
            if (currentFrame >= totalFrames) {
                currentFrame = 0;
            }
        }
    }

    public void render(Graphics g, int x, int y) {
        if (frames != null && frames[currentFrame] != null) {
            g.drawImage(frames[currentFrame], x, y, null);
        }
    }
}