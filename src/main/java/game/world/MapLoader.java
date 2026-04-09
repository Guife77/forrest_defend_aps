package game.world;

import game.world.enums.TileType;

public class MapLoader {

    private int[][] design = {
        {0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 1, 1, 1, 1, 2, 0},
        {0, 0, 0, 0, 0, 0, 0, 0},
    };

    public void load(GameMap map) {
        for (int i = 0; i < design.length; i++) {
            for (int j = 0; j < design[0].length; j++) {

                TileType type;
                switch (design[i][j]) {
                    case 1:
                        type = TileType.PATH;
                        break;
                    case 2:
                        type = TileType.BASE;
                        break;
                    default:
                        type = TileType.GRASS;
                        break;
                }

                Tile tile = new Tile(j, i, type);
                map.setTile(i, j, tile);
            }
        }
    }
}