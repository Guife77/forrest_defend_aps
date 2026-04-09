package game.engine;

public class GameLoop implements Runnable {

    private boolean running = true;

    @Override
    public void run() {
        while (running) {
            update();
            render();
        }
    }

    private void update() {}
    private void render() {}
}
