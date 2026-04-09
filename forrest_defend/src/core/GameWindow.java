package core;

import javax.swing.JFrame;

public class GameWindow {

    private final JFrame frame;
    private final GameCanvas canvas;

    public GameWindow(String title, int width, int height){
        canvas = new GameCanvas(width, height);

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        canvas.init();
    }

    public GameCanvas getCanvas(){
        return canvas;
    }

}
