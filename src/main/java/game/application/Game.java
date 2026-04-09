package game.application;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import game.core.GameLoop;
import game.core.GameWindow;
import game.world.GameMap;
import game.world.MapLoader;
import game.world.Tile;
import game.world.enums.TileType;

public class Game {

    private GameWindow window;
    private GameLoop loop;
    private int x = 100;
    private GameMap map;
    private MapLoader loader;

    public Game(){
        window = new GameWindow("Tower Defense", 800, 600);
        loop = new GameLoop(this);
        map = new GameMap(5, 8);
        loader = new MapLoader();
        loader.load(map);
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

        int tileSize = 80;

        for (int i = 0; i < map.getRows(); i++) {
            for (int j = 0; j < map.getCols(); j++) {
                Tile tile = map.getTile(i, j);

                if (tile.getType() == TileType.GRASS) {
                    g.setColor(new Color(34, 139, 34));
                } else if (tile.getType() == TileType.PATH) {
                    g.setColor(new Color(194, 178, 128));
                } else {
                    g.setColor(new Color(220, 50, 50));
                }

                g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
            }
        }
        g.dispose();
        bs.show();
    }

    public static void main(String[] args){

        new Game();
         
    }


}
