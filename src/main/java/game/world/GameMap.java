package game.world;

public class GameMap {

    private int rows, cols;
    private Tile[][] tiles;

    public GameMap(int rows, int cols) {
        this.rows  = rows;
        this.cols  = cols;
        this.tiles = new Tile[rows][cols];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public Tile getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return null;
        return tiles[row][col];
    }

    public void setTile(int row, int col, Tile tile) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return;
        tiles[row][col] = tile;
    }
}