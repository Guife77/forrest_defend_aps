package core;

import java.awt.Canvas;
import java.awt.Dimension;

public class GameCanvas extends Canvas {

    public GameCanvas(int width, int height){
        setPreferredSize(new Dimension(width, height));
    }

    public void init(){
        createBufferStrategy(2);
    }

}
