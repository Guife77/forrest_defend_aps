package game.core;

import game.application.Game;

public class GameLoop implements Runnable {

    private static final double TARGET_FPS = 60.0;
    private static final double TIME_PER_TICK = 1_000_000_000.0 / TARGET_FPS;

    private boolean running = false;
    private Thread thread;
    private Game game;

    public GameLoop(Game game) {
        this.game = game;
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "GameLoop");
        thread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / TIME_PER_TICK;
            lastTime = now;

            while (delta >= 1) {
                game.update();
                delta--;
            }

            game.render();
        }
    }
}
