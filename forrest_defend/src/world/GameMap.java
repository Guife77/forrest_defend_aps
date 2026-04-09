package world;

public class GameMap {

    private int rows;
    private int cols;
    private Tile tile[][];

    public GameMap(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        tile = new Tile[rows][cols];
    }

    public int getRows(){
        return rows;
    }

    public int getCols(){
        return cols;
    }

    public Tile getTile(int row, int col){
        return this.tile[row][col];
    }

}
