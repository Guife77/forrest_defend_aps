package animations;

import java.awt.*;

public class Macaco {
    private final animation_arara animation;

    public Macaco(String baseName, int frameCount) {
        animation = new animation_arara(baseName, frameCount);
    }

    public void update() {
        animation.update();
    }

    public void render(Graphics g, int x, int y) {
        animation.render(g, x, y);
    }

    public int getFrameWidth() {
        return animation.getFrameWidth();
    }

    public int getFrameHeight() {
        return animation.getFrameHeight();
    }
}
