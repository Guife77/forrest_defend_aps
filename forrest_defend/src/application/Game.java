package application;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import core.GameLoop;
import core.GameWindow;

public class Game {

    private GameWindow window;
    private GameLoop loop;
    private int x = 100;

    public Game(){
        window = new GameWindow("Tower Defense", 800, 600);
        loop = new GameLoop(this);
        loop.start();
    }

    public void update(){
        x++;
        if (x > 800) x = -50;
    }

    public void render(){
        BufferStrategy bs = window.getCanvas().getBufferStrategy();
        if(bs == null) return;

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 800, 600);

        g.setColor(Color.ORANGE);
        g.fillRect(x, 100, 50, 50);

        g.dispose();
        bs.show();
    }

    public static void main(String[] args){

        new Game();
         
    }


}
