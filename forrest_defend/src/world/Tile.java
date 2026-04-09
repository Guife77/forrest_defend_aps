package world;

import world.enums.TileType;

public class Tile {

    private int x;
    private int y;
    private TileType type;

    public Tile(int x, int y, TileType type){
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public TileType getType() {
        return type;
    }

}
